package org.example.practica5_algoritmos;

import javafx.application.Platform;
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
import java.util.function.Function;

public class Controlador {
    private final DataManager dataManager;
    private ObservableList<VideoGame> observableData;
    private TableView<VideoGame> tableView;
    private ComboBox<String> columnCombo;
    private ComboBox<String> algorithmCombo;
    private Label timeLabel;
    private BarChart<String, Number> chart;
    private List<VideoGame> originalListCache;

    // Columnas permitidas para ordenar (excluyendo Summary, Reviews, etc.)
    private final List<String> allowedColumns = Arrays.asList(
            "Title", "Release Date", "Rating", "Number of Reviews",
            "Times Listed", "Plays", "Playing", "Backlogs", "Wishlist"
    );

    public Controlador() {
        dataManager = new DataManager();
        originalListCache = dataManager.getOriginalList();
        observableData = FXCollections.observableArrayList(originalListCache);
        buildUI();
    }

    private void buildUI() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #f4f4f4;");

        // --- Tabla ---
        tableView = new TableView<>();
        tableView.setItems(observableData);
        createTableColumns();

        // --- Panel de controles ---
        HBox controls = new HBox(15);
        controls.setPadding(new Insets(10));
        controls.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-background-radius: 5;");

        columnCombo = new ComboBox<>();
        columnCombo.getItems().addAll(allowedColumns);
        columnCombo.setValue("Rating");

        algorithmCombo = new ComboBox<>();
        algorithmCombo.getItems().addAll("Quicksort", "Mergesort", "Shell sort", "Selection sort",
                "Radix sort", "Arrays.sort()", "Arrays.parallelSort()");
        algorithmCombo.setValue("Quicksort");

        // Deshabilitar Radix sort si la columna no es numérica entera
        columnCombo.setOnAction(e -> updateAlgorithmCombo());

        Button sortBtn = new Button("Ordenar");
        sortBtn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold;");
        sortBtn.setOnAction(e -> performSort());

        Button resetBtn = new Button("Restablecer");
        resetBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        resetBtn.setOnAction(e -> resetTable());

        Button benchmarkAllBtn = new Button("Comparar todos (gráfica)");
        benchmarkAllBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
        benchmarkAllBtn.setOnAction(e -> benchmarkAllAlgorithms());

        timeLabel = new Label("Tiempo: -- ns");
        timeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        controls.getChildren().addAll(new Label("Columna:"), columnCombo,
                new Label("Algoritmo:"), algorithmCombo,
                sortBtn, resetBtn, benchmarkAllBtn, timeLabel);

        // --- Gráfica ---
        //CategoryAxis xAxis = new CategoryAxis();
        //NumberAxis yAxis = new NumberAxis();
        //yAxis.setLabel("Nanosegundos");
        //chart = new BarChart<>(xAxis, yAxis);
        //chart.setTitle("Comparación de tiempos de ordenamiento");
        //chart.setPrefHeight(400);
        //chart.setStyle("-fx-background-color: white;");

        VBox centerBox = new VBox(10, tableView);
        root.setTop(controls);
        root.setCenter(centerBox);

        Scene scene = new Scene(root, 1200, 800);
        // Cargar CSS si existe (opcional)
        try {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS no encontrado, se usan estilos por defecto.");
        }

        Stage stage = new Stage();
        stage.setTitle("Análisis de Algoritmos de Ordenamiento - Videojuegos");
        stage.setScene(scene);
        stage.show();
    }

    private void updateAlgorithmCombo() {
        String col = columnCombo.getValue();
        boolean isIntegerColumn = isIntegerColumn(col);
        if (!isIntegerColumn && "Radix sort".equals(algorithmCombo.getValue())) {
            algorithmCombo.setValue("Quicksort");
        }
        // Opcional: deshabilitar visualmente Radix sort
        // No se puede deshabilitar fácilmente en ComboBox, pero se maneja en performSort
    }

    private boolean isIntegerColumn(String column) {
        return column.equals("Number of Reviews") ||
                column.equals("Times Listed") ||
                column.equals("Plays") ||
                column.equals("Playing") ||
                column.equals("Backlogs") ||
                column.equals("Wishlist");
    }

    private void createTableColumns() {
        // Columna Title (String)
        TableColumn<VideoGame, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(c -> c.getValue().titleProperty());
        titleCol.setPrefWidth(250);
        tableView.getColumns().add(titleCol);

        // Columna Release Date (String)
        TableColumn<VideoGame, String> releaseCol = new TableColumn<>("Release Date");
        releaseCol.setCellValueFactory(c -> c.getValue().releaseDateProperty());
        releaseCol.setPrefWidth(150);
        tableView.getColumns().add(releaseCol);

        // Columna Rating (Double)
        TableColumn<VideoGame, Double> ratingCol = new TableColumn<>("Rating");
        ratingCol.setCellValueFactory(c -> c.getValue().ratingProperty().asObject());
        ratingCol.setPrefWidth(100);
        tableView.getColumns().add(ratingCol);

        // Columna Number of Reviews (Integer)
        TableColumn<VideoGame, Integer> reviewsCol = new TableColumn<>("Number of Reviews");
        reviewsCol.setCellValueFactory(c -> c.getValue().numberOfReviewsProperty().asObject());
        reviewsCol.setPrefWidth(150);
        tableView.getColumns().add(reviewsCol);
    }

    private void performSort() {
        String column = columnCombo.getValue();
        String algorithm = algorithmCombo.getValue();

        List<VideoGame> currentList = new ArrayList<>(tableView.getItems());
        VideoGame[] array = currentList.toArray(new VideoGame[0]);

        Comparator<VideoGame> comparator = getComparatorForColumn(column);
        if (comparator == null) {
            timeLabel.setText("Error: columna no soportada.");
            return;
        }

        long time;
        try {
            if (algorithm.equals("Radix sort")) {
                if (!isIntegerColumn(column)) {
                    timeLabel.setText("Radix sort solo funciona con columnas numéricas enteras (ej: Number of Reviews)");
                    return;
                }
                // Obtenemos la función extractora de entero
                Ordenamientos.ToIntFunction<VideoGame> extractor = getIntExtractor(column);
                if (extractor == null) {
                    timeLabel.setText("No se puede aplicar Radix sort a esta columna.");
                    return;
                }
                time = SortBenchmark.measureRadixTime(array, extractor);
            } else {
                time = SortBenchmark.measureTime(array, comparator, algorithm);
            }
        } catch (Exception e) {
            timeLabel.setText("Error: " + e.getMessage());
            return;
        }

        System.out.println("Primer elemento antes: " + array[0].getTitle() + " - " + array[0].getNumberOfReviews());
// después de ordenar
        System.out.println("Primer elemento después: " + array[0].getTitle() + " - " + array[0].getNumberOfReviews());

        observableData.setAll(array);
        timeLabel.setText(String.format("Tiempo: %,d ns", time));
    }

    private void resetTable() {
        observableData.setAll(originalListCache);
        timeLabel.setText("Tabla restablecida.");
    }

    private Comparator<VideoGame> getComparatorForColumn(String column) {
        switch (column) {
            case "Title": return Comparator.comparing(VideoGame::getTitle, String.CASE_INSENSITIVE_ORDER);
            case "Release Date": return Comparator.comparing(VideoGame::getReleaseDate);
            case "Rating": return Comparator.comparingDouble(VideoGame::getRating);
            case "Number of Reviews": return Comparator.comparingInt(VideoGame::getNumberOfReviews);
            case "Times Listed": return Comparator.comparingInt(VideoGame::getTimesListed);
            case "Plays": return Comparator.comparingInt(VideoGame::getPlays);
            case "Playing": return Comparator.comparingInt(VideoGame::getPlaying);
            case "Backlogs": return Comparator.comparingInt(VideoGame::getBacklogs);
            case "Wishlist": return Comparator.comparingInt(VideoGame::getWishlist);
            default: return null;
        }
    }

    private Ordenamientos.ToIntFunction<VideoGame> getIntExtractor(String column) {
        switch (column) {
            case "Number of Reviews": return VideoGame::getNumberOfReviews;
            case "Times Listed": return VideoGame::getTimesListed;
            case "Plays": return VideoGame::getPlays;
            case "Playing": return VideoGame::getPlaying;
            case "Backlogs": return VideoGame::getBacklogs;
            case "Wishlist": return VideoGame::getWishlist;
            default: return null;
        }
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

        List<VideoGame> freshList = dataManager.getOriginalList();

        for (String algo : algorithms) {
            VideoGame[] copy = freshList.toArray(new VideoGame[0]);
            long t = SortBenchmark.measureTime(copy, comparator, algo);
            results.put(algo, t);
        }

        if (isIntegerColumn(column)) {
            VideoGame[] copyRadix = freshList.toArray(new VideoGame[0]);
            Ordenamientos.ToIntFunction<VideoGame> extractor = getIntExtractor(column);
            if (extractor != null) {
                long tRadix = SortBenchmark.measureRadixTime(copyRadix, extractor);
                results.put("Radix sort", tRadix);
            } else {
                results.put("Radix sort", 0L);
            }
        }

        // Mostrar gráfica en ventana emergente
        showChartInNewWindow(results, column);
        timeLabel.setText("Comparación completada. Gráfica en nueva ventana.");
    }

    private void showChartInNewWindow(Map<String, Long> times, String column) {
        Stage chartStage = new Stage();
        chartStage.setTitle("Comparación de tiempos - " + column);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Nanosegundos");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Rendimiento de algoritmos de ordenamiento");
        barChart.setPrefSize(800, 600);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tiempo (ns)");
        for (Map.Entry<String, Long> e : times.entrySet()) {
            series.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
        }
        barChart.getData().add(series);

        // Mejorar la visibilidad de las etiquetas
        xAxis.setTickLabelRotation(45); // rotar etiquetas para que no se solapen
        xAxis.setPrefWidth(600);

        VBox vbox = new VBox(barChart);
        vbox.setPadding(new Insets(10));
        Scene scene = new Scene(vbox, 900, 600);
        chartStage.setScene(scene);
        chartStage.show();
    }
}
