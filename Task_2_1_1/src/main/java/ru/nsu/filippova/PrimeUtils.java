package ru.nsu.filippova;

/**
 * Вспомогательные методы для работы с простыми числами.
 */
public final class PrimeUtils {
    /**
     * Закрытый конструктор утилитного класса.
     */
    private PrimeUtils() {
    }

    /**
     * Проверяет, является ли число простым.
     *
     * @param number проверяемое число
     * @return {@code true}, если число простое, иначе {@code false}
     */
    public static boolean isPrime(int number) {
        if (number < 2) {
            return false;
        }
        if (number == 2) {
            return true;
        }
        if (number % 2 == 0) {
            return false;
        }

        int limit = (int) Math.sqrt(number);
        for (int divisor = 3; divisor <= limit; divisor += 2) {
            if (number % divisor == 0) {
                return false;
            }
        }

        return true;
    }
}
