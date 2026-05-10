package ru.nsu.filippova.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;


class RobotStrategyTest {
    @Test
    void greedyStrategyMovesToNearestFood() {
        GameModel model = modelWithNoFood(MapType.EMPTY);
        Snake robot = new Snake(false, new Cell(5, 5), Direction.UP, RobotStrategy.GREEDY);
        snakes(model).add(robot);
        food(model).add(new Cell(5, 7));

        Direction direction = robot.getStrategy().chooseDirection(robot, model);

        assertEquals(Direction.RIGHT, direction);
    }

    @Test
    void steadyStrategyTurnsOnlyWhenItImprovesFoodPath() {
        GameModel model = modelWithNoFood(MapType.EMPTY);
        Snake robot = new Snake(false, new Cell(5, 5), Direction.UP, RobotStrategy.STEADY);
        snakes(model).add(robot);
        food(model).add(new Cell(5, 8));
        obstacles(model).add(new Cell(4, 5));

        Direction direction = robot.getStrategy().chooseDirection(robot, model);

        assertEquals(Direction.RIGHT, direction);
    }

    @Test
    void steadyStrategyKeepsForwardDirectionWhenItIsNotWorse() {
        GameModel model = modelWithNoFood(MapType.EMPTY);
        Snake robot = new Snake(false, new Cell(5, 5), Direction.RIGHT, RobotStrategy.STEADY);
        snakes(model).add(robot);
        food(model).add(new Cell(5, 8));

        Direction direction = robot.getStrategy().chooseDirection(robot, model);

        assertEquals(Direction.RIGHT, direction);
    }

    @Test
    void safeDirectionsUseReverseOnlyWhenRobotIsBlocked() {
        GameModel model = modelWithNoFood(MapType.EMPTY);
        Snake robot = new Snake(false, new Cell(1, 1), Direction.UP, RobotStrategy.GREEDY);
        snakes(model).add(robot);
        obstacles(model).addAll(List.of(new Cell(0, 1), new Cell(1, 0), new Cell(1, 2)));

        List<Direction> directions = model.safeDirectionsForRobot(robot);

        assertEquals(List.of(Direction.DOWN), directions);
    }

    @Test
    void dangerCheckWrapsCellsOutsideField() {
        GameModel model = modelWithNoFood(MapType.EMPTY);
        Snake robot = new Snake(false, new Cell(1, 1), Direction.UP, RobotStrategy.GREEDY);
        snakes(model).add(robot);
        obstacles(model).add(new Cell(GameSettings.ROWS - 1, 0));

        assertTrue(model.isDangerousForRobot(robot, new Cell(-1, 0)));
    }

    private static GameModel modelWithNoFood(MapType mapType) {
        return new GameModel(new GameSettings(GameMode.INFINITE, 10, 0, mapType, 0, Difficulty.MEDIUM));
    }

    @SuppressWarnings("unchecked")
    private static Set<Cell> food(GameModel model) {
        return field(model, "food");
    }

    @SuppressWarnings("unchecked")
    private static Set<Cell> obstacles(GameModel model) {
        return field(model, "obstacles");
    }

    @SuppressWarnings("unchecked")
    private static List<Snake> snakes(GameModel model) {
        return field(model, "snakes");
    }

    @SuppressWarnings("unchecked")
    private static <T> T field(Object target, String name) {
        try {
            Field field = target.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
    }
}
