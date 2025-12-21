package ru.nsu.filippova;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParticipantTest {

    @Test
    void playerStoresProvidedName() {
        Player player = new Player("Алексей");
        assertEquals("Алексей", player.getName());
    }

    @Test
    void dealerUsesLocalizedName() {
        Dealer dealer = new Dealer();
        assertEquals("Дилер", dealer.getName());
    }

    @Test
    void takingAndResettingCardsAffectsHand() {
        Player player = new Player("Игрок");
        Card card = new Card(Suit.HEARTS, Rank.FOUR);
        player.takeCard(card);
        assertEquals(1, player.getHand().getCards().size());
        assertTrue(player.getHand().getCards().contains(card));
        player.resetHand();
        assertTrue(player.getHand().getCards().isEmpty());
    }
}
