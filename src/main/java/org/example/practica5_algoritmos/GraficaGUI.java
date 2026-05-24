package org.example.practica5_algoritmos;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Map;

public class GraficaGUI {

    //Metodo para mostrar la grafica necesitando el tiempo, tipo de columna y el enlace css
    public static void mostrar(Map<String, Long> tiempo, String columna, String cssPath) {
        Stage chartStage = new Stage();
        chartStage.setTitle("Comparación de tiempos - " + columna);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Nanosegundos");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Gráfica de Ordenamiento - " + columna);
        barChart.setPrefSize(800, 600);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tiempo (ns)");

        //Segun la lista ingresada, lo estaremos ingresando a la grafica de barras
        for (Map.Entry<String, Long> e : tiempo.entrySet()) {
            series.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
        }

        barChart.getData().add(series);

        xAxis.setTickLabelRotation(45);
        xAxis.setPrefWidth(600);

        VBox vbox = new VBox(barChart);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 900, 600);

        //Carga el CSS pasado por parámetro
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath);
        }

        chartStage.setScene(scene);
        chartStage.show();
    }
}