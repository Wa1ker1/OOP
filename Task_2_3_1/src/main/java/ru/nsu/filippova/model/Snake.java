package ru.nsu.filippova.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Модель одной змейки.
 */
public class Snake {
    private final boolean player;
    private final RobotStrategy strategy;
    private final Deque<Cell> body = new ArrayDeque<>();
    private Direction direction;
    private Direction nextDirection;

    /**
     * Создает змейку в начальной клетке.
     *
     * @param player является ли змейка игроком
     * @param start начальная клетка
     * @param direction начальное направление
     * @param strategy стратегия робота или {@code null}
     */
    public Snake(boolean player, Cell start, Direction direction, RobotStrategy strategy) {
        this.player = player;
        this.strategy = strategy;
        reset(start, direction);
    }

    /**
     * Проверяет, управляет ли змейкой игрок.
     *
     * @return {@code true}, если это змейка игрока
     */
    public boolean isPlayer() {
        return player;
    }

    /**
     * Возвращает стратегию робота.
     *
     * @return стратегия или {@code null}
     */
    public RobotStrategy getStrategy() {
        return strategy;
    }

    /**
     * Возвращает голову змейки.
     *
     * @return первая клетка тела
     */
    public Cell getHead() {
        return body.peekFirst();
    }

    /**
     * Возвращает хвост змейки.
     *
     * @return последняя клетка тела
     */
    public Cell getTail() {
        return body.peekLast();
    }

    /**
     * Возвращает клетки тела.
     *
     * @return копия тела змейки
     */
    public List<Cell> getBody() {
        return new ArrayList<>(body);
    }

    /**
     * Возвращает длину змейки.
     *
     * @return количество звеньев
     */
    public int length() {
        return body.size();
    }

    /**
     * Возвращает текущее направление.
     *
     * @return направление движения
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Запоминает новое направление для следующего шага.
     *
     * @param direction новое направление
     */
    public void setDirection(Direction direction) {
        if (direction == null) {
            return;
        }
        if (body.size() == 1 || !this.direction.isOpposite(direction)) {
            nextDirection = direction;
        }
    }

    /**
     * Применяет отложенный поворот.
     */
    public void applyNextDirection() {
        if (nextDirection != null) {
            direction = nextDirection;
            nextDirection = null;
        }
    }

    /**
     * Перемещает голову змейки.
     *
     * @param newHead новая клетка головы
     * @param grow нужно ли сохранить хвост
     */
    public void moveTo(Cell newHead, boolean grow) {
        body.addFirst(newHead);
        if (!grow) {
            body.removeLast();
        }
    }

    /**
     * Сбрасывает змейку до одного звена.
     *
     * @param start новая клетка
     * @param direction новое направление
     */
    public void reset(Cell start, Direction direction) {
        body.clear();
        body.add(start);
        this.direction = direction;
        nextDirection = null;
    }
}
