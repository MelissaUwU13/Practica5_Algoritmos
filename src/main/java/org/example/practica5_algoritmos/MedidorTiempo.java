package org.example.practica5_algoritmos;

import java.util.Arrays;
import java.util.Comparator;

public class MedidorTiempo {

    //Los tiempos se miden en nanosegundos

    //Calculamos el tiempo de cada algoritmo y regresamos el resultado final
    public static <T> long medirTiempo(T[] arr, Comparator<? super T> comp, String algoritmo) {
        long TiempoInicio = System.nanoTime();
        switch (algoritmo) {
            case "Quicksort": Ordenamientos.quickSort(arr, comp); break;
            case "Mergesort": Ordenamientos.mergeSort(arr, comp); break;
            case "Shell sort": Ordenamientos.shellSort(arr, comp); break;
            case "Selection sort": Ordenamientos.selectionSort(arr, comp); break;
            case "Arrays.sort()": Arrays.sort(arr, comp); break;
            case "Arrays.parallelSort()": Arrays.parallelSort(arr, comp); break;
            default: throw new IllegalArgumentException("Algoritmo desconocido: " + algoritmo);
        }
        long TiempoFinal = System.nanoTime();
        return TiempoFinal - TiempoInicio;
    }

    //Este es un caso especial para calcular el tiempo en en caso de que sea el algoritmo radix
    public static <T> long medirTiempoRadix(T[] arr, Ordenamientos.ToIntFunction<? super T> keyExtractor) {
        long TiempoInicio = System.nanoTime();
        Ordenamientos.radixSort(arr, keyExtractor);
        long TiempoFinal = System.nanoTime();
        return TiempoFinal - TiempoInicio;
    }
}