package org.example.practica5_algoritmos;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataManager {
    private List<VideoGame> originalList;

    public DataManager() {
        originalList = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        // Asegúrate de que el archivo video_games.csv esté en src/app/resources/
        try (InputStream is = getClass().getResourceAsStream("/app/resources/video_games.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // saltar cabecera
                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // maneja comillas
                if (fields.length < 10) continue;

                try {
                    String name = clean(fields[0]);
                    String platform = clean(fields[1]);
                    int year = fields[2].isEmpty() ? 0 : Integer.parseInt(fields[2]);
                    String genre = clean(fields[3]);
                    String publisher = clean(fields[4]);
                    double na = fields[5].isEmpty() ? 0 : Double.parseDouble(fields[5]);
                    double eu = fields[6].isEmpty() ? 0 : Double.parseDouble(fields[6]);
                    double jp = fields[7].isEmpty() ? 0 : Double.parseDouble(fields[7]);
                    double other = fields[8].isEmpty() ? 0 : Double.parseDouble(fields[8]);
                    double global = fields[9].isEmpty() ? 0 : Double.parseDouble(fields[9]);

                    originalList.add(new VideoGame(name, platform, year, genre, publisher,
                            na, eu, jp, other, global));
                } catch (NumberFormatException e) {
                    // ignorar filas con formato incorrecto
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

    public List<VideoGame> getOriginalList() {
        return new ArrayList<>(originalList);  // copia defensiva
    }
}