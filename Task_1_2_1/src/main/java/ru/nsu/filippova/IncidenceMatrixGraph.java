package ru.nsu.filippova;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Реализация графа с использованием матрицы инцидентности.
 * Строки - вершины, столбцы - ребра.
 * matrix[i][j] = 1, если вершина i - ИСТОЧНИК ребра j.
 * matrix[i][j] = -1, если вершина i - НАЗНАЧЕНИЕ ребра j.
 * matrix[i][j] = 0, в остальных случаях.
 * <p>
 * Веса ребер хранятся отдельно, т.к. матрица инцидентности их не содержит.
 *
 * @param <V> Тип вершин
 * @param <E> Тип веса ребер
 */
public class IncidenceMatrixGraph<V, E> extends AbstractGraph<V, E> {
    private static class Edge<V, E> {
        final V source;
        final V destination;
        E weight;

        Edge(V source, V destination, E weight) {
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Edge<?, ?> edge = (Edge<?, ?>) o;
            return Objects.equals(source, edge.source) &&
                    Objects.equals(destination, edge.destination);
        }

        @Override
        public int hashCode() {
            return Objects.hash(source, destination);
        }
    }

    private final Map<V, Integer> vertexToIndex;
    private final List<V> indexToVertex;
    private final List<Edge<V, E>> edges;
    private List<List<Integer>> matrix;

    /**
     * Создает пустой граф на основе матрицы инцидентности.
     */
    public IncidenceMatrixGraph() {
        this.vertexToIndex = new HashMap<>();
        this.indexToVertex = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.matrix = new ArrayList<>();
    }

    @Override
    public boolean addVertex(V vertex) {
        if (containsVertex(vertex)) {
            return false;
        }
        int newIndex = indexToVertex.size();
        vertexToIndex.put(vertex, newIndex);
        indexToVertex.add(vertex);

        List<Integer> newRow = new ArrayList<>(edges.size());
        for (int i = 0; i < edges.size(); i++) {
            newRow.add(0);
        }
        matrix.add(newRow);
        return true;
    }

    @Override
    public boolean removeVertex(V vertex) {
        Integer indexToRemove = vertexToIndex.get(vertex);
        if (indexToRemove == null) {
            return false;
        }

        List<Edge<V, E>> toRemove = new ArrayList<>();
        for (Edge<V, E> edge : edges) {
            if (edge.source.equals(vertex) || edge.destination.equals(vertex)) {
                toRemove.add(edge);
            }
        }

        List<Edge<V, E>> newEdges = new ArrayList<>();
        List<List<Integer>> newMatrix = new ArrayList<>();
        for (Edge<V, E> edge : toRemove) {
            removeEdge(edge.source, edge.destination);
        }

        matrix.remove(indexToRemove.intValue());

        vertexToIndex.clear();
        indexToVertex.remove(indexToRemove.intValue());
        for (int i = 0; i < indexToVertex.size(); i++) {
            vertexToIndex.put(indexToVertex.get(i), i);
        }
        return true;
    }

    @Override
    public boolean addEdge(V source, V destination, E weight) {
        Integer srcIdx = vertexToIndex.get(source);
        Integer destIdx = vertexToIndex.get(destination);

        if (srcIdx == null || destIdx == null) {
            throw new IllegalArgumentException("Vertex not found in graph.");
        }
        if (containsEdge(source, destination)) {
            throw new IllegalStateException("Edge already exists.");
        }

        Edge<V, E> newEdge = new Edge<>(source, destination, weight);
        edges.add(newEdge);

        for (int i = 0; i < indexToVertex.size(); i++) {
            List<Integer> row = matrix.get(i);
            if (i == srcIdx) {
                row.add(1);
            } else if (i == destIdx) {
                row.add(-1);
            } else {
                row.add(0);
            }
        }
        return true;
    }

    @Override
    public boolean removeEdge(V source, V destination) {
        int edgeIndex = -1;
        for (int i = 0; i < edges.size(); i++) {
            Edge<V, E> edge = edges.get(i);
            if (edge.source.equals(source) && edge.destination.equals(destination)) {
                edgeIndex = i;
                break;
            }
        }

        if (edgeIndex == -1) {
            return false;
        }

        edges.remove(edgeIndex);

        for (List<Integer> row : matrix) {
            row.remove(edgeIndex);
        }
        return true;
    }

    private Edge<V, E> findEdge(V source, V destination) {
        for (Edge<V, E> edge : edges) {
            if (edge.source.equals(source) && edge.destination.equals(destination)) {
                return edge;
            }
        }
        return null;
    }


    @Override
    public E getEdgeWeight(V source, V destination) {
        Edge<V, E> edge = findEdge(source, destination);
        return (edge != null) ? edge.weight : null;
    }

    @Override
    public boolean containsVertex(V vertex) {
        return vertexToIndex.containsKey(vertex);
    }

    @Override
    public boolean containsEdge(V source, V destination) {
        return findEdge(source, destination) != null;
    }

    @Override
    public Set<V> getNeighbors(V vertex) {
        Integer srcIdx = vertexToIndex.get(vertex);
        if (srcIdx == null) {
            throw new IllegalArgumentException("Vertex not found: " + vertex);
        }

        Set<V> neighbors = new HashSet<>();
        List<Integer> row = matrix.get(srcIdx);
        for (int j = 0; j < row.size(); j++) {
            if (row.get(j) == 1) {
                Edge<V, E> edge = edges.get(j);
                neighbors.add(edge.destination);
            }
        }
        return neighbors;
    }

    @Override
    public Set<V> getVertices() {
        return new HashSet<>(indexToVertex);
    }

    @Override
    public int getVertexCount() {
        return indexToVertex.size();
    }

    @Override
    public int getEdgeCount() {
        return edges.size();
    }

    @Override
    public void readFromFile(String filePath) throws IOException, ClassCastException {
        this.matrix.clear();
        this.vertexToIndex.clear();
        this.indexToVertex.clear();
        this.edges.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            int numVertices = Integer.parseInt(reader.readLine());
            for (int i = 0; i < numVertices; i++) {
                V vertex = (V) reader.readLine();
                this.addVertex(vertex);
            }

            int numEdges = Integer.parseInt(reader.readLine());
            for (int i = 0; i < numEdges; i++) {
                line = reader.readLine();
                String[] parts = line.split(" ");
                if (parts.length != 3) {
                    throw new IllegalStateException("Invalid edge format: " + line);
                }
                V source = (V) parts[0];
                V dest = (V) parts[1];
                E weight = (E) Double.valueOf(parts[2]);

                if (!this.containsVertex(source) || !this.containsVertex(dest)) {
                    throw new IllegalStateException("Edge refers to non-existent vertex: " + line);
                }

                this.addEdge(source, dest, weight);
            }
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid file format (expected numbers).", e);
        }
    }
}