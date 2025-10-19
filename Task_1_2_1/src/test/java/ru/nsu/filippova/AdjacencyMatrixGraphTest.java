package ru.nsu.filippova;

/**
 * Запускает все тесты из AbstractGraphTest
 * для реализации AdjacencyMatrixGraph.
 */
class AdjacencyMatrixGraphTest extends AbstractGraphTest {
    @Override
    protected Graph<String, Double> createGraph() {
        return new AdjacencyMatrixGraph<>();
    }
}