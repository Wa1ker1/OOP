package ru.nsu.filippova;

import java.security.SecureRandom;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * Представляет одну или несколько перемешанных колод карт.
 */
public class Deck {
    private static final SecureRandom RANDOM = new SecureRandom();

    private final int decksCount;
    private final Deque<Card> cards = new ArrayDeque<>();

    /**
     * Создает колоду из указанного числа стандартных колод.
     *
     * @param decksCount количество колод
     */
    public Deck(int decksCount) {
        if (decksCount <= 0) {
            throw new IllegalArgumentException("Количество колод должно быть положительным");
        }
        this.decksCount = decksCount;
        shuffle();
    }

    /**
     * Перемешивает все карты и обновляет порядок выдачи.
     */
    public void shuffle() {
        List<Card> list = new ArrayList<>();
        for (int i = 0; i < decksCount; i++) {
            for (Suit suit : Suit.values()) {
                for (Rank rank : Rank.values()) {
                    list.add(new Card(suit, rank));
                }
            }
        }
        Collections.shuffle(list, RANDOM);
        cards.clear();
        cards.addAll(list);
    }

    /**
     * Выдает верхнюю карту из колоды.
     *
     * @return выданная карта
     */
    public Card draw() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("В колоде закончились карты");
        }
        return cards.removeFirst();
    }

    /**
     * Возвращает количество оставшихся карт.
     *
     * @return число карт в колоде
     */
    public int remainingCards() {
        return cards.size();
    }
}
