package ru.nsu.filippova;

import java.util.Objects;
import java.util.Set;

/**
 * Абстрактный базовый класс для реализаций графа.
 * Предоставляет общую реализацию методов equals, hashCode и toString,
 * основанную на методах интерфейса Graph.
 *
 * @param <V> Тип вершин
 */
public abstract class AbstractGraph<V> implements Graph<V> {

    /**
     * Сравнивает этот граф с другим объектом на равенство.
     * Два графа равны, если они имеют одинаковый набор вершин и
     * одинаковый набор ребер с одинаковыми весами.
     *
     * @param obj объект для сравнения.
     * @return true, если графы равны, false в противном случае.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Graph)) {
            return false;
        }

        @SuppressWarnings("unchecked")
        Graph<V> other = (Graph<V>) obj;

        if (this.getVertexCount() != other.getVertexCount()
                || this.getEdgeCount() != other.getEdgeCount()) {
            return false;
        }

        Set<V> thisVertices = this.getVertices();
        Set<V> otherVertices = other.getVertices();
        if (!thisVertices.equals(otherVertices)) {
            return false;
        }

        try {
            for (V u : thisVertices) {
                Set<V> thisNeighbors = this.getNeighbors(u);
                Set<V> otherNeighbors = other.getNeighbors(u);
                if (!thisNeighbors.equals(otherNeighbors)) {
                    return false;
                }
                for (V v : thisNeighbors) {
                    if (!Objects.equals(this.getEdgeWeight(u, v), other.getEdgeWeight(u, v))) {
                        return false;
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }

    /**
     * Вычисляет хэш-код для графа.
     * Основан на наборе вершин и количестве ребер.
     *
     * @return хэш-код.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getVertices(), getEdgeCount());
    }

    /**
     * Возвращает строковое представление графа.
     *
     * @return строка вида "V1 -> {V2 (Weight), ...}\n...".
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph [").append(getClass().getSimpleName()).append("]:\n");

        for (V u : this.getVertices()) {
            sb.append("  ").append(u).append(" -> {");
            Set<V> neighbors = this.getNeighbors(u);
            if (neighbors.isEmpty()) {
                sb.append("}\n");
                continue;
            }

            int count = 0;
            for (V v : neighbors) {
                Integer weight = this.getEdgeWeight(u, v);
                sb.append(v).append(" (").append(weight).append(")");
                if (++count < neighbors.size()) {
                    sb.append(", ");
                }
            }
            sb.append("}\n");
        }
        return sb.toString();
    }
}
