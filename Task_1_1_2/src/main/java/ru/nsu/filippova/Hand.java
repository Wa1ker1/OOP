package ru.nsu.filippova;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Хранит карты, принадлежащие участнику игры.
 */
public class Hand {
    private final List<Card> cards = new ArrayList<>();

    /**
     * Добавляет карту в руку.
     *
     * @param card карта для добавления
     */
    public void add(Card card) {
        cards.add(card);
    }

    /**
     * Очищает руку.
     */
    public void clear() {
        cards.clear();
    }

    /**
     * Возвращает список карт в руке без возможности изменения.
     *
     * @return неизменяемый список карт
     */
    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    /**
     * Считает сумму очков с учетом гибкой стоимости тузов.
     *
     * @return сумма очков
     */
    public int getValue() {
        int sum = 0;
        int aces = 0;
        for (Card card : cards) {
            sum += card.getValue();
            if (card.getRank() == Rank.ACE) {
                aces++;
            }
        }
        while (sum > 21 && aces > 0) {
            sum -= 10;
            aces--;
        }
        return sum;
    }

    /**
     * Проверяет, является ли рука блэкджеком.
     *
     * @return {@code true}, если две карты дают 21 очко
     */
    public boolean isBlackjack() {
        return cards.size() == 2 && getValue() == 21;
    }

    /**
     * Проверяет, превышает ли сумма 21 очко.
     *
     * @return {@code true}, если рука перебрала
     */
    public boolean isBust() {
        return getValue() > 21;
    }

    /**
     * Формирует строковое представление руки.
     *
     * @param hideSecondCard скрывать ли вторую карту
     * @return строковое представление руки
     */
    public String format(boolean hideSecondCard) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < cards.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            if (hideSecondCard && i == 1) {
                builder.append("<закрытая карта>");
            } else {
                builder.append(cards.get(i));
            }
        }
        builder.append("]");
        return builder.toString();
    }
}
