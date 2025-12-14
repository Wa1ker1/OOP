package ru.nsu.filippova;

import java.util.Objects;

/**
 * Represents a directed edge with optional weight.
 *
 * @param <V> vertex type
 */
public class Edge<V> {
    final V source;
    final V destination;
    Integer weight;

    /**
     * Создает ориентированное ребро с указанными вершинами и весом.
     *
     * @param source      исходная вершина.
     * @param destination конечная вершина.
     * @param weight      вес ребра (может быть null).
     */
    public Edge(V source, V destination, Integer weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Edge<?> edge = (Edge<?>) o;
        return Objects.equals(source, edge.source)
                && Objects.equals(destination, edge.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, destination);
    }
}
