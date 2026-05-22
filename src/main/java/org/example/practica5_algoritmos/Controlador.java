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
    private final SortService sortService = new SortService();

    // Columnas permitidas para ordenar (excluyendo Summary, Reviews, etc.)
    private final List<String> allowedColumns = Arrays.asList(
            "Title", "Release Date", "Team", "Rating", "Genres",
            "Number of Reviews", "Times Listed", "Plays", "Playing", "Backlogs", "Wishlist"
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

        // Columna Team (String)
        TableColumn<VideoGame, String> teamCol = new TableColumn<>("Team");
        teamCol.setCellValueFactory(c -> c.getValue().teamProperty());
        teamCol.setPrefWidth(200);
        tableView.getColumns().add(teamCol);

        // Columna Rating (Double)
        TableColumn<VideoGame, Double> ratingCol = new TableColumn<>("Rating");
        ratingCol.setCellValueFactory(c -> c.getValue().ratingProperty().asObject());
        ratingCol.setPrefWidth(80);
        tableView.getColumns().add(ratingCol);

        // Columna Times Listed (Integer)
        TableColumn<VideoGame, Integer> timesListedCol = new TableColumn<>("Times Listed");
        timesListedCol.setCellValueFactory(c -> c.getValue().timesListedProperty().asObject());
        timesListedCol.setPrefWidth(100);
        tableView.getColumns().add(timesListedCol);

        // Columna Number of Reviews (Integer)
        TableColumn<VideoGame, Integer> reviewsCol = new TableColumn<>("Number of Reviews");
        reviewsCol.setCellValueFactory(c -> c.getValue().numberOfReviewsProperty().asObject());
        reviewsCol.setPrefWidth(130);
        tableView.getColumns().add(reviewsCol);

        // Columna Genres (String)
        TableColumn<VideoGame, String> genresCol = new TableColumn<>("Genres");
        genresCol.setCellValueFactory(c -> c.getValue().genresProperty());
        genresCol.setPrefWidth(180);
        tableView.getColumns().add(genresCol);

        // Columna Summary (String, texto largo)
        TableColumn<VideoGame, String> summaryCol = new TableColumn<>("Summary");
        summaryCol.setCellValueFactory(c -> c.getValue().summaryProperty());
        summaryCol.setPrefWidth(350);
        tableView.getColumns().add(summaryCol);

        // Columna Reviews (String, texto muy largo)
        TableColumn<VideoGame, String> reviewsTextCol = new TableColumn<>("Reviews");
        reviewsTextCol.setCellValueFactory(c -> c.getValue().reviewsProperty());
        reviewsTextCol.setPrefWidth(450);
        tableView.getColumns().add(reviewsTextCol);

        // Columna Plays (Integer)
        TableColumn<VideoGame, Integer> playsCol = new TableColumn<>("Plays");
        playsCol.setCellValueFactory(c -> c.getValue().playsProperty().asObject());
        playsCol.setPrefWidth(80);
        tableView.getColumns().add(playsCol);

        // Columna Playing (Integer)
        TableColumn<VideoGame, Integer> playingCol = new TableColumn<>("Playing");
        playingCol.setCellValueFactory(c -> c.getValue().playingProperty().asObject());
        playingCol.setPrefWidth(80);
        tableView.getColumns().add(playingCol);

        // Columna Backlogs (Integer)
        TableColumn<VideoGame, Integer> backlogsCol = new TableColumn<>("Backlogs");
        backlogsCol.setCellValueFactory(c -> c.getValue().backlogsProperty().asObject());
        backlogsCol.setPrefWidth(80);
        tableView.getColumns().add(backlogsCol);

        // Columna Wishlist (Integer)
        TableColumn<VideoGame, Integer> wishlistCol = new TableColumn<>("Wishlist");
        wishlistCol.setCellValueFactory(c -> c.getValue().wishlistProperty().asObject());
        wishlistCol.setPrefWidth(80);
        tableView.getColumns().add(wishlistCol);
    }




    private void performSort() {
        String column = columnCombo.getValue();
        String algorithm = algorithmCombo.getValue();

        List<VideoGame> currentList = new ArrayList<>(tableView.getItems());
        VideoGame[] array = currentList.toArray(new VideoGame[0]);

        try {
            long time = sortService.sortAndMeasure(array, column, algorithm);
            observableData.setAll(array);
            timeLabel.setText(String.format("Tiempo: %,d ns", time));
        } catch (Exception e) {
            timeLabel.setText("Error: " + e.getMessage());
        }
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
            case "Team": return Comparator.comparing(VideoGame::getTeam, String.CASE_INSENSITIVE_ORDER);
            case "Genres": return Comparator.comparing(VideoGame::getGenres, String.CASE_INSENSITIVE_ORDER);
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
