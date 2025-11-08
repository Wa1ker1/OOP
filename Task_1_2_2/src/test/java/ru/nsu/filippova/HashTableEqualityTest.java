package ru.nsu.filippova;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HashTable: equals")
class HashTableEqualityTest {

    @Test
    @DisplayName("equals: одинаковые наборы пар равны (порядок вставки неважен)")
    void equalsSameContent() {
        HashTable<String, Integer> a = new HashTable<>();
        HashTable<String, Integer> b = new HashTable<>();

        a.put("k1", 1);
        a.put("k2", 2);
        b.put("k2", 2);
        b.put("k1", 1);

        assertEquals(a, b);
        assertEquals(b, a);
    }

    @Test
    @DisplayName("equals: разные размеры -> не равны")
    void notEqualsDifferentSizes() {
        HashTable<String, Integer> a = new HashTable<>();
        HashTable<String, Integer> b = new HashTable<>();
        a.put("k1", 1);
        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("equals: одинаковые ключи, разные значения -> не равны")
    void notEqualsDifferentValues() {
        HashTable<String, Integer> a = new HashTable<>();
        HashTable<String, Integer> b = new HashTable<>();
        a.put("k1", 1);
        b.put("k1", 2);
        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("equals: null-значения корректно сравниваются")
    void equalsWithNullValues() {
        HashTable<String, Integer> a = new HashTable<>();
        HashTable<String, Integer> b = new HashTable<>();
        a.put("k", null);
        b.put("k", null);
        assertEquals(a, b);

        b.update("k", 1);
        assertNotEquals(a, b);
    }
}
