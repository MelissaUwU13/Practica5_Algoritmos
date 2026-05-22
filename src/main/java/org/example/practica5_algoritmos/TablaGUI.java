package org.example.practica5_algoritmos;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class TablaGUI extends TableView<VideoGame> {

    public TablaGUI(ObservableList<VideoGame> datos) {
        this.setItems(datos);
        VBox.setVgrow(this, javafx.scene.layout.Priority.ALWAYS);
        crearColumnas();
    }

    private void crearColumnas() {
        // Columna Title
        TableColumn<VideoGame, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(c -> c.getValue().titleProperty());
        titleCol.setPrefWidth(250);
        this.getColumns().add(titleCol);

        // Columna Release Date
        TableColumn<VideoGame, String> releaseCol = new TableColumn<>("Release Date");
        releaseCol.setCellValueFactory(c -> c.getValue().releaseDateProperty());
        releaseCol.setPrefWidth(150);
        this.getColumns().add(releaseCol);

        // Columna Team
        TableColumn<VideoGame, String> teamCol = new TableColumn<>("Team");
        teamCol.setCellValueFactory(c -> c.getValue().teamProperty());
        teamCol.setPrefWidth(200);
        this.getColumns().add(teamCol);

        // Columna Rating
        TableColumn<VideoGame, Double> ratingCol = new TableColumn<>("Rating");
        ratingCol.setCellValueFactory(c -> c.getValue().ratingProperty().asObject());
        ratingCol.setPrefWidth(80);
        this.getColumns().add(ratingCol);

        // Columna Times Listed
        TableColumn<VideoGame, Integer> timesListedCol = new TableColumn<>("Times Listed");
        timesListedCol.setCellValueFactory(c -> c.getValue().timesListedProperty().asObject());
        timesListedCol.setPrefWidth(100);
        this.getColumns().add(timesListedCol);

        // Columna Number of Reviews
        TableColumn<VideoGame, Integer> reviewsCol = new TableColumn<>("Number of Reviews");
        reviewsCol.setCellValueFactory(c -> c.getValue().numberOfReviewsProperty().asObject());
        reviewsCol.setPrefWidth(130);
        this.getColumns().add(reviewsCol);

        // Columna Genres
        TableColumn<VideoGame, String> genresCol = new TableColumn<>("Genres");
        genresCol.setCellValueFactory(c -> c.getValue().genresProperty());
        genresCol.setPrefWidth(180);
        this.getColumns().add(genresCol);

        // Columna Summary
        TableColumn<VideoGame, String> summaryCol = new TableColumn<>("Summary");
        summaryCol.setCellValueFactory(c -> c.getValue().summaryProperty());
        summaryCol.setPrefWidth(350);
        this.getColumns().add(summaryCol);

        // Columna Reviews
        TableColumn<VideoGame, String> reviewsTextCol = new TableColumn<>("Reviews");
        reviewsTextCol.setCellValueFactory(c -> c.getValue().reviewsProperty());
        reviewsTextCol.setPrefWidth(450);
        this.getColumns().add(reviewsTextCol);

        // Columna Plays
        TableColumn<VideoGame, Integer> playsCol = new TableColumn<>("Plays");
        playsCol.setCellValueFactory(c -> c.getValue().playsProperty().asObject());
        playsCol.setPrefWidth(80);
        this.getColumns().add(playsCol);

        // Columna Playing
        TableColumn<VideoGame, Integer> playingCol = new TableColumn<>("Playing");
        playingCol.setCellValueFactory(c -> c.getValue().playingProperty().asObject());
        playingCol.setPrefWidth(80);
        this.getColumns().add(playingCol);

        // Columna Backlogs
        TableColumn<VideoGame, Integer> backlogsCol = new TableColumn<>("Backlogs");
        backlogsCol.setCellValueFactory(c -> c.getValue().backlogsProperty().asObject());
        backlogsCol.setPrefWidth(80);
        this.getColumns().add(backlogsCol);

        // Columna Wishlist
        TableColumn<VideoGame, Integer> wishlistCol = new TableColumn<>("Wishlist");
        wishlistCol.setCellValueFactory(c -> c.getValue().wishlistProperty().asObject());
        wishlistCol.setPrefWidth(80);
        this.getColumns().add(wishlistCol);
    }
}