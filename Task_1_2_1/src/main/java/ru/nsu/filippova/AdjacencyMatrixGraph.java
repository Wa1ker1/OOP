package ru.nsu.filippova;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Реализация графа с использованием матрицы смежности.
 * Так как тип вершин V - дженерик, мы используем Map для
 * отображения V -> Integer (индекс) и List для Integer -> V.
 * Сама матрица хранит веса Integer.
 */
public class AdjacencyMatrixGraph<V> extends AbstractGraph<V> {

    private final List<List<Integer>> matrix;
    private final Map<V, Integer> vertexToIndex;
    private final List<V> indexToVertex;
    private int edgeCount;

    /**
     * Создает пустой граф на основе матрицы смежности.
     */
    public AdjacencyMatrixGraph() {
        this.matrix = new ArrayList<>();
        this.vertexToIndex = new HashMap<>();
        this.indexToVertex = new ArrayList<>();
        this.edgeCount = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addVertex(V vertex) {
        if (containsVertex(vertex)) {
            return false;
        }

        int newIndex = indexToVertex.size();
        vertexToIndex.put(vertex, newIndex);
        indexToVertex.add(vertex);

        for (List<Integer> row : matrix) {
            row.add(null);
        }

        List<Integer> newRow = new ArrayList<>(indexToVertex.size());
        for (int i = 0; i < indexToVertex.size(); i++) {
            newRow.add(null);
        }
        matrix.add(newRow);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeVertex(V vertex) {
        Integer indexToRemove = vertexToIndex.get(vertex);
        if (indexToRemove == null) {
            return false;
        }

        int size = indexToVertex.size();
        for (int i = 0; i < size; i++) {
            if (matrix.get(indexToRemove).get(i) != null) {
                edgeCount--;
            }
            if (i != indexToRemove && matrix.get(i).get(indexToRemove) != null) {
                edgeCount--;
            }
        }

        matrix.remove(indexToRemove.intValue());
        for (List<Integer> row : matrix) {
            row.remove(indexToRemove.intValue());
        }


        indexToVertex.remove(indexToRemove.intValue());

        vertexToIndex.clear();

        for (int i = 0; i < indexToVertex.size(); i++) {
            vertexToIndex.put(indexToVertex.get(i), i);
        }

        return true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addEdge(V source, V destination, Integer weight) {
        Integer srcIdx = vertexToIndex.get(source);
        Integer destIdx = vertexToIndex.get(destination);

        if (srcIdx == null || destIdx == null) {
            throw new IllegalArgumentException("Vertex not found in graph.");
        }
        if (matrix.get(srcIdx).get(destIdx) != null) {
            throw new IllegalStateException("Edge already exists.");
        }

        matrix.get(srcIdx).set(destIdx, weight);
        edgeCount++;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeEdge(V source, V destination) {
        Integer srcIdx = vertexToIndex.get(source);
        Integer destIdx = vertexToIndex.get(destination);

        if (srcIdx == null || destIdx == null || matrix.get(srcIdx).get(destIdx) == null) {
            return false;
        }

        matrix.get(srcIdx).set(destIdx, null);
        edgeCount--;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getEdgeWeight(V source, V destination) {
        Integer srcIdx = vertexToIndex.get(source);
        Integer destIdx = vertexToIndex.get(destination);

        if (srcIdx == null || destIdx == null) {
            return null;
        }
        return matrix.get(srcIdx).get(destIdx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsVertex(V vertex) {
        return vertexToIndex.containsKey(vertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsEdge(V source, V destination) {
        Integer srcIdx = vertexToIndex.get(source);
        Integer destIdx = vertexToIndex.get(destination);
        return srcIdx != null && destIdx != null && matrix.get(srcIdx).get(destIdx) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<V> getNeighbors(V vertex) {
        Integer srcIdx = vertexToIndex.get(vertex);
        if (srcIdx == null) {
            throw new IllegalArgumentException("Vertex not found: " + vertex);
        }

        Set<V> neighbors = new HashSet<>();
        List<Integer> row = matrix.get(srcIdx);
        for (int j = 0; j < row.size(); j++) {
            if (row.get(j) != null) {
                neighbors.add(indexToVertex.get(j));
            }
        }
        return neighbors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<V> getVertices() {
        return new HashSet<>(indexToVertex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getVertexCount() {
        return indexToVertex.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getEdgeCount() {
        return edgeCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readFromFile(String filePath) throws IOException {
        this.matrix.clear();
        this.vertexToIndex.clear();
        this.indexToVertex.clear();
        this.edgeCount = 0;

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
                Integer weight = Integer.valueOf(parts[2]);

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
