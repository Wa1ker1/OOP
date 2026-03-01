package ru.nsu.filippova;

import java.util.Arrays;

/**
 * Параллельная реализация поиска не простого числа с использованием
 * {@link java.util.stream.IntStream#parallel()}.
 */
public class ParallelStreamFinder implements NonPrimeFinder {
    /**
     * Запускает параллельную обработку элементов массива и завершает поиск,
     * как только найдено первое не простое число.
     *
     * @param numbers массив целых чисел
     * @return {@code true}, если в массиве найдено не простое число
     * @throws IllegalArgumentException если передан {@code null}
     */
    @Override
    public boolean hasNonPrime(int[] numbers) {
        if (numbers == null) {
            throw new IllegalArgumentException("Array is null");
        }

        return Arrays.stream(numbers)
                .parallel()
                .anyMatch(number -> !PrimeUtils.isPrime(number));
    }
}
