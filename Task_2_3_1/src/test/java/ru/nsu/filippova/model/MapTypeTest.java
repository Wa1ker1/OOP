package ru.nsu.filippova.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;


class MapTypeTest {
    @Test
    void firstMapHasNoObstacles() {
        assertTrue(MapType.EMPTY.buildObstacles(24, 32).isEmpty());
        assertEquals("Карта 1: Пустая", MapType.EMPTY.toString());
    }

    @Test
    void secondMapContainsOnlyBorder() {
        int rows = 6;
        int columns = 8;
        Set<Cell> obstacles = MapType.BORDER.buildObstacles(rows, columns);

        assertEquals(rows * 2 + (columns - 2) * 2, obstacles.size());
        assertTrue(obstacles.contains(new Cell(0, 0)));
        assertTrue(obstacles.contains(new Cell(rows - 1, columns - 1)));
        assertTrue(obstacles.contains(new Cell(3, 0)));
        assertTrue(obstacles.contains(new Cell(0, 4)));
        assertFalse(obstacles.contains(new Cell(3, 4)));
        assertEquals("Карта 2: Периметр", MapType.BORDER.toString());
    }

    @Test
    void thirdMapBuildsLabyrinth() {
        Set<Cell> obstacles = MapType.LABYRINTH.buildObstacles(24, 32);

        assertFalse(obstacles.isEmpty());
        assertTrue(obstacles.contains(new Cell(3, 3)));
        assertTrue(obstacles.contains(new Cell(4, 5)));
        assertFalse(obstacles.contains(new Cell(0, 0)));
        assertEquals("Карта 3: Лабиринт", MapType.LABYRINTH.toString());
    }

    @Test
    void packageHelpersAddExpectedCells() {
        Set<Cell> cells = new LinkedHashSet<>();

        MapType.addHorizontal(cells, 2, 1, 3, 6, 6);
        MapType.addVertical(cells, 4, 1, 3, 6, 6);
        MapType.addBorder(cells, 6, 6);

        assertTrue(cells.contains(new Cell(2, 1)));
        assertTrue(cells.contains(new Cell(3, 4)));
        assertTrue(cells.contains(new Cell(0, 5)));
        assertFalse(cells.contains(new Cell(3, 3)));
    }
}
