package ru.nsu.filippova;

/**
 * Запускает все тесты из AbstractGraphTest
 * для реализации IncidenceMatrixGraph.
 */
class IncidenceMatrixGraphTest extends AbstractGraphTest {
    @Override
    protected Graph<String> createGraph() {
        return new IncidenceMatrixGraph<>();
    }
}
