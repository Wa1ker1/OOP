package ru.nsu.filippova;

/**
 * Описывает достоинства карт и их базовые значения.
 */
public enum Rank {
    TWO("Двойка", 2),
    THREE("Тройка", 3),
    FOUR("Четверка", 4),
    FIVE("Пятерка", 5),
    SIX("Шестерка", 6),
    SEVEN("Семерка", 7),
    EIGHT("Восьмерка", 8),
    NINE("Девятка", 9),
    TEN("Десятка", 10),
    JACK("Валет", 10),
    QUEEN("Дама", 10),
    KING("Король", 10),
    ACE("Туз", 11);

    private final String title;
    private final int value;

    Rank(String title, int value) {
        this.title = title;
        this.value = value;
    }

    /**
     * Возвращает локализованное название достоинства.
     *
     * @return название достоинства
     */
    public String getTitle() {
        return title;
    }

    /**
     * Возвращает базовое значение достоинства в очках.
     *
     * @return значение достоинства
     */
    public int getValue() {
        return value;
    }
}
