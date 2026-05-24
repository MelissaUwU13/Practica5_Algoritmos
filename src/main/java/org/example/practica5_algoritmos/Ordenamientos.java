package org.example.practica5_algoritmos;

import java.util.Comparator;

//En esta clase se almacenan los metodos de ordenamiento
public class Ordenamientos {

    public static <T> void selectionSort(T[] arr, Comparator<? super T> comp) {
        int tamano = arr.length;
        T menor;
        int k;

        for (int i = 0; i < tamano; i++) {
            menor = arr[i];
            k = i;

            for (int j = i + 1; j < tamano; j++) {
                if (comp.compare(arr[j], menor) < 0) {
                    menor = arr[j];
                    k = j;
                }
            }

            arr[k] = arr[i];
            arr[i] = menor;
        }
    }

    public static <T> void insertionSort(T[] arr, Comparator<? super T> comp) {
        int tamano = arr.length;
        T aux;
        int k;

        for (int i = 1; i < tamano; i++) {
            aux = arr[i];
            k = i - 1;

            while (k >= 0 && comp.compare(aux, arr[k]) < 0) {
                arr[k + 1] = arr[k];
                k = k - 1;
            }

            arr[k + 1] = aux;
        }
    }

    public static <T> void shellSort(T[] arr, Comparator<? super T> comp) {
        int tamano = arr.length;
        int intervalo = tamano + 1;
        int bandera, i;
        T aux;

        while (intervalo > 1) {
            intervalo = (intervalo / 2);
            bandera = 1;

            while (bandera == 1) {
                bandera = 0;
                i = 0;

                // Restamos 1 al límite superior por el desfase de índices en Java (0 a length-1)
                while ((i + intervalo) <= tamano - 1) {
                    // Si arr[i] es mayor que arr[i + intervalo]
                    if (comp.compare(arr[i], arr[i + intervalo]) > 0) {
                        aux = arr[i];
                        arr[i] = arr[i + intervalo];
                        arr[i + intervalo] = aux;
                        bandera = 1;
                    }
                    i += 1; // Se incrementa dentro del bucle para avanzar
                }
            }
        }
    }

    public static <T> void quickSort(T[] arr, Comparator<? super T> comp) {
        quickRecursivo(arr, 0, arr.length - 1, comp);
    }

    private static <T> void quickRecursivo(T[] arr, int inicio, int fin, Comparator<? super T> comp) {
        int izq = inicio, der = fin, pos = inicio;
        T aux;
        boolean bandera = true;

        while (bandera) {
            bandera = false;

            // Validar de derecha a izquierda usando el Comparator
            while (comp.compare(arr[pos], arr[der]) <= 0 && pos != der) {
                der = der - 1;
            }

            if (pos != der) {
                aux = arr[pos];
                arr[pos] = arr[der];
                arr[der] = aux;
                pos = der;

                // Validar de izquierda a derecha usando el Comparator
                while (comp.compare(arr[pos], arr[izq]) >= 0 && pos != izq) {
                    izq = izq + 1;
                }

                if (pos != izq) {
                    bandera = true;
                    aux = arr[pos];
                    arr[pos] = arr[izq];
                    arr[izq] = aux;
                    pos = izq;
                }
            }
        }

        // Sub-arreglos izquierdo y derecho recursivos
        if ((pos - 1) > inicio) {
            quickRecursivo(arr, inicio, pos - 1, comp);
        }

        if (fin > (pos + 1)) {
            quickRecursivo(arr, pos + 1, fin, comp);
        }
    }

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

    @FunctionalInterface
    public interface ToIntFunction<T> {
        int applyAsInt(T value);
    }

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
}