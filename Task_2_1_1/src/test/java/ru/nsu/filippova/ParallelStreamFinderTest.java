package ru.nsu.filippova;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ParallelStreamFinderTest {
    private final ParallelStreamFinder finder = new ParallelStreamFinder();

    @Test
    void throwsForNullArray() {
        assertThrows(IllegalArgumentException.class, () -> finder.hasNonPrime(null));
    }

    @Test
    void returnsFalseWhenAllNumbersArePrime() {
        assertFalse(finder.hasNonPrime(new int[]{2, 3, 5, 7, 11, 13, 17, 19}));
    }

    @Test
    void returnsTrueWhenArrayContainsNonPrime() {
        assertTrue(finder.hasNonPrime(new int[]{2, 3, 5, 6, 11, 13, 17, 19}));
    }

    @Test
    void returnsFalseForEmptyArray() {
        assertFalse(finder.hasNonPrime(new int[0]));
    }
}
