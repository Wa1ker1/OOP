package ru.nsu.filippova;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Абстрактный класс для тестирования контракта интерфейса Graph.
 * Любая реализация Graph должна проходить эти тесты.
 */
public abstract class AbstractGraphTest {

    protected Graph<String> graph;

    /**
     * Этот метод должны реализовать подклассы,
     * предоставляя конкретную реализацию графа для тестирования.
     *
     * @return пустой граф.
     */
    protected abstract Graph<String> createGraph();

    @BeforeEach
    void setUp() {
        graph = createGraph();
    }

    @Test
    @DisplayName("Добавление вершин")
    void testAddVertex() {
        assertTrue(graph.addVertex("A"));
        assertEquals(1, graph.getVertexCount());
        assertTrue(graph.containsVertex("A"));
        assertEquals(Set.of("A"), graph.getVertices());

        assertFalse(graph.addVertex("A"));
        assertEquals(1, graph.getVertexCount());
    }

    @Test
    @DisplayName("Удаление вершин")
    void testRemoveVertex() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 2);
        graph.addEdge("C", "A", 3);

        assertEquals(3, graph.getVertexCount());
        assertEquals(3, graph.getEdgeCount());

        assertTrue(graph.removeVertex("B"));
        assertEquals(2, graph.getVertexCount());
        assertFalse(graph.containsVertex("B"));

        assertEquals(1, graph.getEdgeCount());
        assertFalse(graph.containsEdge("A", "B"));
        assertFalse(graph.containsEdge("B", "C"));
        assertTrue(graph.containsEdge("C", "A"));

        assertFalse(graph.removeVertex("B"));
        assertFalse(graph.removeVertex("Z"));
    }

    @Test
    @DisplayName("Добавление ребер")
    void testAddEdge() {
        graph.addVertex("A");
        graph.addVertex("B");

        assertTrue(graph.addEdge("A", "B", 15));
        assertEquals(1, graph.getEdgeCount());
        assertTrue(graph.containsEdge("A", "B"));
        assertFalse(graph.containsEdge("B", "A"));

        assertEquals(15, graph.getEdgeWeight("A", "B"));
        assertNull(graph.getEdgeWeight("B", "A"));
    }

    @Test
    @DisplayName("Исключения при добавлении ребер")
    void testAddEdgeExceptions() {
        graph.addVertex("A");
        graph.addVertex("B");

        assertThrows(IllegalArgumentException.class, () -> graph.addEdge("A", "Z", 1));
        assertThrows(IllegalArgumentException.class, () -> graph.addEdge("Z", "A", 1));

        graph.addEdge("A", "B", 1);
        assertThrows(IllegalStateException.class, () -> graph.addEdge("A", "B", 2));
    }

    @Test
    @DisplayName("Удаление ребер")
    void testRemoveEdge() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addEdge("A", "B", 1);

        assertTrue(graph.removeEdge("A", "B"));
        assertEquals(0, graph.getEdgeCount());
        assertFalse(graph.containsEdge("A", "B"));

        assertFalse(graph.removeEdge("A", "B"));
        assertFalse(graph.removeEdge("B", "A"));
    }

    @Test
    @DisplayName("Получение соседей (исходящие)")
    void testGetNeighbors() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");

        graph.addEdge("A", "B", 1);
        graph.addEdge("A", "C", 2);
        graph.addEdge("C", "A", 3);

        Set<String> neighborsA = graph.getNeighbors("A");
        assertEquals(Set.of("B", "C"), neighborsA);

        Set<String> neighborsB = graph.getNeighbors("B");
        assertTrue(neighborsB.isEmpty());

        Set<String> neighborsD = graph.getNeighbors("D");
        assertTrue(neighborsD.isEmpty());

        assertThrows(IllegalArgumentException.class, () -> graph.getNeighbors("Z"));
    }

    @Test
    @DisplayName("Тестирование equals и hashCode (из AbstractGraph)")
    void testEqualsAndHashCode() {
        Graph<String> graph2 = createGraph();

        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B", 1);

        graph2.addVertex("A");
        graph2.addVertex("B");
        graph2.addVertex("C");
        graph2.addEdge("A", "B", 1);


        assertEquals(graph, graph2);
        assertEquals(graph.hashCode(), graph2.hashCode());

        graph2.addEdge("B", "C", 2);
        assertNotEquals(graph, graph2);

        graph.addEdge("B", "C", 2);
        assertEquals(graph, graph2);
        assertEquals(graph.hashCode(), graph2.hashCode());

        Graph<String> graph3 = createGraph();
        graph3.addVertex("A");
        graph3.addVertex("B");
        graph3.addVertex("C");
        graph3.addEdge("A", "B", 1);
        graph3.addEdge("B", "C", 99);
        assertNotEquals(graph, graph3);

        assertNotEquals(graph, null);
        assertNotEquals(graph, new Object());
    }

    @Test
    @DisplayName("Тестирование toString (из AbstractGraph)")
    void testToString() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addEdge("A", "B", 15);

        String str = graph.toString();

        assertTrue(str.contains(graph.getClass().getSimpleName()));

        assertTrue(str.contains("A -> {B (15)}"));
        assertTrue(str.contains("B -> {}"));
    }

    @Test
    @DisplayName("Чтение из файла")
    void testReadFromFile(@TempDir Path tempDir) throws IOException {
        String content = "3\n" +
                "V1\n" +
                "V2\n" +
                "V3\n" +
                "2\n" +
                "V1 V2 10\n" +
                "V2 V3 20\n";

        File tempFile = tempDir.resolve("testGraph.txt").toFile();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(content);
        }

        graph.readFromFile(tempFile.getAbsolutePath());

        assertEquals(3, graph.getVertexCount());
        assertEquals(2, graph.getEdgeCount());
        assertTrue(graph.containsVertex("V1"));
        assertTrue(graph.containsVertex("V2"));
        assertTrue(graph.containsVertex("V3"));
        assertTrue(graph.containsEdge("V1", "V2"));
        assertTrue(graph.containsEdge("V2", "V3"));
        assertEquals(10, graph.getEdgeWeight("V1", "V2"));
        assertEquals(20, graph.getEdgeWeight("V2", "V3"));
    }

    @Test
    @DisplayName("Исключения при чтении файла")
    void testReadFromFileExceptions(@TempDir Path tempDir) throws IOException {

        String badNumber = "3\nA\nB\nC\nNotANumber\n";
        File file1 = tempDir.resolve("badNum.txt").toFile();
        try (FileWriter writer = new FileWriter(file1)) {
            writer.write(badNumber);
        }
        assertThrows(IllegalStateException.class, () -> graph.readFromFile(file1.getAbsolutePath()));

        String badEdge = "2\nA\nB\n1\nA B\n";
        File file2 = tempDir.resolve("badEdge.txt").toFile();
        try (FileWriter writer = new FileWriter(file2)) {
            writer.write(badEdge);
        }
        assertThrows(IllegalStateException.class, () -> graph.readFromFile(file2.getAbsolutePath()));


        String ghostVertex = "2\nA\nB\n1\nA Z 1\n";
        File file3 = tempDir.resolve("ghost.txt").toFile();
        try (FileWriter writer = new FileWriter(file3)) {
            writer.write(ghostVertex);
        }
        assertThrows(IllegalStateException.class, () -> graph.readFromFile(file3.getAbsolutePath()));

        assertThrows(IOException.class, () -> graph.readFromFile("non_existent_file.txt"));
    }
}
