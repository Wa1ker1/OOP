package ru.nsu.filippova;

/**
 * Последовательная реализация поиска не простого числа в массиве.
 */
public class SequentialFinder implements NonPrimeFinder {
    /**
     * Последовательно обходит массив и завершает поиск при первом найденном
     * не простом числе.
     *
     * @param numbers массив целых чисел
     * @return {@code true}, если в массиве есть хотя бы одно не простое число
     * @throws IllegalArgumentException если передан {@code null}
     */
    @Override
    public boolean hasNonPrime(int[] numbers) {
        if (numbers == null) {
            throw new IllegalArgumentException("Array is null");
        }

        for (int number : numbers) {
            if (!PrimeUtils.isPrime(number)) {
                return true;
            }
        }

        return false;
    }
}
