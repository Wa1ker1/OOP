package ru.nsu.filippova;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HashTable: коллизии и resize")
class HashTableCollisionAndResizeTest {

    /**
     * Генерирует ключи, у которых одинаковый hashCode, чтобы гарантировать коллизию.
     * Простой трюк: использовать объекты с переопределённым hashCode.
     */
    static final class BadKey {
        final String s;
        BadKey(String s) { this.s = s; }
        @Override public int hashCode() { return 42; } 
        @Override public boolean equals(Object o) {
            return (o instanceof BadKey) && ((BadKey) o).s.equals(this.s);
        }
        @Override public String toString() { return "K(" + s + ")"; }
    }

    @Test
    @DisplayName("Коллизии на цепочках: все значения доступны и корректно удаляются")
    void collisionsChainWork() {
        HashTable<BadKey, Integer> ht = new HashTable<>(4);

        BadKey k1 = new BadKey("a");
        BadKey k2 = new BadKey("b");
        BadKey k3 = new BadKey("c");

        ht.put(k1, 1);
        ht.put(k2, 2);
        ht.put(k3, 3);

        assertEquals(3, ht.size());
        assertEquals(2, ht.get(k2));
        assertEquals(3, ht.get(k3));

        assertEquals(2, ht.remove(k2));
        assertNull(ht.get(k2));
        assertEquals(2, ht.size());

        assertEquals(1, ht.get(k1));
        assertEquals(3, ht.get(k3));
    }

    @Test
    @DisplayName("Авто-расширение (resize) сохраняет все элементы")
    void resizeKeepsAll() {
        HashTable<Integer, Integer> ht = new HashTable<>(2);
        for (int i = 0; i < 100; i++) {
            ht.put(i, i * 10);
        }
        assertEquals(100, ht.size());
        for (int i = 0; i < 100; i++) {
            assertEquals(i * 10, ht.get(i));
        }
    }
}
