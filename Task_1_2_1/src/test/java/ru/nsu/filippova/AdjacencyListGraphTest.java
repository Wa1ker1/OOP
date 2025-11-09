package ru.nsu.filippova;

/**
 * Запускает все тесты из AbstractGraphTest
 * для реализации AdjacencyListGraph.
 */
class AdjacencyListGraphTest extends AbstractGraphTest {
    @Override
    protected Graph<String> createGraph() {
        return new AdjacencyListGraph<>();
    }
}
