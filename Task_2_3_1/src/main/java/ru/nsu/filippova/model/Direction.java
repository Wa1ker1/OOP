package ru.nsu.filippova.model;

/**
 * Направление движения змейки по клеточному полю.
 */
public enum Direction {
    /** Вверх на одну строку. */
    UP(-1, 0),
    /** Вправо на один столбец. */
    RIGHT(0, 1),
    /** Вниз на одну строку. */
    DOWN(1, 0),
    /** Влево на один столбец. */
    LEFT(0, -1);

    private final int rowDelta;
    private final int columnDelta;

    Direction(int rowDelta, int columnDelta) {
        this.rowDelta = rowDelta;
        this.columnDelta = columnDelta;
    }

    /**
     * Возвращает изменение строки при движении в этом направлении.
     *
     * @return -1, 0 или 1
     */
    public int rowDelta() {
        return rowDelta;
    }

    /**
     * Возвращает изменение столбца при движении в этом направлении.
     *
     * @return -1, 0 или 1
     */
    public int columnDelta() {
        return columnDelta;
    }

    /**
     * Проверяет, является ли другое направление противоположным текущему.
     *
     * @param direction проверяемое направление
     * @return {@code true}, если направления противоположны
     */
    public boolean isOpposite(Direction direction) {
        return rowDelta + direction.rowDelta == 0 && columnDelta + direction.columnDelta == 0;
    }
}
