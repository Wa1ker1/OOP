package ru.nsu.filippova;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Параметризованная хеш-таблица на цепочках.
 *
 * <p>
 * Поддерживает операции:
 * <ul>
 *     <li>создание пустой таблицы;</li>
 *     <li>{@link #put(Object, Object)} — добавление нового ключа;</li>
 *     <li>{@link #update(Object, Object)} — обновление уже существующего ключа;</li>
 *     <li>{@link #get(Object)} — получение значения по ключу;</li>
 *     <li>{@link #remove(Object)} — удаление пары;</li>
 *     <li>{@link #containsKey(Object)} — проверка, есть ли ключ;</li>
 *     <li>итерация по всем (k, v) с защитой от конкурентных модификаций;</li>
 *     <li>{@link #equals(Object)} и {@link #toString()}.</li>
 * </ul>
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
public class HashTable<K, V> implements Iterable<HashEntry<K, V>> {

    /** начальная ёмкость по умолчанию. */
    private static final int DEFAULT_CAPACITY = 16;
    /** целевой коэффициент загрузки. */
    private static final float LOAD_FACTOR = 0.75f;

    /** массив бакетов. */
    private HashEntry<K, V>[] table;
    /** текущее количество элементов. */
    private int size;
    /** модификации структуры — для итератора. */
    private int modCount;

    /**
     * Создаёт пустую хеш-таблицу с ёмкостью по умолчанию.
     */
    @SuppressWarnings("unchecked")
    public HashTable() {
        this.table = (HashEntry<K, V>[]) new HashEntry[DEFAULT_CAPACITY];
        this.size = 0;
        this.modCount = 0;
    }

    /**
     * Создаёт пустую хеш-таблицу с указанной начальной ёмкостью.
     *
     * @param capacity начальное количество бакетов
     */
    @SuppressWarnings("unchecked")
    public HashTable(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be > 0");
        }
        this.table = (HashEntry<K, V>[]) new HashEntry[capacity];
        this.size = 0;
        this.modCount = 0;
    }

    /**
     * Добавляет пару (key, value), если такого ключа ещё нет.
     *
     * <p>
     * В задании есть отдельный метод {@link #update(Object, Object)}, поэтому
     * здесь при наличии ключа кидаем исключение.
     *
     * @param key   ключ
     * @param value значение
     * @throws IllegalArgumentException если ключ уже есть
     */
    public void put(K key, V value) {
        ensureCapacity();

        int index = indexFor(key);
        HashEntry<K, V> current = table[index];
        while (current != null) {
            if (keysEqual(current.getKey(), key)) {
                throw new IllegalArgumentException("Key already exists: " + key);
            }
            current = current.getNext();
        }

        HashEntry<K, V> newEntry = new HashEntry<>(key, value, table[index]);
        table[index] = newEntry;
        size++;
        modCount++;
    }

    /**
     * Обновляет значение по уже существующему ключу.
     *
     * @param key   ключ
     * @param value новое значение
     * @throws NoSuchElementException если ключа нет
     */
    public void update(K key, V value) {
        int index = indexFor(key);
        HashEntry<K, V> current = table[index];
        while (current != null) {
            if (keysEqual(current.getKey(), key)) {
                current.setValue(value);
                modCount++; 
                return;
            }
            current = current.getNext();
        }
        throw new NoSuchElementException("Key not found: " + key);
    }

    /**
     * Возвращает значение по ключу или {@code null}, если ключ не найден.
     *
     * @param key ключ
     * @return значение или null
     */
    public V get(K key) {
        int index = indexFor(key);
        HashEntry<K, V> current = table[index];
        while (current != null) {
            if (keysEqual(current.getKey(), key)) {
                return current.getValue();
            }
            current = current.getNext();
        }
        return null;
    }

    /**
     * Удаляет пару по ключу.
     *
     * @param key ключ
     * @return удалённое значение или {@code null}, если ключа не было
     */
    public V remove(K key) {
        int index = indexFor(key);
        HashEntry<K, V> current = table[index];
        HashEntry<K, V> prev = null;

        while (current != null) {
            if (keysEqual(current.getKey(), key)) {
                if (prev == null) {
                    table[index] = current.getNext();
                } else {
                    prev.setNext(current.getNext());
                }
                size--;
                modCount++;
                return current.getValue();
            }
            prev = current;
            current = current.getNext();
        }
        return null;
    }

    /**
     * Проверка наличия ключа.
     *
     * @param key ключ
     * @return true, если ключ есть
     */
    public boolean containsKey(K key) {
        int index = indexFor(key);
        HashEntry<K, V> current = table[index];
        while (current != null) {
            if (keysEqual(current.getKey(), key)) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    /**
     * Возвращает текущее количество элементов в таблице.
     *
     * @return текущее количество элементов
     */
    public int size() {
        return size;
    }

    /**
     * Удаляет все элементы.
     */
    @SuppressWarnings("unchecked")
    public void clear() {
        this.table = (HashEntry<K, V>[]) new HashEntry[DEFAULT_CAPACITY];
        this.size = 0;
        this.modCount++;
    }

    /**
     * Итератор по всем парам (k, v).
     *
     * <p>
     * Если во время обхода структура была изменена — бросит
     * {@link ConcurrentModificationException}.
     */
    @Override
    public Iterator<HashEntry<K, V>> iterator() {
        return new HashTableIterator<>(this);
    }

    /**
     * Сравнение с другой хеш-таблицей: равны, если размеры совпадают и
     * для каждого ключа значения equals.
     *
     * @param o объект для сравнения
     * @return true, если таблицы логически равны
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HashTable<?, ?>)) {
            return false;
        }

        HashTable<?, ?> rawOther = (HashTable<?, ?>) o;
        if (this.size != rawOther.size) {
            return false;
        }

        HashTable<K, ?> other = (HashTable<K, ?>) rawOther;

        for (HashEntry<K, V> entry : this) {
            Object otherVal = other.get(entry.getKey());
            if (otherVal == null && !other.containsKey(entry.getKey())) {
                return false;
            }
            if (entry.getValue() == null) {
                if (otherVal != null) {
                    return false;
                }
            } else if (!entry.getValue().equals(otherVal)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Строковое представление в виде {k1=v1, k2=v2}.
     *
     * @return строка
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (HashEntry<K, V> entry : this) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Вычисляет индекс бакета для заданного ключа.
     *
     * @param key ключ
     * @return номер бакета
     */
    private int indexFor(K key) {
        int h = (key == null) ? 0 : key.hashCode();
        return (h & 0x7fffffff) % table.length;
    }

    /**
     * Сравнивает ключи с учётом возможных {@code null}-ов.
     *
     * @param k1 первый ключ
     * @param k2 второй ключ
     * @return true, если значения эквивалентны
     */
    private boolean keysEqual(K k1, K k2) {
        if (k1 == null) {
            return k2 == null;
        }
        return k1.equals(k2);
    }

    /**
     * Контролирует коэффициент загрузки и при необходимости расширяет таблицу.
     */
    private void ensureCapacity() {
        if ((float) (size + 1) / table.length > LOAD_FACTOR) {
            resize(table.length * 2);
        }
    }

    /**
     * Перестраивает таблицу с новой ёмкостью и перераспределяет пары.
     *
     * @param newCapacity новая ёмкость таблицы
     */
    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        HashEntry<K, V>[] oldTable = this.table;
        this.table = (HashEntry<K, V>[]) new HashEntry[newCapacity];
        this.size = 0;

        for (HashEntry<K, V> bucket : oldTable) {
            HashEntry<K, V> current = bucket;
            while (current != null) {
                put(current.getKey(), current.getValue());
                current = current.getNext();
            }
        }
    }

    /**
     * Возвращает количество структурных модификаций (для итератора).
     *
     * @return счётчик модификаций
     */
    int getModCount() {
        return modCount;
    }

    /**
     * Предоставляет массив бакетов для итератора.
     *
     * @return внутренний массив
     */
    HashEntry<K, V>[] getTable() {
        return table;
    }
}
