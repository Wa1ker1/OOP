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
     * Возвращает ключ, связанный с этим элементом.
     *
     * @return ключ
     */
    public K getKey() {
        return key;
    }

    /**
     * Возвращает сохранённое значение.
     *
     * @return значение
     */
    public V getValue() {
        return value;
    }

    /**
     * Устанавливает новое значение без изменения положения элемента.
     *
     * @param value новое значение
     */
    public void setValue(V value) {
        this.value = value;
    }

    /**
     * Возвращает ссылку на следующий элемент в цепочке бакета.
     *
     * @return следующий элемент в цепочке
     */
    public HashEntry<K, V> getNext() {
        return next;
    }

    /**
     * Перенастраивает ссылку на следующий элемент в цепочке.
     *
     * @param next новый следующий элемент
     */
    public void setNext(HashEntry<K, V> next) {
        this.next = next;
    }
}
