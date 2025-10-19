package ru.nsu.filippova;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Класс, содержащий статические методы для выполнения
 * алгоритмов на графах.
 */
public class GraphAlgorithms {

    /**
     * Выполняет топологическую сортировку вершин графа.
     * Использует алгоритм, основанный на поиске в глубину (DFS).
     *
     * @param graph граф для сортировки.
     * @param <V>   Тип вершин.
     * @param <E>   Тип веса ребер.
     * @return {@link List} вершин в порядке топологической сортировки.
     * @throws IllegalStateException если в графе обнаружен цикл.
     */
    public static <V, E> List<V> topologicalSort(Graph<V, E> graph) {
        LinkedList<V> sortedList = new LinkedList<>();
        Set<V> visited = new HashSet<>();
        Set<V> recursionStack = new HashSet<>();

        for (V vertex : graph.getVertices()) {
            if (!visited.contains(vertex)) {
                topologicalSortDfs(vertex, graph, visited, recursionStack, sortedList);
            }
        }

        return sortedList;
    }

    /**
     * Вспомогательный рекурсивный метод DFS для топологической сортировки.
     */
    private static <V, E> void topologicalSortDfs(
            V vertex,
            Graph<V, E> graph,
            Set<V> visited,
            Set<V> recursionStack,
            LinkedList<V> sortedList
    ) {
        visited.add(vertex);
        recursionStack.add(vertex);

        for (V neighbor : graph.getNeighbors(vertex)) {
            if (!visited.contains(neighbor)) {
                topologicalSortDfs(neighbor, graph, visited, recursionStack, sortedList);
            } else if (recursionStack.contains(neighbor)) {
                throw new IllegalStateException("Graph contains a cycle! Topological sort impossible.");
            }
        }

        recursionStack.remove(vertex);
        sortedList.addFirst(vertex);
    }

    /**
     * Альтернативная реализация: Топологическая сортировка по алгоритму Кана
     * (на основе подсчета входящих степеней).
     *
     * @param graph граф для сортировки.
     * @param <V>   Тип вершин.
     * @param <E>   Тип веса ребер.
     * @return {@link List} вершин в порядке топологической сортировки.
     * @throws IllegalStateException если в графе обнаружен цикл.
     */
    public static <V, E> List<V> topologicalSortKahn(Graph<V, E> graph) {
        Map<V, Integer> inDegree = new HashMap<>();
        for (V v : graph.getVertices()) {
            inDegree.put(v, 0);
        }
        for (V u : graph.getVertices()) {
            for (V v : graph.getNeighbors(u)) {
                inDegree.put(v, inDegree.get(v) + 1);
            }
        }

        LinkedList<V> queue = new LinkedList<>();
        for (V v : graph.getVertices()) {
            if (inDegree.get(v) == 0) {
                queue.add(v);
            }
        }

        List<V> sortedList = new LinkedList<>();
        int visitedCount = 0;

        while (!queue.isEmpty()) {
            V u = queue.poll();
            sortedList.add(u);
            visitedCount++;

            for (V v : graph.getNeighbors(u)) {
                int newInDegree = inDegree.get(v) - 1;
                inDegree.put(v, newInDegree);
                if (newInDegree == 0) {
                    queue.add(v);
                }
            }
        }

        if (visitedCount != graph.getVertexCount()) {
            throw new IllegalStateException("Graph contains a cycle! Topological sort impossible.");
        }

        return sortedList;
    }
}