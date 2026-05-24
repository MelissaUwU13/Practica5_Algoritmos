package org.example.practica5_algoritmos;

import javafx.beans.property.*;

//En esta clase obtenemos los datos de nuestro archivo
public class VideoGame {
    private final StringProperty title;
    private final StringProperty releaseDate;
    private final StringProperty team;
    private final DoubleProperty rating;
    private final IntegerProperty timesListed;
    private final IntegerProperty numberOfReviews;
    private final StringProperty genres;
    private final StringProperty summary;
    private final StringProperty reviews;
    private final IntegerProperty plays;
    private final IntegerProperty playing;
    private final IntegerProperty backlogs;
    private final IntegerProperty wishlist;

    //Constructor - modelado de los datos de video games
    public VideoGame(String title, String releaseDate, String team, double rating,
                     int timesListed, int numberOfReviews, String genres, String summary,
                     String reviews, int plays, int playing, int backlogs, int wishlist) {
        this.title = new SimpleStringProperty(title);
        this.releaseDate = new SimpleStringProperty(releaseDate);
        this.team = new SimpleStringProperty(team);
        this.rating = new SimpleDoubleProperty(rating);
        this.timesListed = new SimpleIntegerProperty(timesListed);
        this.numberOfReviews = new SimpleIntegerProperty(numberOfReviews);
        this.genres = new SimpleStringProperty(genres);
        this.summary = new SimpleStringProperty(summary);
        this.reviews = new SimpleStringProperty(reviews);
        this.plays = new SimpleIntegerProperty(plays);
        this.playing = new SimpleIntegerProperty(playing);
        this.backlogs = new SimpleIntegerProperty(backlogs);
        this.wishlist = new SimpleIntegerProperty(wishlist);
    }

    // Getters y PropertyGetters para TableView
    public String getTitle() { return title.get(); }
    public StringProperty titleProperty() { return title; }
    public String getReleaseDate() { return releaseDate.get(); }
    public StringProperty releaseDateProperty() { return releaseDate; }
    public String getTeam() { return team.get(); }
    public StringProperty teamProperty() { return team; }
    public double getRating() { return rating.get(); }
    public DoubleProperty ratingProperty() { return rating; }
    public int getTimesListed() { return timesListed.get(); }
    public IntegerProperty timesListedProperty() { return timesListed; }
    public int getNumberOfReviews() { return numberOfReviews.get(); }
    public IntegerProperty numberOfReviewsProperty() { return numberOfReviews; }
    public String getGenres() { return genres.get(); }
    public StringProperty genresProperty() { return genres; }
    public String getSummary() { return summary.get(); }
    public StringProperty summaryProperty() { return summary; }
    public String getReviews() { return reviews.get(); }
    public StringProperty reviewsProperty() { return reviews; }
    public int getPlays() { return plays.get(); }
    public IntegerProperty playsProperty() { return plays; }
    public int getPlaying() { return playing.get(); }
    public IntegerProperty playingProperty() { return playing; }
    public int getBacklogs() { return backlogs.get(); }
    public IntegerProperty backlogsProperty() { return backlogs; }
    public int getWishlist() { return wishlist.get(); }
    public IntegerProperty wishlistProperty() { return wishlist; }
}