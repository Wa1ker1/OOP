package ru.nsu.filippova.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CellDirectionDifficultyTest {
    @Test
    void cellMovesAndMeasuresDistance() {
        Cell cell = new Cell(3, 4);

        assertEquals(new Cell(2, 4), cell.move(Direction.UP));
        assertEquals(new Cell(3, 5), cell.move(Direction.RIGHT));
        assertEquals(new Cell(4, 4), cell.move(Direction.DOWN));
        assertEquals(new Cell(3, 3), cell.move(Direction.LEFT));
        assertEquals(7, cell.distanceTo(new Cell(8, 2)));
    }

    @Test
    void directionDetectsOppositeValues() {
        assertTrue(Direction.UP.isOpposite(Direction.DOWN));
        assertTrue(Direction.LEFT.isOpposite(Direction.RIGHT));
        assertFalse(Direction.UP.isOpposite(Direction.RIGHT));
        assertEquals(-1, Direction.UP.rowDelta());
        assertEquals(1, Direction.RIGHT.columnDelta());
    }

    @Test
    void difficultyHasReadableTitleAndTickDuration() {
        assertEquals("Легкий", Difficulty.EASY.toString());
        assertEquals("Средний", Difficulty.MEDIUM.toString());
        assertEquals("Сложный", Difficulty.HARD.toString());
        assertEquals(190_000_000L, Difficulty.EASY.tickNanos());
        assertEquals(130_000_000L, Difficulty.MEDIUM.tickNanos());
        assertEquals(85_000_000L, Difficulty.HARD.tickNanos());
    }
}
