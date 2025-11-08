package ru.nsu.filippova;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HashTable: базовые операции")
class HashTableBasicTest {

    @Test
    @DisplayName("put/get/update/remove/contains/size")
    void basicCrud() {
        HashTable<String, Number> ht = new HashTable<>();

        assertEquals(0, ht.size());
        assertFalse(ht.containsKey("a"));
        assertNull(ht.get("a"));
        assertNull(ht.remove("a"));

        ht.put("a", 1);
        assertTrue(ht.containsKey("a"));
        assertEquals(1, ht.get("a"));
        assertEquals(1, ht.size());

        ht.update("a", 1.5);
        assertEquals(1.5, ht.get("a"));
        assertEquals(1, ht.size());

        Number removed = ht.remove("a");
        assertEquals(1.5, removed);
        assertEquals(0, ht.size());
        assertFalse(ht.containsKey("a"));
    }

    @Test
    @DisplayName("put одного и того же ключа — ошибка (обновление через update)")
    void putDuplicateKeyThrows() {
        HashTable<String, Integer> ht = new HashTable<>();
        ht.put("k", 1);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ht.put("k", 2));
        assertTrue(ex.getMessage().contains("Key already exists"));
        assertEquals(1, ht.get("k"));
    }

    @Test
    @DisplayName("update несуществующего ключа — NoSuchElementException")
    void updateMissingKeyThrows() {
        HashTable<Integer, String> ht = new HashTable<>();
        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> ht.update(42, "x"));
        assertTrue(ex.getMessage().contains("Key not found"));
    }

    @Test
    @DisplayName("Работа с null-ключом (поддерживается)")
    void nullKeySupported() {
        HashTable<String, String> ht = new HashTable<>();
        ht.put(null, "nil");
        assertTrue(ht.containsKey(null));
        assertEquals("nil", ht.get(null));

        ht.update(null, "zero");
        assertEquals("zero", ht.get(null));

        assertEquals("zero", ht.remove(null));
        assertNull(ht.get(null));
    }

    @Test
    @DisplayName("toString содержит пары в фигурных скобках (порядок не гарантирован)")
    void toStringHasPairs() {
        HashTable<String, Integer> ht = new HashTable<>();
        ht.put("a", 10);
        ht.put("b", 20);
        String s = ht.toString();
        assertTrue(s.startsWith("{") && s.endsWith("}"));
        assertTrue(s.contains("a=10"));
        assertTrue(s.contains("b=20"));
    }

    @Test
    @DisplayName("clear сбрасывает состояние")
    void clearResets() {
        HashTable<String, Integer> ht = new HashTable<>();
        ht.put("x", 1);
        ht.put("y", 2);
        assertEquals(2, ht.size());
        ht.clear();
        assertEquals(0, ht.size());
        assertNull(ht.get("x"));
        assertNull(ht.get("y"));
    }
}
