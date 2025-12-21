package ru.nsu.filippova;

/**
 * Неизменяемое описание игральной карты.
 */
public class Card {
    private final Suit suit;
    private final Rank rank;

    /**
     * Создает карту указанной масти и достоинства.
     *
     * @param suit масть карты
     * @param rank достоинство карты
     */
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    /**
     * Возвращает масть карты.
     *
     * @return масть
     */
    public Suit getSuit() {
        return suit;
    }

    /**
     * Возвращает достоинство карты.
     *
     * @return достоинство
     */
    public Rank getRank() {
        return rank;
    }

    /**
     * Возвращает базовую стоимость карты в очках.
     *
     * @return стоимость карты
     */
    public int getValue() {
        return rank.getValue();
    }

    /**
     * Возвращает строковое представление карты с локализованными названиями.
     *
     * @return описание карты
     */
    @Override
    public String toString() {
        return rank.getTitle() + " " + suit.getTitle() + " (" + getValue() + ")";
    }
}
