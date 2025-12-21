package ru.nsu.filippova;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlackjackGameTest {

    @Test
    void playRoundReturnsPlayerWhenOnlyPlayerHasBlackjack() throws Exception {
        BlackjackGame game = createGameWithInput();
        setDeck(game, cards(
                new Card(Suit.HEARTS, Rank.ACE),
                new Card(Suit.CLUBS, Rank.NINE),
                new Card(Suit.SPADES, Rank.KING),
                new Card(Suit.DIAMONDS, Rank.EIGHT)
        ));
        Enum<?> winner = invokeWinner(game, "playRound");
        assertEquals("PLAYER", winner.name());
    }

    @Test
    void playRoundReturnsDealerWhenOnlyDealerHasBlackjack() throws Exception {
        BlackjackGame game = createGameWithInput();
        setDeck(game, cards(
                new Card(Suit.HEARTS, Rank.NINE),
                new Card(Suit.CLUBS, Rank.ACE),
                new Card(Suit.SPADES, Rank.FIVE),
                new Card(Suit.DIAMONDS, Rank.KING)
        ));
        Enum<?> winner = invokeWinner(game, "playRound");
        assertEquals("DEALER", winner.name());
    }

    @Test
    void playRoundReturnsDrawWhenBothHaveBlackjack() throws Exception {
        BlackjackGame game = createGameWithInput();
        setDeck(game, cards(
                new Card(Suit.HEARTS, Rank.ACE),
                new Card(Suit.CLUBS, Rank.ACE),
                new Card(Suit.SPADES, Rank.KING),
                new Card(Suit.DIAMONDS, Rank.QUEEN)
        ));
        Enum<?> winner = invokeWinner(game, "playRound");
        assertEquals("DRAW", winner.name());
    }

    @Test
    void playerTurnStopsAfterReachingTwentyOne() throws Exception {
        BlackjackGame game = createGameWithInput("1");
        prepareHands(game,
                List.of(new Card(Suit.CLUBS, Rank.FIVE), new Card(Suit.SPADES, Rank.SIX)),
                List.of(new Card(Suit.HEARTS, Rank.TWO), new Card(Suit.DIAMONDS, Rank.THREE))
        );
        setDeck(game, cards(new Card(Suit.CLUBS, Rank.KING)));
        Enum<?> result = invokeWinner(game, "playerTurn");
        assertEquals("NONE", result.name());
        Player player = getPlayer(game);
        assertEquals(21, player.getHand().getValue());
    }

    @Test
    void playerTurnBustsAndDealerWins() throws Exception {
        BlackjackGame game = createGameWithInput("1");
        prepareHands(game,
                List.of(new Card(Suit.CLUBS, Rank.QUEEN), new Card(Suit.SPADES, Rank.KING)),
                List.of()
        );
        setDeck(game, cards(new Card(Suit.HEARTS, Rank.QUEEN)));
        Enum<?> result = invokeWinner(game, "playerTurn");
        assertEquals("DEALER", result.name());
    }

    @Test
    void playerTurnAllowsStanding() throws Exception {
        BlackjackGame game = createGameWithInput("0");
        prepareHands(game,
                List.of(new Card(Suit.CLUBS, Rank.FIVE), new Card(Suit.SPADES, Rank.SEVEN)),
                List.of()
        );
        Enum<?> result = invokeWinner(game, "playerTurn");
        assertEquals("NONE", result.name());
    }

    @Test
    void dealerTurnBustsWhenDrawingAboveTwentyOne() throws Exception {
        BlackjackGame game = createGameWithInput();
        prepareHands(game,
                List.of(new Card(Suit.CLUBS, Rank.FIVE), new Card(Suit.SPADES, Rank.SEVEN)),
                List.of(new Card(Suit.HEARTS, Rank.EIGHT), new Card(Suit.DIAMONDS, Rank.SEVEN))
        );
        setDeck(game, cards(new Card(Suit.CLUBS, Rank.KING)));
        Enum<?> result = invokeWinner(game, "dealerTurn");
        assertEquals("PLAYER", result.name());
    }

    @Test
    void dealerTurnReturnsDealerWhenScoreIsHigher() throws Exception {
        BlackjackGame game = createGameWithInput();
        prepareHands(game,
                List.of(new Card(Suit.CLUBS, Rank.NINE), new Card(Suit.SPADES, Rank.NINE)),
                List.of(new Card(Suit.HEARTS, Rank.QUEEN), new Card(Suit.DIAMONDS, Rank.JACK))
        );
        Enum<?> result = invokeWinner(game, "dealerTurn");
        assertEquals("DEALER", result.name());
    }

    @Test
    void dealerTurnAwardsPlayerWhenPlayerHasMorePoints() throws Exception {
        BlackjackGame game = createGameWithInput();
        prepareHands(game,
                List.of(new Card(Suit.CLUBS, Rank.QUEEN), new Card(Suit.SPADES, Rank.JACK)),
                List.of(new Card(Suit.HEARTS, Rank.NINE), new Card(Suit.DIAMONDS, Rank.NINE))
        );
        Enum<?> result = invokeWinner(game, "dealerTurn");
        assertEquals("PLAYER", result.name());
    }

    @Test
    void dealerTurnCanEndInDraw() throws Exception {
        BlackjackGame game = createGameWithInput();
        prepareHands(game,
                List.of(new Card(Suit.CLUBS, Rank.NINE), new Card(Suit.SPADES, Rank.EIGHT)),
                List.of(new Card(Suit.HEARTS, Rank.NINE), new Card(Suit.DIAMONDS, Rank.EIGHT))
        );
        Enum<?> result = invokeWinner(game, "dealerTurn");
        assertEquals("DRAW", result.name());
    }

    @Test
    void initialDealAlternatesCardsBetweenParticipants() throws Exception {
        BlackjackGame game = createGameWithInput();
        setDeck(game, cards(
                new Card(Suit.HEARTS, Rank.ACE),
                new Card(Suit.CLUBS, Rank.TWO),
                new Card(Suit.SPADES, Rank.THREE),
                new Card(Suit.DIAMONDS, Rank.FOUR)
        ));
        invokeVoid(game, "initialDeal");
        Player player = getPlayer(game);
        Dealer dealer = getDealer(game);
        List<Card> playerCards = player.getHand().getCards();
        List<Card> dealerCards = dealer.getHand().getCards();
        assertEquals(Rank.ACE, playerCards.get(0).getRank());
        assertEquals(Rank.THREE, playerCards.get(1).getRank());
        assertEquals(Rank.TWO, dealerCards.get(0).getRank());
        assertEquals(Rank.FOUR, dealerCards.get(1).getRank());
    }

    @Test
    void revealHoleCardHandlesShortHands() throws Exception {
        BlackjackGame game = createGameWithInput();
        Dealer dealer = getDealer(game);
        dealer.resetHand();
        dealer.takeCard(new Card(Suit.HEARTS, Rank.ACE));
        invokeVoid(game, "revealHoleCard");
        assertEquals(1, dealer.getHand().getCards().size());
    }

    @Test
    void readBinaryChoiceSkipsInvalidInput() throws Exception {
        BlackjackGame game = createGameWithInput("invalid", "1");
        int choice = (int) invoke(game, "readBinaryChoice", new Class<?>[]{String.class}, "prompt");
        assertEquals(1, choice);
    }

    @Test
    void askForNextRoundReturnsTrueOnlyForOne() throws Exception {
        BlackjackGame game = createGameWithInput("1", "0");
        assertTrue((Boolean) invoke(game, "askForNextRound", new Class<?>[]{}));
        assertFalse((Boolean) invoke(game, "askForNextRound", new Class<?>[]{}));
    }

    @Test
    void announceRoundResultUpdatesStatistics() throws Exception {
        BlackjackGame game = createGameWithInput();
        Class<?> winnerClass = Class.forName("ru.nsu.filippova.BlackjackGame$Winner");
        Method announce = BlackjackGame.class.getDeclaredMethod("announceRoundResult", winnerClass);
        announce.setAccessible(true);
        announce.invoke(game, Enum.valueOf((Class<Enum>) winnerClass, "PLAYER"));
        announce.invoke(game, Enum.valueOf((Class<Enum>) winnerClass, "DEALER"));
        announce.invoke(game, Enum.valueOf((Class<Enum>) winnerClass, "DRAW"));
        announce.invoke(game, Enum.valueOf((Class<Enum>) winnerClass, "NONE"));

        assertEquals(1, getIntField(game, "playerWins"));
        assertEquals(1, getIntField(game, "dealerWins"));
        assertEquals(1, getIntField(game, "draws"));
    }

    private void prepareHands(BlackjackGame game, List<Card> playerCards, List<Card> dealerCards) throws Exception {
        Player player = getPlayer(game);
        player.resetHand();
        for (Card card : playerCards) {
            player.takeCard(card);
        }
        Dealer dealer = getDealer(game);
        dealer.resetHand();
        for (Card card : dealerCards) {
            dealer.takeCard(card);
        }
    }

    private BlackjackGame createGameWithInput(String... answers) {
        String input;
        if (answers.length == 0) {
            input = System.lineSeparator();
        } else {
            input = String.join(System.lineSeparator(), answers) + System.lineSeparator();
        }
        InputStream original = System.in;
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        try {
            return new BlackjackGame();
        } finally {
            System.setIn(original);
        }
    }

    private void setDeck(BlackjackGame game, List<Card> cards) throws Exception {
        Deck deck = getField(game, "deck", Deck.class);
        Field cardsField = Deck.class.getDeclaredField("cards");
        cardsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Deque<Card> deque = (Deque<Card>) cardsField.get(deck);
        deque.clear();
        deque.addAll(cards);
    }

    private List<Card> cards(Card... order) {
        return new ArrayList<>(Arrays.asList(order));
    }

    private Enum<?> invokeWinner(BlackjackGame game, String methodName) throws Exception {
        Method method = BlackjackGame.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        return (Enum<?>) method.invoke(game);
    }

    private Object invoke(BlackjackGame game, String name, Class<?>[] parameterTypes, Object... args) throws Exception {
        Method method = BlackjackGame.class.getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return method.invoke(game, args);
    }

    private void invokeVoid(BlackjackGame game, String methodName) throws Exception {
        Method method = BlackjackGame.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(game);
    }

    private Player getPlayer(BlackjackGame game) throws Exception {
        return getField(game, "player", Player.class);
    }

    private Dealer getDealer(BlackjackGame game) throws Exception {
        return getField(game, "dealer", Dealer.class);
    }

    private <T> T getField(Object target, String fieldName, Class<T> type) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return type.cast(field.get(target));
    }

    private int getIntField(Object target, String name) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.getInt(target);
    }
}
