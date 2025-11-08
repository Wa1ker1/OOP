package ru.nsu.filippova;

/**
 * Один элемент (узел) цепочки в бакете хеш-таблицы.
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
public class HashEntry<K, V> {

    private final K key;
    private V value;
    private HashEntry<K, V> next;

    /**
     * Создаёт новый элемент.
     *
     * @param key   ключ
     * @param value значение
     * @param next  следующий элемент цепочки
     */
    public HashEntry(K key, V value, HashEntry<K, V> next) {
        this.key = key;
        this.value = value;
        this.next = next;
    }

    /**
     * @return ключ
     */
    public K getKey() {
        return key;
    }

    /**
     * @return значение
     */
    public V getValue() {
        return value;
    }

    /**
     * Устанавливает новое значение.
     *
     * @param value новое значение
     */
    public void setValue(V value) {
        this.value = value;
    }

    /**
     * @return следующий элемент в цепочке
     */
    public HashEntry<K, V> getNext() {
        return next;
    }

    /**
     * Устанавливает следующий элемент цепочки.
     *
     * @param next новый следующий элемент
     */
    public void setNext(HashEntry<K, V> next) {
        this.next = next;
    }
}
