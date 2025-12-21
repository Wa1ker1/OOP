package ru.nsu.filippova;

import java.util.Scanner;

/**
 * Основной игровой цикл консольного блэкджека.
 */
public class BlackjackGame {
    private static final int DEFAULT_DECKS = 1;
    private static final int MIN_CARDS_BEFORE_SHUFFLE = 15;

    private final Deck deck = new Deck(DEFAULT_DECKS);
    private final Player player = new Player("Игрок");
    private final Dealer dealer = new Dealer();
    private final Scanner scanner = new Scanner(System.in);

    private int playerWins;
    private int dealerWins;
    private int draws;

    /**
     * Запускает игру и обрабатывает серию раундов до выхода пользователя.
     */
    public void start() {
        System.out.println("Добро пожаловать в Блэкджек!");
        int round = 1;
        boolean continueGame = true;
        while (continueGame) {
            if (deck.remainingCards() < MIN_CARDS_BEFORE_SHUFFLE) {
                deck.shuffle();
                System.out.println();
                System.out.println("Колода перетасована.");
            }
            System.out.println();
            System.out.println("Раунд " + round);
            Winner result = playRound();
            announceRoundResult(result);
            continueGame = askForNextRound();
            round++;
        }
        System.out.println("Спасибо за игру!");
    }

    private Winner playRound() {
        player.resetHand();
        dealer.resetHand();
        initialDeal();
        boolean hideDealerCard = true;
        printHands(hideDealerCard);

        boolean playerBlackjack = player.getHand().isBlackjack();
        boolean dealerBlackjack = dealer.getHand().isBlackjack();
        if (playerBlackjack || dealerBlackjack) {
            return resolveBlackjack(playerBlackjack, dealerBlackjack);
        }

        Winner afterPlayerTurn = playerTurn();
        if (afterPlayerTurn != Winner.NONE) {
            return afterPlayerTurn;
        }

        return dealerTurn();
    }

    private void initialDeal() {
        player.takeCard(deck.draw());
        dealer.takeCard(deck.draw());
        player.takeCard(deck.draw());
        dealer.takeCard(deck.draw());

        System.out.println("Дилер раздал карты");
    }

    private Winner resolveBlackjack(boolean playerBlackjack, boolean dealerBlackjack) {
        if (playerBlackjack && dealerBlackjack) {
            System.out.println("Оба участника собрали блэкджек. Ничья.");
            return Winner.DRAW;
        }
        if (playerBlackjack) {
            System.out.println("Вы собрали блэкджек! Вы выиграли раунд.");
            return Winner.PLAYER;
        }
        System.out.println("Дилер собрал блэкджек. Раунд за дилером.");
        return Winner.DEALER;
    }

    private Winner playerTurn() {
        System.out.println();
        System.out.println("Ваш ход");
        System.out.println("-------");

        while (true) {
            if (player.getHand().getValue() == 21) {
                System.out.println("У вас 21 очко. Ход переходит дилеру.");
                return Winner.NONE;
            }

            int choice = readBinaryChoice("Введите \"1\", "
                    + "чтобы взять карту, и \"0\", чтобы остановиться...");
            if (choice == 0) {
                return Winner.NONE;
            }
            Card card = deck.draw();
            player.takeCard(card);
            System.out.println("Вы открыли карту " + card);
            printHands(true);
            if (player.getHand().isBust()) {
                System.out.println("Сумма ваших карт превысила 21. Вы проиграли раунд.");
                return Winner.DEALER;
            }
        }
    }

    private Winner dealerTurn() {
        System.out.println();
        System.out.println("Ход дилера");
        System.out.println("-------");
        revealHoleCard();
        printHands(false);

        while (dealer.getHand().getValue() < 17) {
            Card card = deck.draw();
            dealer.takeCard(card);
            System.out.println("Дилер открывает карту " + card);
            printHands(false);
            if (dealer.getHand().isBust()) {
                System.out.println("Дилер перебрал. Вы выиграли раунд!");
                return Winner.PLAYER;
            }
        }

        int playerValue = player.getHand().getValue();
        int dealerValue = dealer.getHand().getValue();
        if (dealerValue > playerValue) {
            System.out.println("Дилер набрал " + dealerValue + " очков. Раунд за дилером.");
            return Winner.DEALER;
        }
        if (dealerValue < playerValue) {
            System.out.println("Вы набрали " + playerValue + " очков и победили.");
            return Winner.PLAYER;
        }
        System.out.println("Одинаковое количество очков. Ничья.");
        return Winner.DRAW;
    }

    private void printHands(boolean hideDealerCard) {
        System.out.println("Ваши карты: " + player.getHand().format(false)
                + " ⇒ " + player.getHand().getValue());
        if (hideDealerCard) {
            System.out.println("Карты дилера: " + dealer.getHand().format(true));
        } else {
            System.out.println("Карты дилера: " + dealer.getHand().format(false)
                    + " ⇒ " + dealer.getHand().getValue());
        }
    }

    private void revealHoleCard() {
        if (dealer.getHand().getCards().size() >= 2) {
            Card hiddenCard = dealer.getHand().getCards().get(1);
            System.out.println("Дилер открывает закрытую карту " + hiddenCard);
        }
    }

    private int readBinaryChoice(String prompt) {
        while (true) {
            System.out.println(prompt);
            String input = scanner.nextLine().trim();
            if ("0".equals(input) || "1".equals(input)) {
                return Integer.parseInt(input);
            }
            System.out.println("Некорректный ввод. Повторите попытку.");
        }
    }

    private void announceRoundResult(Winner winner) {
        switch (winner) {
            case PLAYER -> playerWins++;
            case DEALER -> dealerWins++;
            case DRAW -> draws++;
            default -> {
            }
        }
        System.out.println("Счет " + playerWins + ":" + dealerWins + ". Ничьи: " + draws + ".");
    }

    private boolean askForNextRound() {
        int choice = readBinaryChoice("Введите \"1\","
                + " чтобы сыграть еще раунд, и \"0\", чтобы завершить игру...");
        return choice == 1;
    }

    private enum Winner {
        PLAYER,
        DEALER,
        DRAW,
        NONE
    }
}
