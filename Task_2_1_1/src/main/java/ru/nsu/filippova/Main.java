package ru.nsu.filippova;

import java.util.Arrays;

/**
 * Точка входа для ручного сравнения реализаций поиска не простого числа.
 */
public final class Main {
    private static final int DATASET_SIZE = 10_000;
    private static final int[] PRIME_SAMPLE = {2_147_483_647, 2_147_483_629,
        2_147_483_587, 2_147_483_579, 2_147_483_563, 2_147_483_549, 2_147_483_477
    };

    private Main() {
    }

    /**
     * Создает массив, состоящий только из простых чисел.
     *
     * @param size длина массива
     * @return массив с простыми числами
     */
    public static int[] buildPrimeDataset(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be >= 0");
        }

        int[] numbers = new int[size];
        for (int i = 0; i < size; i++) {
            numbers[i] = PRIME_SAMPLE[i % PRIME_SAMPLE.length];
        }
        return numbers;
    }

    private static void runCase(String label, NonPrimeFinder finder, int[] numbers) {
        long startedAt = System.nanoTime();
        boolean result = finder.hasNonPrime(numbers);
        long elapsedNanos = System.nanoTime() - startedAt;
        double elapsedMillis = elapsedNanos / 1_000_000.0;

        System.out.printf("%-28s -> result=%-5s time=%.3f ms%n", label, result, elapsedMillis);
    }

    /**
     * Запускает сравнение реализаций на одном массиве простых чисел.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        int[] numbers = buildPrimeDataset(DATASET_SIZE);

        System.out.println("Первые элементы набора с простыми числами: "
                + Arrays.toString(Arrays.copyOf(numbers, Math.min(8, numbers.length))));
        System.out.println("Размер массива: " + numbers.length);

        runCase("Sequential", new SequentialFinder(), numbers);
        runCase("Thread (1)", new ThreadFinder(1), numbers);
        runCase("Thread (2)", new ThreadFinder(2), numbers);
        runCase("Thread (4)", new ThreadFinder(4), numbers);
        runCase("Thread (8)", new ThreadFinder(8), numbers);
        runCase("Parallel Stream", new ParallelStreamFinder(), numbers);
    }
}
