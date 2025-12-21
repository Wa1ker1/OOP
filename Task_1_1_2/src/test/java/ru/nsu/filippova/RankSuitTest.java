package ru.nsu.filippova;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class RankSuitTest {

    @Test
    void rankValuesCorrespondToTraditionalBlackjack() {
        assertEquals(2, Rank.TWO.getValue());
        assertEquals(10, Rank.QUEEN.getValue());
        assertEquals(11, Rank.ACE.getValue());
    }

    @Test
    void titlesOfSuitsAreLocalized() {
        for (Suit suit : Suit.values()) {
            assertFalse(suit.getTitle().isBlank());
        }
    }
}
