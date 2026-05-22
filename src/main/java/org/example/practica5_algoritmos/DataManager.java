package org.example.practica5_algoritmos;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private List<VideoGame> originalList;

    public DataManager() {
        originalList = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        // Ruta del CSV dentro de resources (ajústala si es necesario)
        String csvFile = "/video_games.csv";
        try (InputStream is = getClass().getResourceAsStream(csvFile);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // saltar cabecera
                // El CSV tiene comillas alrededor de algunos campos, usamos un split simple
                // pero como hay comillas internas, hacemos una limpieza posterior
                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (fields.length < 14) continue;
                try {
                    // Saltamos el primer campo (índice 0) que está vacío
                    String title = clean(fields[1]);
                    String releaseDate = clean(fields[2]);
                    String team = clean(fields[3]);
                    double rating = fields[4].isEmpty() ? 0.0 : Double.parseDouble(fields[4]);
                    int timesListed = parseKNumber(clean(fields[5]));
                    int numberOfReviews = parseKNumber(clean(fields[6]));
                    String genres = clean(fields[7]);
                    String summary = clean(fields[8]);
                    String reviews = clean(fields[9]);
                    int plays = parseKNumber(clean(fields[10]));
                    int playing = parseKNumber(clean(fields[11]));
                    int backlogs = parseKNumber(clean(fields[12]));
                    int wishlist = parseKNumber(clean(fields[13]));

                    originalList.add(new VideoGame(title, releaseDate, team, rating,
                            timesListed, numberOfReviews, genres, summary, reviews,
                            plays, playing, backlogs, wishlist));
                } catch (Exception e) {
                    // ignorar líneas con formato incorrecto
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error cargando datos: " + e.getMessage());
        }
        System.out.println("Cargados " + originalList.size() + " registros.");
    }

    private String clean(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) s = s.substring(1, s.length()-1);
        return s;
    }

    // Convierte cadenas como "3.9K" a 3900
    private int parseKNumber(String s) {
        if (s == null || s.isEmpty()) return 0;
        s = s.trim().toUpperCase();
        if (s.endsWith("K")) {
            double val = Double.parseDouble(s.substring(0, s.length()-1));
            return (int)(val * 1000);
        }
        return (int)Double.parseDouble(s);
    }

    public List<VideoGame> getOriginalList() {
        return new ArrayList<>(originalList);
    }
}