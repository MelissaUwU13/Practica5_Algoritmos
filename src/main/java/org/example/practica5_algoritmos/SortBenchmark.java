package org.example.practica5_algoritmos;

import java.util.Arrays;
import java.util.Comparator;

public class SortBenchmark {

    public static <T> long measureTime(T[] arr, Comparator<? super T> comp, String algorithm) {
        long start = System.nanoTime();
        switch (algorithm) {
            case "Quicksort": Ordenamientos.quickSort(arr, comp); break;
            case "Mergesort": Ordenamientos.mergeSort(arr, comp); break;
            case "Shell sort": Ordenamientos.shellSort(arr, comp); break;
            case "Selection sort": Ordenamientos.selectionSort(arr, comp); break;
            case "Arrays.sort()": Arrays.sort(arr, comp); break;
            case "Arrays.parallelSort()": Arrays.parallelSort(arr, comp); break;
            default: throw new IllegalArgumentException("Algoritmo desconocido: " + algorithm);
        }
        long end = System.nanoTime();
        return end - start;
    }

    public static <T> long measureRadixTime(T[] arr, Ordenamientos.ToIntFunction<? super T> keyExtractor) {
        long start = System.nanoTime();
        Ordenamientos.radixSort(arr, keyExtractor);
        long end = System.nanoTime();
        return end - start;
    }
}