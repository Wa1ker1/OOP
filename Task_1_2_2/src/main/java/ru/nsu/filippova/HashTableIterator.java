package ru.nsu.filippova;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Итератор по хеш-таблице.
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
public class HashTableIterator<K, V> implements Iterator<HashEntry<K, V>> {

    private final HashTable<K, V> tableRef;
    private final int expectedModCount;

    private int bucketIndex = 0;
    private HashEntry<K, V> current;

    /**
     * Создаёт итератор для указанной таблицы.
     *
     * @param tableRef таблица
     */
    public HashTableIterator(HashTable<K, V> tableRef) {
        this.tableRef = tableRef;
        this.expectedModCount = tableRef.getModCount();
        this.current = findNextNonEmptyBucket();
    }

    @Override
    public boolean hasNext() {
        return current != null;
    }

    @Override
    public HashEntry<K, V> next() {
        checkForComodification();
        if (current == null) {
            throw new NoSuchElementException();
        }
        HashEntry<K, V> result = current;

        if (current.getNext() != null) {
            current = current.getNext();
        } else {
            current = findNextNonEmptyBucket();
        }
        return result;
    }

    private void checkForComodification() {
        if (expectedModCount != tableRef.getModCount()) {
            throw new ConcurrentModificationException("HashTable was modified during iteration");
        }
    }

    private HashEntry<K, V> findNextNonEmptyBucket() {
        HashEntry<K, V>[] arr = tableRef.getTable();
        while (bucketIndex < arr.length) {
            HashEntry<K, V> candidate = arr[bucketIndex++];
            if (candidate != null) {
                return candidate;
            }
        }
        return null;
    }
}
