package ru.nsu.filippova.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;




class GameModelTest {
    @Test
    void createsInitialGameState() {
        GameSettings settings = settings(GameMode.INFINITE, 20, 2, MapType.EMPTY, 5);
        GameModel model = new GameModel(settings);

        assertEquals(GameSettings.ROWS, model.getRows());
        assertEquals(GameSettings.COLUMNS, model.getColumns());
        assertEquals(GameStatus.RUNNING, model.getStatus());
        assertEquals("", model.getMessage());
        assertEquals(3, model.getSnakes().size());
        assertEquals(1, model.getPlayerSnake().length());
        assertEquals(5, model.getFood().size());
        assertTrue(model.getObstacles().isEmpty());
    }

    @Test
    void pausesAndResumesGame() {
        GameModel model = new GameModel(settings(GameMode.INFINITE, 20, 0, MapType.EMPTY, 0));

        model.pause();
        assertEquals(GameStatus.PAUSED, model.getStatus());
        assertEquals("Пауза", model.getMessage());

        model.resume();
        assertEquals(GameStatus.RUNNING, model.getStatus());
        assertEquals("", model.getMessage());
    }

    @Test
    void wrapsMovementThroughFieldEdges() {
        GameModel model = new GameModel(settings(GameMode.INFINITE, 20, 0, MapType.EMPTY, 0));
        Snake player = model.getPlayerSnake();
        player.reset(new Cell(0, 0), Direction.UP);

        model.tick();

        assertEquals(GameStatus.RUNNING, model.getStatus());
        assertEquals(new Cell(GameSettings.ROWS - 1, 0), player.getHead());
        assertEquals(new Cell(0, GameSettings.COLUMNS - 1), model.nextCell(new
                Cell(0, 0), Direction.LEFT));
    }

    @Test
    void playerGrowsAndWinsAfterEatingTargetFood() {
        GameModel model = new GameModel(settings(GameMode.TARGET_LENGTH, 2, 0, MapType.EMPTY, 1));
        Snake player = model.getPlayerSnake();
        food(model).clear();
        food(model).add(model.nextCell(player.getHead(), player.getDirection()));

        model.tick();

        assertEquals(GameStatus.WON, model.getStatus());
        assertEquals(2, player.length());
        assertEquals(1, model.getFood().size());
        assertTrue(model.getMessage().contains("Победа"));
    }

    @Test
    void playerLosesOnObstacle() {
        GameModel model = new GameModel(settings(GameMode.INFINITE, 20, 0, MapType.BORDER, 0));
        model.getPlayerSnake().reset(new Cell(1, 1), Direction.LEFT);

        model.tick();

        assertEquals(GameStatus.LOST, model.getStatus());
        assertEquals("Игра окончена", model.getMessage());
    }

    @Test
    void playerLosesOnOwnBody() {
        GameModel model = new GameModel(settings(GameMode.INFINITE, 20, 0, MapType.EMPTY, 0));
        placeSnake(model.getPlayerSnake(), Direction.LEFT,
                new Cell(5, 7),
                new Cell(5, 6),
                new Cell(5, 5));

        model.tick();

        assertEquals(GameStatus.LOST, model.getStatus());
    }

    @Test
    void playerLosesOnHeadToHeadCollisionWithRobot() {
        GameModel model = new GameModel(settings(GameMode.INFINITE, 20, 0, MapType.EMPTY, 0));
        placeSnake(model.getPlayerSnake(), Direction.RIGHT, new Cell(5, 5));
        Snake robot = new Snake(false, new Cell(5, 7), Direction.LEFT, RobotStrategy.GREEDY);
        snakes(model).add(robot);

        model.tick();

        assertEquals(GameStatus.LOST, model.getStatus());
    }

    @Test
    void crashedRobotShrinksToOneCell() {
        GameModel model = new GameModel(settings(GameMode.INFINITE, 20, 0, MapType.BORDER, 0));
        Snake robot = new Snake(false, new Cell(1, 1), Direction.LEFT, RobotStrategy.GREEDY);
        placeSnake(robot, Direction.LEFT,
                new Cell(1, 1),
                new Cell(1, 2),
                new Cell(2, 1),
                new Cell(2, 2));
        snakes(model).add(robot);

        model.tick();

        assertEquals(GameStatus.RUNNING, model.getStatus());
        assertEquals(1, robot.length());
        assertEquals(new Cell(1, 1), robot.getHead());
    }

    @Test
    void allowedDirectionsDependOnSnakeTypeAndLength() {
        GameModel model = new GameModel(settings(GameMode.INFINITE, 20, 0, MapType.EMPTY, 0));
        Snake player = model.getPlayerSnake();
        Snake robot = new Snake(false, new Cell(1, 1), Direction.RIGHT, RobotStrategy.GREEDY);

        assertTrue(model.allowedDirections(player).contains(Direction.LEFT));
        assertFalse(model.allowedDirections(robot).contains(Direction.LEFT));

        player.moveTo(model.nextCell(player.getHead(), Direction.RIGHT), true);
        assertFalse(model.allowedDirections(player).contains(Direction.LEFT));
    }

    @Test
    void reportsDistancesToFood() {
        GameModel model = new GameModel(settings(GameMode.INFINITE, 20, 0, MapType.EMPTY, 0));

        assertEquals(GameSettings.ROWS + GameSettings.COLUMNS,
                model.distanceToNearestFood(new Cell(5, 5)));

        food(model).add(new Cell(8, 7));

        assertEquals(5, model.distanceToNearestFood(new Cell(5, 5)));
    }

    private static GameSettings settings(GameMode mode, int targetLength,
                                         int enemyCount, MapType mapType, int foodCount) {
        return new GameSettings(mode, targetLength, enemyCount,
                mapType, foodCount, Difficulty.MEDIUM);
    }

    private static void placeSnake(Snake snake, Direction direction, Cell... cells) {
        Deque<Cell> body = body(snake);
        body.clear();
        for (Cell cell : cells) {
            body.addLast(cell);
        }
        setField(snake, "direction", direction);
        setField(snake, "nextDirection", null);
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
    private static Deque<Cell> body(Snake snake) {
        return field(snake, "body");
    }

    private static void setField(Object target, String name, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
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
