package ru.nsu.filippova;

/**
 * Определяет, содержит ли массив хотя бы одно число, не являющееся простым.
 */
public interface NonPrimeFinder {
    /**
     * Проверяет массив чисел на наличие хотя бы одного составного,
     * отрицательного числа, нуля или единицы.
     *
     * @param numbers массив целых чисел
     * @return {@code true}, если найдено хотя бы одно не простое число,
     *         иначе {@code false}
     * @throws IllegalArgumentException если передан {@code null}
     */
    boolean hasNonPrime(int[] numbers);
}
