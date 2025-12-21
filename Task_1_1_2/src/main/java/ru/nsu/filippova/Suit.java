package ru.nsu.filippova;

/**
 * Описывает масти карт и их локализованные названия.
 */
public enum Suit {
    CLUBS("Трефы"),
    DIAMONDS("Бубны"),
    HEARTS("Червы"),
    SPADES("Пики");

    private final String title;

    Suit(String title) {
        this.title = title;
    }

    /**
     * Возвращает локализованное название масти.
     *
     * @return название масти
     */
    public String getTitle() {
        return title;
    }
}
