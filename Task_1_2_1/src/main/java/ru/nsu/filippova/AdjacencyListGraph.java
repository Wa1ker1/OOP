package ru.nsu.filippova;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Реализация графа с использованием списка смежности.
 * Основа - Map, где ключ - вершина, а значение -
 * другая Map (соседи), где ключ - сосед, а значение - вес ребра.
 */
public class AdjacencyListGraph<V> extends AbstractGraph<V> {

    private final Map<V, Map<V, Integer>> adjList;
    private int edgeCount;

    /**
     * Создает пустой граф на основе списка смежности.
     */
    public AdjacencyListGraph() {
        this.adjList = new HashMap<>();
        this.edgeCount = 0;
    }

    @Override
    public boolean addVertex(V vertex) {
        if (containsVertex(vertex)) {
            return false;
        }
        adjList.put(vertex, new HashMap<>());
        return true;
    }

    @Override
    public boolean removeVertex(V vertex) {
        if (!containsVertex(vertex)) {
            return false;
        }

        edgeCount -= adjList.get(vertex).size();
        adjList.remove(vertex);

        for (Map<V, Integer> neighbors : adjList.values()) {
            if (neighbors.remove(vertex) != null) {
                edgeCount--;
            }
        }
        return true;
    }

    @Override
    public boolean addEdge(V source, V destination, Integer weight) {
        if (!containsVertex(source) || !containsVertex(destination)) {
            throw new IllegalArgumentException("Vertex not found in graph.");
        }
        if (containsEdge(source, destination)) {
            throw new IllegalStateException("Edge already exists.");
        }

        adjList.get(source).put(destination, weight);
        edgeCount++;
        return true;
    }

    @Override
    public boolean removeEdge(V source, V destination) {
        if (!containsVertex(source) || !containsVertex(destination)) {
            return false;
        }
        Map<V, Integer> neighbors = adjList.get(source);
        if (neighbors != null && neighbors.remove(destination) != null) {
            edgeCount--;
            return true;
        }
        return false;
    }

    @Override
    public Integer getEdgeWeight(V source, V destination) {
        if (!containsVertex(source)) {
            return null;
        }
        return adjList.get(source).get(destination);
    }

    @Override
    public boolean containsVertex(V vertex) {
        return adjList.containsKey(vertex);
    }

    @Override
    public boolean containsEdge(V source, V destination) {
        return containsVertex(source) && adjList.get(source).containsKey(destination);
    }

    @Override
    public Set<V> getNeighbors(V vertex) {
        if (!containsVertex(vertex)) {
            throw new IllegalArgumentException("Vertex not found: " + vertex);
        }
        return new HashSet<>(adjList.get(vertex).keySet());
    }

    @Override
    public Set<V> getVertices() {
        return new HashSet<>(adjList.keySet());
    }

    @Override
    public int getVertexCount() {
        return adjList.size();
    }

    @Override
    public int getEdgeCount() {
        return edgeCount;
    }

    @Override
    public void readFromFile(String filePath) throws IOException {
        this.adjList.clear();
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
