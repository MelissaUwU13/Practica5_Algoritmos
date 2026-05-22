package org.example.practica5_algoritmos;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private List<VideoGame> ListaOriginal;

    public DataManager() {
        ListaOriginal = new ArrayList<>();
        cargarDatos();
    }

    private void cargarDatos() {
        String csvFile = "/video_games.csv";
        try (InputStream is = getClass().getResourceAsStream(csvFile);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; continue;
                } // saltar cabecera

                // El CSV tiene comillas alrededor de algunos campos, usamos un split simple
                // pero como hay comillas internas, hacemos una limpieza posterior
                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (fields.length < 14) continue;

                try {
                    // Saltamos el primer campo (índice 0) que está vacío
                    String title = limpiar(fields[1]);
                    String releaseDate = limpiar(fields[2]);
                    String team = limpiar(fields[3]);
                    double rating = fields[4].isEmpty() ? 0.0 : Double.parseDouble(fields[4]);
                    int timesListed = convertidorNumero(limpiar(fields[5]));
                    int numberOfReviews = convertidorNumero(limpiar(fields[6]));
                    String genres = limpiar(fields[7]);
                    String summary = limpiar(fields[8]);
                    String reviews = limpiar(fields[9]);
                    int plays = convertidorNumero(limpiar(fields[10]));
                    int playing = convertidorNumero(limpiar(fields[11]));
                    int backlogs = convertidorNumero(limpiar(fields[12]));
                    int wishlist = convertidorNumero(limpiar(fields[13]));

                    ListaOriginal.add(new VideoGame(title, releaseDate, team, rating,
                            timesListed, numberOfReviews, genres, summary, reviews,
                            plays, playing, backlogs, wishlist));
                }
                catch (Exception e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String limpiar(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) s = s.substring(1, s.length()-1);
        return s;
    }

    // Convierte cadenas como "3.9K" a 3900
    private int convertidorNumero(String s) {
        if (s == null || s.isEmpty()) return 0;
        s = s.trim().toUpperCase();
        if (s.endsWith("K")) {
            double val = Double.parseDouble(s.substring(0, s.length()-1));
            return (int)(val * 1000);
        }
        return (int)Double.parseDouble(s);
    }

    public List<VideoGame> getListaOriginal() {
        return new ArrayList<>(ListaOriginal);
    }
}