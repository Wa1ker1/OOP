package ru.nsu.filippova;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HashTable: итератор и fail-fast")
class HashTableIteratorTest {

    @Test
    @DisplayName("Итерация по всем элементам без пропусков/дубликатов")
    void iterateAllOnce() {
        HashTable<String, Integer> ht = new HashTable<>();
        ht.put("a", 1);
        ht.put("b", 2);
        ht.put("c", 3);

        Set<String> keys = new HashSet<>();
        Set<Integer> values = new HashSet<>();
        for (HashEntry<String, Integer> e : ht) {
            keys.add(e.getKey());
            values.add(e.getValue());
        }
        assertEquals(Set.of("a", "b", "c"), keys);
        assertEquals(Set.of(1, 2, 3), values);
    }

    @Test
    @DisplayName("Fail-fast: изменение структуры во время обхода -> ConcurrentModificationException")
    void failFastOnPut() {
        HashTable<String, Integer> ht = new HashTable<>();
        ht.put("a", 1);
        ht.put("b", 2);

        var it = ht.iterator();
        assertTrue(it.hasNext());
        it.next(); 

        ht.put("c", 3);

        assertThrows(ConcurrentModificationException.class, it::next);
    }

    @Test
    @DisplayName("Fail-fast: update также считается модификацией")
    void failFastOnUpdate() {
        HashTable<String, Integer> ht = new HashTable<>();
        ht.put("x", 1);
        var it = ht.iterator();
        assertTrue(it.hasNext());
        ht.update("x", 2);
        assertThrows(ConcurrentModificationException.class, it::next);
    }
}
