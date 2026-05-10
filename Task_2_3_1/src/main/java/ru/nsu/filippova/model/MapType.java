package ru.nsu.filippova.model;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Типы игровых карт.
 */
public enum MapType {
    /** Пустая карта без препятствий. */
    EMPTY("Карта 1: Пустая") {
        @Override
        public Set<Cell> buildObstacles(int rows, int columns) {
            return new LinkedHashSet<>();
        }
    },
    /** Карта со стенами по периметру. */
    BORDER("Карта 2: Периметр") {
        @Override
        public Set<Cell> buildObstacles(int rows, int columns) {
            Set<Cell> cells = new LinkedHashSet<>();
            addBorder(cells, rows, columns);
            return cells;
        }
    },
    /** Карта с лабиринтом. */
    LABYRINTH("Карта 3: Лабиринт") {
        @Override
        public Set<Cell> buildObstacles(int rows, int columns) {
            Set<Cell> cells = new LinkedHashSet<>();
            for (int row = 3; row < rows - 3; row += 4) {
                if ((row / 4) % 2 == 0) {
                    addHorizontal(cells, row, 3, columns - 8, rows, columns);
                } else {
                    addHorizontal(cells, row, 7, columns - 4, rows, columns);
                }
            }
            addVertical(cells, 5, 4, rows - 5, rows, columns);
            addVertical(cells, columns - 6, 4, rows - 5, rows, columns);
            for (int row = 6; row < rows - 6; row += 8) {
                cells.remove(new Cell(row, 5));
                cells.remove(new Cell(row + 1, columns - 6));
            }
            return cells;
        }
    };

    private final String title;

    MapType(String title) {
        this.title = title;
    }

    /**
     * Создает препятствия для карты.
     *
     * @param rows количество строк
     * @param columns количество столбцов
     * @return клетки с препятствиями
     */
    public abstract Set<Cell> buildObstacles(int rows, int columns);

    static void addHorizontal(Set<Cell> cells, int row,
                              int startColumn, int endColumn, int rows, int columns) {
        for (int column = startColumn; column <= endColumn; column++) {
            addIfInside(cells, row, column, rows, columns);
        }
    }

    static void addVertical(Set<Cell> cells, int column,
                            int startRow, int endRow, int rows, int columns) {
        for (int row = startRow; row <= endRow; row++) {
            addIfInside(cells, row, column, rows, columns);
        }
    }

    static void addBorder(Set<Cell> cells, int rows, int columns) {
        for (int column = 0; column < columns; column++) {
            cells.add(new Cell(0, column));
            cells.add(new Cell(rows - 1, column));
        }
        for (int row = 0; row < rows; row++) {
            cells.add(new Cell(row, 0));
            cells.add(new Cell(row, columns - 1));
        }
    }

    private static void addIfInside(Set<Cell> cells, int row, int column, int rows, int columns) {
        if (row > 0 && row < rows - 1 && column > 0 && column < columns - 1) {
            cells.add(new Cell(row, column));
        }
    }

    @Override
    public String toString() {
        return title;
    }
}
