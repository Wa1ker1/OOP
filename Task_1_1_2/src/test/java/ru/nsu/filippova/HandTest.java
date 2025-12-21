package ru.nsu.filippova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;


class HandTest {

    @Test
    void valueTreatsAcesAsOneWhenNecessary() {
        Hand hand = new Hand();
        hand.add(new Card(Suit.HEARTS, Rank.ACE));
        hand.add(new Card(Suit.CLUBS, Rank.NINE));
        hand.add(new Card(Suit.SPADES, Rank.ACE));
        assertEquals(21, hand.getValue());
    }

    @Test
    void blackjackRequiresTwoCardsWorthTwentyOne() {
        Hand hand = new Hand();
        hand.add(new Card(Suit.HEARTS, Rank.ACE));
        hand.add(new Card(Suit.CLUBS, Rank.KING));
        assertTrue(hand.isBlackjack());
        hand.add(new Card(Suit.DIAMONDS, Rank.TWO));
        assertFalse(hand.isBlackjack());
    }

    @Test
    void detectsBustWhenValueAboveTwentyOne() {
        Hand hand = new Hand();
        hand.add(new Card(Suit.HEARTS, Rank.KING));
        hand.add(new Card(Suit.CLUBS, Rank.QUEEN));
        hand.add(new Card(Suit.SPADES, Rank.JACK));
        assertTrue(hand.isBust());
    }

    @Test
    void clearRemovesAllCards() {
        Hand hand = new Hand();
        hand.add(new Card(Suit.SPADES, Rank.TEN));
        hand.clear();
        assertTrue(hand.getCards().isEmpty());
    }

    @Test
    void exposedCardsListIsUnmodifiable() {
        Hand hand = new Hand();
        hand.add(new Card(Suit.HEARTS, Rank.FIVE));
        List<Card> cards = hand.getCards();
        assertThrows(UnsupportedOperationException.class,
                () -> cards.add(new Card(Suit.CLUBS, Rank.NINE)));
    }

    @Test
    void formatCanHideHoleCard() {
        Hand hand = new Hand();
        hand.add(new Card(Suit.HEARTS, Rank.FIVE));
        hand.add(new Card(Suit.DIAMONDS, Rank.SIX));
        hand.add(new Card(Suit.CLUBS, Rank.THREE));
        assertTrue(hand.format(true).contains("<закрытая карта>"));
        assertTrue(hand.format(false).contains("Шестерка"));
    }
}
