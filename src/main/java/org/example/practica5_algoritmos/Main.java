package org.example.practica5_algoritmos;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        new Controlador(); // El controlador crea su propia ventana
    }

    public static void main(String[] args) {
        launch(args);
    }
}