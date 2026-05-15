package org.example.practica5_algoritmos;

import java.util.Comparator;

public class Ordenamientos {

    // ---------- QUICKSORT ----------
    public static <T> void quickSort(T[] arr, Comparator<? super T> comp) {
        quickSort(arr, 0, arr.length - 1, comp);
    }
    private static <T> void quickSort(T[] arr, int low, int high, Comparator<? super T> comp) {
        if (low < high) {
            int pi = partition(arr, low, high, comp);
            quickSort(arr, low, pi - 1, comp);
            quickSort(arr, pi + 1, high, comp);
        }
    }
    private static <T> int partition(T[] arr, int low, int high, Comparator<? super T> comp) {
        T pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (comp.compare(arr[j], pivot) <= 0) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    // ---------- MERGESORT ----------
    public static <T> void mergeSort(T[] arr, Comparator<? super T> comp) {
        if (arr.length < 2) return;
        T[] aux = arr.clone();
        mergeSort(arr, aux, 0, arr.length - 1, comp);
    }
    private static <T> void mergeSort(T[] arr, T[] aux, int left, int right, Comparator<? super T> comp) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(arr, aux, left, mid, comp);
            mergeSort(arr, aux, mid + 1, right, comp);
            merge(arr, aux, left, mid, right, comp);
        }
    }
    private static <T> void merge(T[] arr, T[] aux, int left, int mid, int right, Comparator<? super T> comp) {
        System.arraycopy(arr, left, aux, left, right - left + 1);
        int i = left, j = mid + 1, k = left;
        while (i <= mid && j <= right) {
            if (comp.compare(aux[i], aux[j]) <= 0) arr[k++] = aux[i++];
            else arr[k++] = aux[j++];
        }
        while (i <= mid) arr[k++] = aux[i++];
    }

    // ---------- SHELL SORT (secuencia de Knuth) ----------
    public static <T> void shellSort(T[] arr, Comparator<? super T> comp) {
        int n = arr.length;
        int h = 1;
        while (h < n / 3) h = 3 * h + 1;
        while (h >= 1) {
            for (int i = h; i < n; i++) {
                for (int j = i; j >= h && comp.compare(arr[j], arr[j - h]) < 0; j -= h) {
                    swap(arr, j, j - h);
                }
            }
            h /= 3;
        }
    }

    // ---------- SELECCIÓN DIRECTA (Selection sort) ----------
    public static <T> void selectionSort(T[] arr, Comparator<? super T> comp) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (comp.compare(arr[j], arr[minIdx]) < 0) minIdx = j;
            }
            swap(arr, i, minIdx);
        }
    }

    // ---------- RADIX SORT (sólo para enteros) ----------
    // Necesitamos una función que extraiga un int de cada elemento
    public static <T> void radixSort(T[] arr, ToIntFunction<? super T> keyExtractor) {
        if (arr.length == 0) return;
        int maxVal = Integer.MIN_VALUE;
        for (T item : arr) {
            int val = keyExtractor.applyAsInt(item);
            if (val > maxVal) maxVal = val;
        }
        int exp = 1;
        T[] output = arr.clone();
        while (maxVal / exp > 0) {
            countingSortByDigit(arr, output, exp, keyExtractor);
            exp *= 10;
        }
    }
    private static <T> void countingSortByDigit(T[] arr, T[] output, int exp, ToIntFunction<? super T> keyExtractor) {
        int n = arr.length;
        int[] count = new int[10];
        for (T item : arr) {
            int digit = (keyExtractor.applyAsInt(item) / exp) % 10;
            count[digit]++;
        }
        for (int i = 1; i < 10; i++) count[i] += count[i - 1];
        for (int i = n - 1; i >= 0; i--) {
            int digit = (keyExtractor.applyAsInt(arr[i]) / exp) % 10;
            output[count[digit] - 1] = arr[i];
            count[digit]--;
        }
        System.arraycopy(output, 0, arr, 0, n);
    }

    @FunctionalInterface
    public interface ToIntFunction<T> {
        int applyAsInt(T value);
    }

    // Utilidad
    private static <T> void swap(T[] arr, int i, int j) {
        T temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}