package org.example.practica5_algoritmos;

import java.util.Comparator;

public class Controlador {
    public long sortAndMeasure(VideoGame[] array, String column, String algorithm) {
        Comparator<VideoGame> comparator = getComparator(column);

        if (comparator == null) throw new IllegalArgumentException("Columna no soportada");
        if (algorithm.equals("Radix sort")) {
            if (!isIntegerColumn(column)) throw new IllegalArgumentException("Radix solo para enteros");
            Ordenamientos.ToIntFunction<VideoGame> extractor = getIntExtractor(column);
            return MedidorTiempo.medirTiempoRadix(array, extractor);
        }
        else {
            return MedidorTiempo.medirTiempo(array, comparator, algorithm);
        }
    }

    private Comparator<VideoGame> getComparator(String column) {
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

    private boolean isIntegerColumn(String column) {
        return column.equals("Number of Reviews") || column.equals("Times Listed") ||
                column.equals("Plays") || column.equals("Playing") ||
                column.equals("Backlogs") || column.equals("Wishlist");
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
}
