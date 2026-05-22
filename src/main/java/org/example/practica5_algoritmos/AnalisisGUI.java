package org.example.practica5_algoritmos;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

public class AnalisisGUI {
    private final DataManager dataManager;
    private ObservableList<VideoGame> observableData;
    private TablaGUI tablaView;
    private ComboBox<String> columnaCombo;
    private ComboBox<String> algoritmoCombo;
    private Label timeLabel;
    private List<VideoGame> ListaOriginal;
    private final Controlador controlador = new Controlador();

    private final List<String> allowedColumns = Arrays.asList(
            "Title", "Release Date", "Team", "Rating", "Genres",
            "Number of Reviews", "Times Listed", "Plays", "Playing", "Backlogs", "Wishlist"
    );

    public AnalisisGUI() {
        dataManager = new DataManager();
        ListaOriginal = dataManager.getListaOriginal();
        observableData = FXCollections.observableArrayList(ListaOriginal);
        mostrar();
    }

    private void mostrar() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #9ec5fa;");

        //creamos nuestra tabla
        tablaView = new TablaGUI(observableData);

        //Panel de controles
        HBox controls = new HBox(15);
        controls.setPadding(new Insets(15));
        controls.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        controls.getStyleClass().add("panel-controles");

        columnaCombo = new ComboBox<>();
        columnaCombo.getStyleClass().add("combo-box-estilo");
        columnaCombo.getItems().addAll(allowedColumns);
        columnaCombo.setValue("Rating");

        algoritmoCombo = new ComboBox<>();
        algoritmoCombo.getStyleClass().add("combo-box-estilo");
        algoritmoCombo.getItems().addAll("Quicksort", "Mergesort", "Shell sort", "Selection sort",
                "Radix sort", "Arrays.sort()", "Arrays.parallelSort()");
        algoritmoCombo.setValue("Quicksort");

        //accion del boton
        columnaCombo.setOnAction(
                e -> actualizarAlgoritmoCombo()
        );

        //Botones
        Button sortBtn = new Button("Ordenar");
        sortBtn.getStyleClass().add("boton-estilo");
        sortBtn.setOnAction(e -> performSort());

        Button resetBtn = new Button("Restablecer");
        resetBtn.getStyleClass().add("boton-estilo");
        resetBtn.setOnAction(e ->
                resetearTabla()
        );

        Button benchmarkAllBtn = new Button("Gráfica");
        benchmarkAllBtn.getStyleClass().add("boton-estilo");
        benchmarkAllBtn.setOnAction(e -> benchmarkAllAlgorithms());

        timeLabel = new Label("Tiempo: -- ns");
        timeLabel.getStyleClass().add("label-tiempo");

        Label lblCol = new Label("Columna:");
        Label lblAlgo = new Label("Algoritmo:");
        lblCol.getStyleClass().add("label-encabezado");
        lblAlgo.getStyleClass().add("label-encabezado");

        controls.getChildren().addAll(lblCol, columnaCombo, lblAlgo, algoritmoCombo,
                sortBtn, resetBtn, benchmarkAllBtn, timeLabel);

        VBox centerBox = new VBox(15, tablaView);

        root.setTop(controls);
        root.setCenter(centerBox);
        BorderPane.setMargin(controls, new Insets(0, 0, 15, 0));

        Scene scene = new Scene(root, 1000, 600);
        String cssUrl = getClass().getResource("/estilo.css").toExternalForm();
        scene.getStylesheets().add(cssUrl);

        Stage stage = new Stage();
        stage.setTitle("Ordenamiento de Videojuegos");
        stage.setScene(scene);
        stage.show();
    }

    private void actualizarAlgoritmoCombo() {
        String col = columnaCombo.getValue();
        if (!isIntegerColumn(col) && "Radix sort".equals(algoritmoCombo.getValue())) {
            algoritmoCombo.setValue("Quicksort");
        }
    }

    private boolean isIntegerColumn(String column) {
        return column.equals("Number of Reviews") || column.equals("Times Listed") ||
                column.equals("Plays") || column.equals("Playing") ||
                column.equals("Backlogs") || column.equals("Wishlist");
    }

    private void performSort() {
        String column = columnaCombo.getValue();
        String algorithm = algoritmoCombo.getValue();

        List<VideoGame> currentList = new ArrayList<>(tablaView.getItems());
        VideoGame[] array = currentList.toArray(new VideoGame[0]);

        try {
            long time = controlador.sortAndMeasure(array, column, algorithm);
            observableData.setAll(array);
            timeLabel.setText(String.format("Tiempo: %,d ns", time));
        } catch (Exception e) {
            timeLabel.setText("Error: " + e.getMessage());
        }
    }

    private void resetearTabla() {
        observableData.setAll();
        timeLabel.setText("Tabla restablecida");
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
        String columna = columnaCombo.getValue();
        Comparator<VideoGame> comparator = getComparatorForColumn(columna);
        if (comparator == null) {
            timeLabel.setText("No se puede comparar!");
            return;
        }

        List<String> algoritmos = Arrays.asList("Quicksort", "Mergesort", "Shell sort", "Selection sort",
                "Arrays.sort()", "Arrays.parallelSort()");
        Map<String, Long> results = new LinkedHashMap<>();

        List<VideoGame> ListaNueva = dataManager.getListaOriginal();

        for (String algo : algoritmos) {
            VideoGame[] copy = ListaNueva.toArray(new VideoGame[0]);
            long t = MedidorTiempo.medirTiempo(copy, comparator, algo);
            results.put(algo, t);
        }

        if (isIntegerColumn(columna)) {
            VideoGame[] copyRadix = ListaNueva.toArray(new VideoGame[0]);
            Ordenamientos.ToIntFunction<VideoGame> extractor = getIntExtractor(columna);
            if (extractor != null) {
                long tRadix = MedidorTiempo.medirTiempoRadix(copyRadix, extractor);
                results.put("Radix sort", tRadix);
            }
            else {
                results.put("Radix sort", 0L);
            }
        }

        String cssUrl = getClass().getResource("/estilo.css").toExternalForm();
        GraficaGUI.mostrar(results, columna, cssUrl);

        timeLabel.setText("Comparación completada!");
    }
}