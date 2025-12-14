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

    /**
     * Проверяет, остались ли элементы для обхода.
     *
     * @return true, если есть ещё элементы
     */
    @Override
    public boolean hasNext() {
        return current != null;
    }

    /**
     * Возвращает следующий элемент, контролируя модификации таблицы.
     *
     * @return следующий {@link HashEntry}
     */
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

    /**
     * Проверяет, не была ли таблица изменена после создания итератора.
     */
    private void checkForComodification() {
        if (expectedModCount != tableRef.getModCount()) {
            throw new ConcurrentModificationException("HashTable was modified during iteration");
        }
    }

    /**
     * Находит следующую непустую цепочку в массиве бакетов.
     *
     * @return первый элемент следующего непустого бакета или {@code null}
     */
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
