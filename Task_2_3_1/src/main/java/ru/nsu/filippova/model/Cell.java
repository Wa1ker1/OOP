package ru.nsu.filippova.model;

/**
 * Координата одной клетки игрового поля.
 *
 * @param row номер строки
 * @param column номер столбца
 */
public record Cell(int row, int column) {
    /**
     * Возвращает соседнюю клетку в указанном направлении без учета границ поля.
     *
     * @param direction направление движения
     * @return новая координата после сдвига
     */
    public Cell move(Direction direction) {
        return new Cell(row + direction.rowDelta(), column + direction.columnDelta());
    }

    /**
     * Считает манхэттенское расстояние до другой клетки.
     *
     * @param other другая клетка
     * @return расстояние по вертикали и горизонтали
     */
    public int distanceTo(Cell other) {
        return Math.abs(row - other.row) + Math.abs(column - other.column);
    }
}
