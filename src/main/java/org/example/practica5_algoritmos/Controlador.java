package org.example.practica5_algoritmos;

//package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

public class Controlador {
    private final DataManager dataManager;
    private ObservableList<VideoGame> observableData;
    private TableView<VideoGame> tableView;
    private ComboBox<String> columnCombo;
    private ComboBox<String> algorithmCombo;
    private Label timeLabel;
    private BarChart<String, Number> chart;
    private Map<String, Long> lastTimes; // para la gráfica

    public Controlador() {
        dataManager = new DataManager();
        lastTimes = new HashMap<>();
        buildUI();
    }

    private void buildUI() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // --- Tabla ---
        tableView = new TableView<>();
        observableData = FXCollections.observableArrayList(dataManager.getOriginalList());
        tableView.setItems(observableData);
        createTableColumns();

        // --- Panel de controles ---
        HBox controls = new HBox(15);
        controls.setPadding(new Insets(10));

        // Columna a ordenar
        columnCombo = new ComboBox<>();
        columnCombo.getItems().addAll("Name", "Platform", "Year", "Genre", "Publisher",
                "NA_Sales", "EU_Sales", "JP_Sales", "Other_Sales", "Global_Sales");
        columnCombo.setValue("Global_Sales");

        // Algoritmo
        algorithmCombo = new ComboBox<>();
        algorithmCombo.getItems().addAll("Quicksort", "Mergesort", "Shell sort", "Selection sort",
                "Radix sort", "Arrays.sort()", "Arrays.parallelSort()");
        algorithmCombo.setValue("Quicksort");

        Button sortBtn = new Button("Ordenar");
        sortBtn.setOnAction(e -> performSort());

        Button benchmarkAllBtn = new Button("Comparar todos (gráfica)");
        benchmarkAllBtn.setOnAction(e -> benchmarkAllAlgorithms());

        timeLabel = new Label("Tiempo: -- ns");
        timeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: darkblue;");

        controls.getChildren().addAll(new Label("Columna:"), columnCombo,
                new Label("Algoritmo:"), algorithmCombo,
                sortBtn, benchmarkAllBtn, timeLabel);

        // --- Gráfica ---
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Nanosegundos");
        chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Comparación de tiempos de ordenamiento");
        chart.setPrefHeight(400);

        VBox centerBox = new VBox(10, tableView, chart);
        root.setTop(controls);
        root.setCenter(centerBox);

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/app/resources/style.css").toExternalForm());

        Stage stage = new Stage();
        stage.setTitle("Análisis de Algoritmos de Ordenamiento - Videojuegos");
        stage.setScene(scene);
        stage.show();
    }

    private void createTableColumns() {
        String[] cols = {"Name", "Platform", "Year", "Genre", "Publisher",
                "NA_Sales", "EU_Sales", "JP_Sales", "Other_Sales", "Global_Sales"};
        for (String col : cols) {
            TableColumn<VideoGame, ?> column = new TableColumn<>(col);
            switch (col) {
                case "Name": column.setCellValueFactory(c -> c.getValue().nameProperty()); break;
                case "Platform": column.setCellValueFactory(c -> c.getValue().platformProperty()); break;
                case "Year": column.setCellValueFactory(c -> c.getValue().yearProperty().asObject()); break;
                case "Genre": column.setCellValueFactory(c -> c.getValue().genreProperty()); break;
                case "Publisher": column.setCellValueFactory(c -> c.getValue().publisherProperty()); break;
                case "NA_Sales": column.setCellValueFactory(c -> c.getValue().naSalesProperty().asObject()); break;
                case "EU_Sales": column.setCellValueFactory(c -> c.getValue().euSalesProperty().asObject()); break;
                case "JP_Sales": column.setCellValueFactory(c -> c.getValue().jpSalesProperty().asObject()); break;
                case "Other_Sales": column.setCellValueFactory(c -> c.getValue().otherSalesProperty().asObject()); break;
                case "Global_Sales": column.setCellValueFactory(c -> c.getValue().globalSalesProperty().asObject()); break;
            }
            tableView.getColumns().add(column);
        }
    }

    private void performSort() {
        String column = columnCombo.getValue();
        String algorithm = algorithmCombo.getValue();

        // Obtener copia actual de los datos (la tabla está mostrando una lista, usaremos su estado actual)
        List<VideoGame> currentList = new ArrayList<>(tableView.getItems());
        VideoGame[] array = currentList.toArray(new VideoGame[0]);

        Comparator<VideoGame> comparator = getComparatorForColumn(column);
        if (comparator == null) {
            timeLabel.setText("Error: columna no soportada para este algoritmo.");
            return;
        }

        long time;
        try {
            if (algorithm.equals("Radix sort")) {
                // Radix sort requiere extraer int (solo funciona para Year)
                if (!column.equals("Year")) {
                    timeLabel.setText("Radix sort sólo funciona para la columna Year (entero).");
                    return;
                }
                time = SortBenchmark.measureRadixTime(array, VideoGame::getYear);
            } else {
                time = SortBenchmark.measureTime(array, comparator, algorithm);
            }
        } catch (Exception e) {
            timeLabel.setText("Error: " + e.getMessage());
            return;
        }

        // Actualizar la tabla con el array ordenado
        observableData.setAll(array);
        timeLabel.setText(String.format("Tiempo: %,d ns", time));
        lastTimes.put(algorithm + ":" + column, time);
    }

    private void benchmarkAllAlgorithms() {
        String column = columnCombo.getValue();
        Comparator<VideoGame> comparator = getComparatorForColumn(column);
        if (comparator == null) {
            timeLabel.setText("No se puede comparar: columna no soportada.");
            return;
        }

        List<String> algorithms = Arrays.asList("Quicksort", "Mergesort", "Shell sort", "Selection sort",
                "Arrays.sort()", "Arrays.parallelSort()");
        Map<String, Long> results = new LinkedHashMap<>();

        // Obtener una copia fresca de los datos originales
        List<VideoGame> freshList = dataManager.getOriginalList();

        for (String algo : algorithms) {
            VideoGame[] copy = freshList.toArray(new VideoGame[0]);
            long t = SortBenchmark.measureTime(copy, comparator, algo);
            results.put(algo, t);
        }

        // Radix sort solo para Year
        if (column.equals("Year")) {
            VideoGame[] copyRadix = freshList.toArray(new VideoGame[0]);
            long tRadix = SortBenchmark.measureRadixTime(copyRadix, VideoGame::getYear);
            results.put("Radix sort", tRadix);
        }

        lastTimes.clear();
        for (Map.Entry<String, Long> e : results.entrySet()) {
            lastTimes.put(e.getKey() + ":" + column, e.getValue());
        }

        updateChart(results);
        timeLabel.setText("Comparación completada.");
    }

    private void updateChart(Map<String, Long> times) {
        chart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tiempo (ns) para " + columnCombo.getValue());
        for (Map.Entry<String, Long> e : times.entrySet()) {
            series.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
        }
        chart.getData().add(series);
    }

    private Comparator<VideoGame> getComparatorForColumn(String column) {
        switch (column) {
            case "Name": return Comparator.comparing(VideoGame::getName, String.CASE_INSENSITIVE_ORDER);
            case "Platform": return Comparator.comparing(VideoGame::getPlatform);
            case "Year": return Comparator.comparingInt(VideoGame::getYear);
            case "Genre": return Comparator.comparing(VideoGame::getGenre);
            case "Publisher": return Comparator.comparing(VideoGame::getPublisher);
            case "NA_Sales": return Comparator.comparingDouble(VideoGame::getNaSales);
            case "EU_Sales": return Comparator.comparingDouble(VideoGame::getEuSales);
            case "JP_Sales": return Comparator.comparingDouble(VideoGame::getJpSales);
            case "Other_Sales": return Comparator.comparingDouble(VideoGame::getOtherSales);
            case "Global_Sales": return Comparator.comparingDouble(VideoGame::getGlobalSales);
            default: return null;
        }
    }
}