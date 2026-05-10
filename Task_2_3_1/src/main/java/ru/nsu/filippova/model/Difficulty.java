package ru.nsu.filippova.model;

/**
 * Уровень сложности игры.
 *
 * <p>Сложность задает задержку между шагами змейки: чем меньше задержка,
 * тем быстрее идет игра.</p>
 */
public enum Difficulty {
    /** Медленная скорость. */
    EASY("Легкий", 190),
    /** Средняя скорость. */
    MEDIUM("Средний", 130),
    /** Высокая скорость. */
    HARD("Сложный", 85);

    private final String title;
    private final long tickMillis;

    Difficulty(String title, long tickMillis) {
        this.title = title;
        this.tickMillis = tickMillis;
    }

    /**
     * Возвращает задержку одного игрового шага в наносекундах.
     *
     * @return длительность шага в наносекундах
     */
    public long tickNanos() {
        return tickMillis * 1_000_000L;
    }

    /**
     * Возвращает отображаемое название сложности.
     *
     * @return русское название сложности
     */
    @Override
    public String toString() {
        return title;
    }
}
