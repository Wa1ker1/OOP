package ru.nsu.filippova;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

class PrimeUtilsTest {
    @Test
    void isPrimeReturnsFalseForNumbersLessThanTwo() {
        assertFalse(PrimeUtils.isPrime(-5));
        assertFalse(PrimeUtils.isPrime(0));
        assertFalse(PrimeUtils.isPrime(1));
    }

    @Test
    void isPrimeHandlesPrimeAndCompositeNumbers() {
        assertTrue(PrimeUtils.isPrime(2));
        assertTrue(PrimeUtils.isPrime(3));
        assertTrue(PrimeUtils.isPrime(2_147_483_647));
        assertFalse(PrimeUtils.isPrime(4));
        assertFalse(PrimeUtils.isPrime(9));
        assertFalse(PrimeUtils.isPrime(21));
    }

    @Test
    void utilityConstructorCanBeInvokedReflectively() throws Exception {
        Constructor<PrimeUtils> constructor = PrimeUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
