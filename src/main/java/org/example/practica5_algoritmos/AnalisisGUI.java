package org.example.practica5_algoritmos;

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

public class AnalisisGUI {
    private final DataManager dataManager;
    private ObservableList<VideoGame> observableData;
    private TableView<VideoGame> tableView;
    private ComboBox<String> columnCombo;
    private ComboBox<String> algorithmCombo;
    private Label timeLabel;
    private BarChart<String, Number> chart;
    private List<VideoGame> originalListCache;
    private final Controlador controlador = new Controlador();

    // Columnas permitidas para ordenar (excluyendo Summary, Reviews, etc.)
    private final List<String> allowedColumns = Arrays.asList(
            "Title", "Release Date", "Team", "Rating", "Genres",
            "Number of Reviews", "Times Listed", "Plays", "Playing", "Backlogs", "Wishlist"
    );

    public AnalisisGUI() {
        dataManager = new DataManager();
        originalListCache = dataManager.getOriginalList();
        observableData = FXCollections.observableArrayList(originalListCache);
        mostrar();
    }

    private void mostrar() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20)); // Más respiro en los bordes
        root.setStyle("-fx-background-color: #9ec5fa;"); // Un fondo azul/grisáceo muy claro y limpio

        tableView = new TableView<>();
        tableView.setItems(observableData);
        createTableColumns();

        // Hacemos que la tabla use todo el espacio vertical y horizontal disponible
        VBox.setVgrow(tableView, javafx.scene.layout.Priority.ALWAYS);

        // Contenedor de controles superior
        HBox controls = new HBox(15);
        controls.setPadding(new Insets(15));
        controls.setAlignment(javafx.geometry.Pos.CENTER_LEFT); // Centrar verticalmente los elementos internos
        controls.getStyleClass().add("panel-controles");

        columnCombo = new ComboBox<>();
        columnCombo.getStyleClass().add("combo-box-estilo");
        columnCombo.getItems().addAll(allowedColumns);
        columnCombo.setValue("Rating");

        algorithmCombo = new ComboBox<>();
        algorithmCombo.getStyleClass().add("combo-box-estilo");
        algorithmCombo.getItems().addAll("Quicksort", "Mergesort", "Shell sort", "Selection sort",
                "Radix sort", "Arrays.sort()", "Arrays.parallelSort()");
        algorithmCombo.setValue("Quicksort");

        columnCombo.setOnAction(e -> updateAlgorithmCombo());

        Button sortBtn = new Button("Ordenar");
        sortBtn.getStyleClass().add("boton-estilo");
        sortBtn.setOnAction(e -> performSort());

        Button resetBtn = new Button("Restablecer");
        resetBtn.getStyleClass().add("boton-estilo");
        resetBtn.setOnAction(e -> resetTable());

        Button benchmarkAllBtn = new Button("Gráfica");
        benchmarkAllBtn.getStyleClass().add("boton-estilo");
        benchmarkAllBtn.setOnAction(e -> benchmarkAllAlgorithms());

        timeLabel = new Label("Tiempo: -- ns");
        timeLabel.getStyleClass().add("label-tiempo");

        // Etiquetas con clase para darles estilo azul oscuro
        Label lblCol = new Label("Columna:");
        Label lblAlgo = new Label("Algoritmo:");
        lblCol.getStyleClass().add("label-encabezado");
        lblAlgo.getStyleClass().add("label-encabezado");

        controls.getChildren().addAll(lblCol, columnCombo,
                lblAlgo, algorithmCombo,
                sortBtn, resetBtn, benchmarkAllBtn, timeLabel);

        // Separación de 15px entre la barra de controles y la tabla
        VBox centerBox = new VBox(15, tableView);
        VBox.setVgrow(tableView, javafx.scene.layout.Priority.ALWAYS);

        root.setTop(controls);
        root.setCenter(centerBox);
        BorderPane.setMargin(controls, new Insets(0, 0, 15, 0)); // Margen inferior para los controles

        Scene scene = new Scene(root, 1000, 600); // Un ancho inicial más cómodo para tablas grandes
        scene.getStylesheets().add(getClass().getResource("/estilo.css").toExternalForm());

        Stage stage = new Stage();
        stage.setTitle("Ordenamiento de Videojuegos");
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
            long time = controlador.sortAndMeasure(array, column, algorithm);
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
            timeLabel.setText("No se puede comparar!");
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
        timeLabel.setText("Comparación completada!");
    }

    private void showChartInNewWindow(Map<String, Long> times, String column) {
        Stage chartStage = new Stage();
        chartStage.setTitle("Comparación de tiempos - " + column);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Nanosegundos");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Gráfica de Ordenamiento - " + column);
        barChart.setPrefSize(800, 600);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tiempo (ns)");
        for (Map.Entry<String, Long> e : times.entrySet()) {
            series.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
        }
        barChart.getData().add(series);

        // Mejorar la visibilidad de las etiquetas
        xAxis.setTickLabelRotation(45);
        xAxis.setPrefWidth(600);

        VBox vbox = new VBox(barChart);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 900, 600);

        scene.getStylesheets().add(getClass().getResource("/estilo.css").toExternalForm());

        chartStage.setScene(scene);
        chartStage.show();
    }
}
