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
    private Label label;
    private List<VideoGame> ListaOriginal;
    private final Controlador controlador = new Controlador();

    private final List<String> Columnas = Arrays.asList(
            "Title", "Release Date", "Team", "Rating", "Genres",
            "Number of Reviews", "Times Listed", "Plays", "Playing", "Backlogs", "Wishlist"
    );

    //Constructor
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

        //Combos de columnas y de algoritmos
        columnaCombo = new ComboBox<>();
        columnaCombo.getStyleClass().add("combo-box-estilo");
        columnaCombo.getItems().addAll(Columnas);
        columnaCombo.setValue("Rating");

        algoritmoCombo = new ComboBox<>();
        algoritmoCombo.getStyleClass().add("combo-box-estilo");
        algoritmoCombo.getItems().addAll("Quicksort", "Mergesort", "Shell sort", "Selection sort",
                "Radix sort", "Arrays.sort()", "Arrays.parallelSort()");
        algoritmoCombo.setValue("Quicksort");

        columnaCombo.setOnAction(
                e -> actualizarAlgoritmoCombo()
        );

        //Botones
        Button sortBtn = new Button("Ordenar");
        sortBtn.getStyleClass().add("boton-estilo");
        sortBtn.setOnAction(
                e -> ordenar()
        );

        Button resetBtn = new Button("Restablecer");
        resetBtn.getStyleClass().add("boton-estilo");
        resetBtn.setOnAction(
                e -> resetearTabla()
        );

        Button benchmarkAllBtn = new Button("Gráfica");
        benchmarkAllBtn.getStyleClass().add("boton-estilo");
        benchmarkAllBtn.setOnAction(
                e -> OrdenarTodo()
        );

        //Textos
        Label lblCol = new Label("Columna:");
        Label lblAlgo = new Label("Algoritmo:");
        label = new Label("--");
        label.getStyleClass().add("label-tiempo");
        lblCol.getStyleClass().add("label-encabezado");
        lblAlgo.getStyleClass().add("label-encabezado");

        controls.getChildren().addAll(lblCol, columnaCombo, lblAlgo, algoritmoCombo,
                sortBtn, resetBtn, benchmarkAllBtn, label);

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

    //Actualizamos el combo de algoritmos
    private void actualizarAlgoritmoCombo() {
        String col = columnaCombo.getValue();
        if (!ColumnaEsNumerica(col) && "Radix sort".equals(algoritmoCombo.getValue())) {
            algoritmoCombo.setValue("Quicksort");
        }
    }

    //Verificamos si la columna es de tipo numerico y regresamos true o false
    private boolean ColumnaEsNumerica(String columna) {
        return columna.equals("Number of Reviews") || columna.equals("Times Listed") ||
                columna.equals("Plays") || columna.equals("Playing") ||
                columna.equals("Backlogs") || columna.equals("Wishlist");
    }

    //Ordenamos la tabla grafica segun lo seleccionado
    private void ordenar() {
        String columna = columnaCombo.getValue();
        String algoritmo = algoritmoCombo.getValue();

        List<VideoGame> currentList = new ArrayList<>(tablaView.getItems());
        VideoGame[] array = currentList.toArray(new VideoGame[0]);

        try {
            long time = controlador.OrdenarAlgoritmo(array, columna, algoritmo);
            observableData.setAll(array);
        } catch (Exception e) {
            label.setText("Error: " + e.getMessage());
        }
    }

    //Regresamos la tabla a su estado original
    private void resetearTabla() {
        observableData.setAll(ListaOriginal);
        label.setText("Tabla restablecida");
    }

    //comparamos si la opcion seleccionada esta dentro de las opciones y regresamos el nombre
    private Comparator<VideoGame> getComparadorDeColumnas(String columna) {
        switch (columna) {
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

    //Este metodo regresa el contenido segun la columna
    private Ordenamientos.ToIntFunction<VideoGame> getIntExtractor(String columna) {
        switch (columna) {
            case "Number of Reviews": return VideoGame::getNumberOfReviews;
            case "Times Listed": return VideoGame::getTimesListed;
            case "Plays": return VideoGame::getPlays;
            case "Playing": return VideoGame::getPlaying;
            case "Backlogs": return VideoGame::getBacklogs;
            case "Wishlist": return VideoGame::getWishlist;
            default: return null;
        }
    }

    //En este metodo realizamos una copia de la lista original de los elementos de la tabla, para que mediante
    //un for apliques cada uno de los algoritmos y contemos el tiempo y los iremos almacenando en otra lista
    //la cual mandaremos a otro metodos llamado mostrar que graficara los resultados de los tiempos en una grafica
    private void OrdenarTodo() {
        String columna = columnaCombo.getValue();
        Comparator<VideoGame> comparator = getComparadorDeColumnas(columna);
        if (comparator == null) {
            label.setText("No se puede comparar!");
            return;
        }

        List<String> algoritmos = Arrays.asList("Quicksort", "Mergesort", "Shell sort", "Selection sort",
                "Arrays.sort()", "Arrays.parallelSort()");
        Map<String, Long> resultados = new LinkedHashMap<>();

        List<VideoGame> ListaNueva = dataManager.getListaOriginal();

        for (String algo : algoritmos) {
            VideoGame[] copia = ListaNueva.toArray(new VideoGame[0]);
            long t = MedidorTiempo.medirTiempo(copia, comparator, algo);
            resultados.put(algo, t);
        }

        //Apartado especial para verificar si agregar a radix a la grafica
        if (ColumnaEsNumerica(columna)){
            VideoGame[] copiaRadix = ListaNueva.toArray(new VideoGame[0]);
            Ordenamientos.ToIntFunction<VideoGame> extractor = getIntExtractor(columna);

            //Este es un apartado especial para radix, ya que en caso de que la columna no sea un
            //tipo de dato apto para radix, no se agregara ni graficara
            if (extractor != null) {
                long tRadix = MedidorTiempo.medirTiempoRadix(copiaRadix, extractor);
                resultados.put("Radix sort", tRadix);
            }
            else {
                resultados.put("Radix sort", 0L);
            }
        }

        String cssUrl = getClass().getResource("/estilo.css").toExternalForm();
        GraficaGUI.mostrar(resultados, columna, cssUrl);

        label.setText("Comparación completada!");
    }
}