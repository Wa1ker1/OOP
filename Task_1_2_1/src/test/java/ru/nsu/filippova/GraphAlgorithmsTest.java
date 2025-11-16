package ru.nsu.filippova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;






class GraphAlgorithmsTest {

    private Graph<String> graph;

    static Stream<Function<Graph<String>, List<String>>> sortMethods() {
        return Stream.of(
                Graph::sort,
                GraphAlgorithms::topologicalSort,
                GraphAlgorithms::topologicalSortKahn
        );
    }

    @BeforeEach
    void setUp() {
        graph = new AdjacencyListGraph<>();
    }

    /**
     * Вспомогательный метод для проверки корректности топологической сортировки.
     * Проверяет, что для любого ребра (u -> v), u идет раньше v в списке.
     */
    private void assertTopologicalOrder(List<String> sortedList, Graph<String> g) {
        Map<String, Integer> indices = IntStream.range(0, sortedList.size())
                .boxed()
                .collect(Collectors.toMap(sortedList::get, i -> i));

        assertEquals(g.getVertexCount(), sortedList.size());

        for (String u : g.getVertices()) {
            for (String v : g.getNeighbors(u)) {
                assertTrue(indices.containsKey(u), "Вершина " + u + " отсутствует в результате");
                assertTrue(indices.containsKey(v), "Вершина " + v + " отсутствует в результате");

                String message = String.format(
                        "Нарушен порядок: ребро (%s -> %s), "
                                + "но %s (индекс %d) идет после %s (индекс %d)",
                        u, v, u, indices.get(u), v, indices.get(v)
                );
                assertTrue(indices.get(u) < indices.get(v), message);
            }
        }
    }

    @ParameterizedTest
    @MethodSource("sortMethods")
    @DisplayName("Простая линейная сортировка")
    void testTopologicalSort_Linear(Function<Graph<String>, List<String>> sortAlgorithm) {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 1);
        graph.addEdge("C", "D", 1);

        List<String> sorted = sortAlgorithm.apply(graph);
        List<String> expected = List.of("A", "B", "C", "D");
        assertEquals(expected, sorted);
    }

    @ParameterizedTest
    @MethodSource("sortMethods")
    @DisplayName("Сложный DAG (ациклический граф)")
    void testTopologicalSort_ComplexDag(Function<Graph<String>, List<String>> sortAlgorithm) {
        graph.addVertex("Носки");
        graph.addVertex("Обувь");
        graph.addVertex("Брюки");
        graph.addVertex("Ремень");
        graph.addVertex("Рубашка");
        graph.addVertex("Галстук");
        graph.addVertex("Пиджак");

        graph.addEdge("Носки", "Обувь", 1);
        graph.addEdge("Брюки", "Обувь", 1);
        graph.addEdge("Брюки", "Ремень", 1);
        graph.addEdge("Рубашка", "Ремень", 1);
        graph.addEdge("Рубашка", "Галстук", 1);
        graph.addEdge("Ремень", "Пиджак", 1);
        graph.addEdge("Галстук", "Пиджак", 1);

        List<String> sorted = sortAlgorithm.apply(graph);
        assertTopologicalOrder(sorted, graph);
    }

    @ParameterizedTest
    @MethodSource("sortMethods")
    @DisplayName("Несвязный граф")
    void testTopologicalSort_Disconnected(Function<Graph<String>, List<String>> sortAlgorithm) {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.addEdge("A", "B", 1);
        graph.addEdge("C", "D", 1);

        List<String> sorted = sortAlgorithm.apply(graph);
        assertTopologicalOrder(sorted, graph);
    }

    @ParameterizedTest
    @MethodSource("sortMethods")
    @DisplayName("Пустой граф")
    void testTopologicalSort_Empty(Function<Graph<String>, List<String>> sortAlgorithm) {
        List<String> sorted = sortAlgorithm.apply(graph);
        assertTrue(sorted.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("sortMethods")
    @DisplayName("Граф с циклом (должен кидать исключение)")
    void testTopologicalSort_Cycle(Function<Graph<String>, List<String>> sortAlgorithm) {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B", 1);
        graph.addEdge("B", "C", 1);
        graph.addEdge("C", "A", 1);

        assertThrows(IllegalStateException.class, () -> sortAlgorithm.apply(graph));
    }
}
