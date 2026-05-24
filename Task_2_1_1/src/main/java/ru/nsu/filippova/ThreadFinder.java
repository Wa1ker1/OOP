package ru.nsu.filippova;

/**
 * Параллельная реализация поиска не простого числа с использованием
 * нескольких экземпляров {@link Thread}.
 */
public class ThreadFinder implements NonPrimeFinder {
    private final int threadCount;

    /**
     * Создает поиск с заданным количеством потоков.
     *
     * @param threadCount количество рабочих потоков
     * @throws IllegalArgumentException если количество потоков не положительное
     */
    public ThreadFinder(int threadCount) {
        if (threadCount <= 0) {
            throw new IllegalArgumentException("threadCount must be > 0");
        }
        this.threadCount = threadCount;
    }

    /**
     * Делит массив на части между потоками и возвращает результат, как только
     * один из потоков находит не простое число.
     *
     * @param numbers массив целых чисел
     * @return {@code true}, если найдено хотя бы одно не простое число
     * @throws IllegalArgumentException если передан {@code null}
     */
    @Override
    public boolean hasNonPrime(int[] numbers) {
        if (numbers == null) {
            throw new IllegalArgumentException("Array is null");
        }
        if (numbers.length == 0) {
            return false;
        }

        int workersCount = Math.min(threadCount, numbers.length);
        int chunk = (numbers.length + workersCount - 1) / workersCount;
        SearchResult result = new SearchResult();
        Worker[] workers = new Worker[workersCount];

        for (int i = 0; i < workersCount; i++) {
            int start = i * chunk;
            int end = Math.min(start + chunk, numbers.length);
            workers[i] = new Worker(numbers, start, end, result);
            workers[i].start();
        }

        for (Worker worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                return result.isFound();
            }
        }

        return result.isFound();
    }

    /**
     * Общий результат поиска. Поле volatile обеспечивает видимость изменений
     * между потоками без применения atomic-типов.
     */
    private static class SearchResult {
        private volatile boolean found;

        private boolean isFound() {
            return found;
        }

        private void markFound() {
            found = true;
        }
    }

    /**
     * Рабочий поток, проверяющий выделенный диапазон массива.
     */
    private static class Worker extends Thread {
        private final int[] numbers;
        private final int start;
        private final int end;
        private final SearchResult result;

        /**
         * Создает рабочий поток для проверки части массива.
         *
         * @param numbers исходный массив чисел
         * @param start индекс начала диапазона включительно
         * @param end индекс конца диапазона не включительно
         * @param result общий результат поиска
         */
        private Worker(int[] numbers, int start, int end, SearchResult result) {
            this.numbers = numbers;
            this.start = start;
            this.end = end;
            this.result = result;
        }

        /**
         * Проверяет свой диапазон массива и устанавливает общий флаг
         * при обнаружении не простого числа.
         */
        @Override
        public void run() {
            for (int i = start; i < end && !result.isFound(); i++) {
                if (!PrimeUtils.isPrime(numbers[i])) {
                    result.markFound();
                    return;
                }
            }
        }
    }
}
