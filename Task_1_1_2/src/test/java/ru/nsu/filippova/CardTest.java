package ru.nsu.filippova;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CardTest {

    @Test
    void toStringUsesLocalizedTitlesAndValues() {
        Card card = new Card(Suit.HEARTS, Rank.ACE);
        assertEquals("Туз Червы (11)", card.toString());
    }

    @Test
    void gettersExposeSuitRankAndValue() {
        Card card = new Card(Suit.SPADES, Rank.KING);
        assertEquals(Suit.SPADES, card.getSuit());
        assertEquals(Rank.KING, card.getRank());
        assertEquals(10, card.getValue());
        assertTrue(card.toString().contains(Rank.KING.getTitle()));
    }
}
