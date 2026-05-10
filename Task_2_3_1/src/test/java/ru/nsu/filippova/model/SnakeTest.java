package ru.nsu.filippova.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class SnakeTest {
    @Test
    void exposesInitialState() {
        Snake snake = new Snake(true, new Cell(2, 3), Direction.RIGHT, null);

        assertTrue(snake.isPlayer());
        assertNull(snake.getStrategy());
        assertEquals(new Cell(2, 3), snake.getHead());
        assertEquals(new Cell(2, 3), snake.getTail());
        assertEquals(1, snake.length());
        assertEquals(Direction.RIGHT, snake.getDirection());
    }

    @Test
    void storesBodyAsDefensiveCopy() {
        Snake snake = new Snake(false, new Cell(1, 1), Direction.RIGHT, RobotStrategy.GREEDY);
        snake.moveTo(new Cell(1, 2), true);

        List<Cell> body = snake.getBody();
        body.clear();

        assertEquals(2, snake.length());
        assertEquals(new Cell(1, 2), snake.getHead());
        assertEquals(new Cell(1, 1), snake.getTail());
    }

    @Test
    void appliesDelayedDirectionAndBlocksReverseForLongSnake() {
        Snake snake = new Snake(true, new Cell(5, 5), Direction.RIGHT, null);
        snake.moveTo(new Cell(5, 6), true);

        snake.setDirection(Direction.LEFT);
        snake.applyNextDirection();
        assertEquals(Direction.RIGHT, snake.getDirection());

        snake.setDirection(Direction.DOWN);
        assertEquals(Direction.RIGHT, snake.getDirection());
        snake.applyNextDirection();
        assertEquals(Direction.DOWN, snake.getDirection());
    }

    @Test
    void allowsReverseForSingleCellSnakeAndCanReset() {
        Snake snake = new Snake(true, new Cell(5, 5), Direction.RIGHT, null);

        snake.setDirection(Direction.LEFT);
        snake.applyNextDirection();

        assertEquals(Direction.LEFT, snake.getDirection());

        snake.moveTo(new Cell(5, 4), true);
        snake.reset(new Cell(8, 8), Direction.UP);

        assertEquals(1, snake.length());
        assertEquals(new Cell(8, 8), snake.getHead());
        assertEquals(Direction.UP, snake.getDirection());
    }

    @Test
    void marksRobotSnake() {
        Snake snake = new Snake(false, new Cell(1, 1), Direction.UP, RobotStrategy.STEADY);

        assertFalse(snake.isPlayer());
        assertEquals(RobotStrategy.STEADY, snake.getStrategy());
    }
}
