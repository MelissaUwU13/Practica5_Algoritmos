package org.example.practica5_algoritmos;

import javafx.beans.property.*;

public class VideoGame {
    private final StringProperty name;
    private final StringProperty platform;
    private final IntegerProperty year;
    private final StringProperty genre;
    private final StringProperty publisher;
    private final DoubleProperty naSales;
    private final DoubleProperty euSales;
    private final DoubleProperty jpSales;
    private final DoubleProperty otherSales;
    private final DoubleProperty globalSales;

    public VideoGame(String name, String platform, int year, String genre, String publisher,
                     double naSales, double euSales, double jpSales, double otherSales, double globalSales) {
        this.name = new SimpleStringProperty(name);
        this.platform = new SimpleStringProperty(platform);
        this.year = new SimpleIntegerProperty(year);
        this.genre = new SimpleStringProperty(genre);
        this.publisher = new SimpleStringProperty(publisher);
        this.naSales = new SimpleDoubleProperty(naSales);
        this.euSales = new SimpleDoubleProperty(euSales);
        this.jpSales = new SimpleDoubleProperty(jpSales);
        this.otherSales = new SimpleDoubleProperty(otherSales);
        this.globalSales = new SimpleDoubleProperty(globalSales);
    }

    // Getters y PropertyGetters (necesarios para TableView)
    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    public String getPlatform() { return platform.get(); }
    public StringProperty platformProperty() { return platform; }

    public int getYear() { return year.get(); }
    public IntegerProperty yearProperty() { return year; }

    public String getGenre() { return genre.get(); }
    public StringProperty genreProperty() { return genre; }

    public String getPublisher() { return publisher.get(); }
    public StringProperty publisherProperty() { return publisher; }

    public double getNaSales() { return naSales.get(); }
    public DoubleProperty naSalesProperty() { return naSales; }

    public double getEuSales() { return euSales.get(); }
    public DoubleProperty euSalesProperty() { return euSales; }

    public double getJpSales() { return jpSales.get(); }
    public DoubleProperty jpSalesProperty() { return jpSales; }

    public double getOtherSales() { return otherSales.get(); }
    public DoubleProperty otherSalesProperty() { return otherSales; }

    public double getGlobalSales() { return globalSales.get(); }
    public DoubleProperty globalSalesProperty() { return globalSales; }
}