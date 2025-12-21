package ru.nsu.filippova;

/**
 * Базовый класс для игрока и дилера.
 */
public abstract class Participant {
    private final String name;
    private final Hand hand = new Hand();

    /**
     * Создает участника с указанным именем.
     *
     * @param name отображаемое имя участника
     */
    protected Participant(String name) {
        this.name = name;
    }

    /**
     * Возвращает имя участника.
     *
     * @return имя участника
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает руку участника.
     *
     * @return рука участника
     */
    public Hand getHand() {
        return hand;
    }

    /**
     * Добавляет карту в руку участника.
     *
     * @param card карта для добавления
     */
    public void takeCard(Card card) {
        hand.add(card);
    }

    /**
     * Очищает руку участника.
     */
    public void resetHand() {
        hand.clear();
    }
}
