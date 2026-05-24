package ru.nsu.filippova;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ThreadFinderTest {
    @Test
    void constructorRejectsNonPositiveThreadCount() {
        assertThrows(IllegalArgumentException.class, () -> new ThreadFinder(0));
        assertThrows(IllegalArgumentException.class, () -> new ThreadFinder(-2));
    }

    @Test
    void throwsForNullArray() {
        ThreadFinder finder = new ThreadFinder(2);
        assertThrows(IllegalArgumentException.class, () -> finder.hasNonPrime(null));
    }

    @Test
    void returnsFalseForEmptyArray() {
        ThreadFinder finder = new ThreadFinder(4);
        assertFalse(finder.hasNonPrime(new int[0]));
    }

    @Test
    void returnsFalseWhenAllNumbersArePrime() {
        ThreadFinder finder = new ThreadFinder(10);
        assertFalse(finder.hasNonPrime(new int[]{2, 3, 5, 7, 11}));
    }

    @Test
    void returnsTrueWhenArrayContainsNonPrime() {
        ThreadFinder finder = new ThreadFinder(3);
        assertTrue(finder.hasNonPrime(new int[]{2, 3, 5, 7, 1, 11, 13}));
    }

    @Test
    void preservesInterruptStatusWhenJoinIsInterrupted() {
        ThreadFinder finder = new ThreadFinder(2);
        int[] data = {2, 3, 5, 7, 11};

        Thread.currentThread().interrupt();
        try {
            assertFalse(finder.hasNonPrime(data));
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
    }
}
