package ru.nsu.filippova;

/**
 * Готовые стратегии сортировки вершин.
 * Новые алгоритмы можно добавлять как новые методы, возвращающие {@link GraphSortStrategy}.
 */
public final class GraphSortStrategies {

    private GraphSortStrategies() {
    }

    /**
     * Топологическая сортировка на основе DFS.
     */
    public static <V> GraphSortStrategy<V> depthFirstTopological() {
        return GraphAlgorithms::topologicalSort;
    }

    /**
     * Топологическая сортировка по алгоритму Кана.
     */
    public static <V> GraphSortStrategy<V> kahnTopological() {
        return GraphAlgorithms::topologicalSortKahn;
    }
}
