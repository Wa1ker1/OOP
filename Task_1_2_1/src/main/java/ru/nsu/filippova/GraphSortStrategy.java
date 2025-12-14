package ru.nsu.filippova;

import java.util.List;

/**
 * Стратегия сортировки вершин графа.
 * Позволяет добавлять новые алгоритмы сортировки без изменения интерфейса Graph.
 *
 * @param <V> тип вершины
 */
@FunctionalInterface
public interface GraphSortStrategy<V> {
    /**
     * Выполняет сортировку вершин для переданного графа.
     *
     * @param graph граф
     * @return отсортированный список вершин
     */
    List<V> sort(Graph<V> graph);
}
