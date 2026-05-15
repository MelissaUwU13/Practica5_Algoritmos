package org.example.practica5_algoritmos;

import java.util.Arrays;
import java.util.Comparator;

public class SortBenchmark {

    // Benchmark para algoritmos genéricos (Quick, Merge, Shell, Selection)
    public static <T> long measureTime(T[] arr, Comparator<? super T> comp, String algorithm) {
        T[] copy = arr.clone();
        long start = System.nanoTime();
        switch (algorithm) {
            case "Quicksort": Ordenamientos.quickSort(copy, comp); break;
            case "Mergesort": Ordenamientos.mergeSort(copy, comp); break;
            case "Shell sort": Ordenamientos.shellSort(copy, comp); break;
            case "Selection sort": Ordenamientos.selectionSort(copy, comp); break;
            case "Arrays.sort()": Arrays.sort(copy, comp); break;
            case "Arrays.parallelSort()": Arrays.parallelSort(copy, comp); break;
            default: throw new IllegalArgumentException("Algoritmo desconocido: " + algorithm);
        }
        long end = System.nanoTime();
        return end - start;
    }

    // Benchmark especial para Radix Sort (requiere keyExtractor int)
    public static <T> long measureRadixTime(T[] arr, Ordenamientos.ToIntFunction<? super T> keyExtractor) {
        T[] copy = arr.clone();
        long start = System.nanoTime();
        Ordenamientos.radixSort(copy, keyExtractor);
        long end = System.nanoTime();
        return end - start;
    }
}