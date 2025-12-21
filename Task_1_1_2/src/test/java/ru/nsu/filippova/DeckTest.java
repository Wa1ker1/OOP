package ru.nsu.filippova;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeckTest {

    @Test
    void constructorRejectsNonPositiveDecks() {
        assertThrows(IllegalArgumentException.class, () -> new Deck(0));
        assertThrows(IllegalArgumentException.class, () -> new Deck(-3));
    }

    @Test
    void drawDecreasesRemainingCards() {
        Deck deck = new Deck(1);
        int totalCards = Rank.values().length * Suit.values().length;
        assertEquals(totalCards, deck.remainingCards());
        deck.draw();
        assertEquals(totalCards - 1, deck.remainingCards());
    }

    @Test
    void drawFromEmptyDeckThrows() {
        Deck deck = new Deck(1);
        int totalCards = Rank.values().length * Suit.values().length;
        for (int i = 0; i < totalCards; i++) {
            deck.draw();
        }
        assertThrows(IllegalStateException.class, deck::draw);
    }

    @Test
    void shuffleRestoresFullDeck() {
        Deck deck = new Deck(1);
        deck.draw();
        deck.draw();
        deck.shuffle();
        int totalCards = Rank.values().length * Suit.values().length;
        assertEquals(totalCards, deck.remainingCards());
    }

    @Test
    void deckContainsAllSuitAndRankCombinations() {
        Deck deck = new Deck(1);
        Set<String> seen = new HashSet<>();
        int totalCards = Rank.values().length * Suit.values().length;
        for (int i = 0; i < totalCards; i++) {
            Card card = deck.draw();
            seen.add(card.getSuit().name() + "-" + card.getRank().name());
        }
        assertEquals(totalCards, seen.size());
        assertTrue(seen.contains(Suit.CLUBS.name() + "-" + Rank.ACE.name()));
    }
}
