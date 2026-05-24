package org.example.practica5_algoritmos;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

//En esta clase cargamos los datos del archivo y lo guardamos en una lista
public class DataManager {
    private List<VideoGame> ListaOriginal;

    //constructor
    public DataManager() {
        ListaOriginal = new ArrayList<>();
        cargarDatos();
    }

    //Metodo que carga la informacion del archivo en una lista
    private void cargarDatos() {
        String csvFile = "/video_games.csv";
        //Verificamos con try si el archivo esta en las carpetas
        try (InputStream is = getClass().getResourceAsStream(csvFile);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false; continue;
                } //se salta cabecera ya que es un espacio en blanco

                //El CSV tiene comillas alrededor de algunos campos, usamos un split simple
                //pero como hay comillas internas, hacemos una limpieza posterior
                String[] campos = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                //si es menor a 14 entonces no es el archivo esperado
                if (campos.length < 14) continue;

                try {
                    //Saltamos el primer campo (índice 0) que está vacío y vamos directo al indice 1
                    String title = limpiar(campos[1]);
                    String releaseDate = limpiar(campos[2]);
                    String team = limpiar(campos[3]);
                    double rating = campos[4].isEmpty() ? 0.0 : Double.parseDouble(campos[4]);
                    int timesListed = convertidorNumero(limpiar(campos[5]));
                    int numberOfReviews = convertidorNumero(limpiar(campos[6]));
                    String genres = limpiar(campos[7]);
                    String summary = limpiar(campos[8]);
                    String reviews = limpiar(campos[9]);
                    int plays = convertidorNumero(limpiar(campos[10]));
                    int playing = convertidorNumero(limpiar(campos[11]));
                    int backlogs = convertidorNumero(limpiar(campos[12]));
                    int wishlist = convertidorNumero(limpiar(campos[13]));

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

    //Limpia la cadena de forma mas organizada
    private String limpiar(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) s = s.substring(1, s.length()-1);
        return s;
    }

    //Convierte cadenas como "3.9K" a 3900
    private int convertidorNumero(String s) {
        if (s == null || s.isEmpty()) return 0;
        s = s.trim().toUpperCase();
        if (s.endsWith("K")) {
            double val = Double.parseDouble(s.substring(0, s.length()-1));
            return (int)(val * 1000);
        }
        return (int)Double.parseDouble(s);
    }

    //GETTER
    public List<VideoGame> getListaOriginal() {
        return new ArrayList<>(ListaOriginal);
    }
}