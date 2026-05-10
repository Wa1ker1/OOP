package ru.nsu.filippova.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Модель состояния и правил игры.
 */
public class GameModel {
    private final GameSettings settings;
    private final int rows = GameSettings.ROWS;
    private final int columns = GameSettings.COLUMNS;
    private final Random random = new Random();
    private final Set<Cell> obstacles;
    private final Set<Cell> food = new LinkedHashSet<>();
    private final List<Snake> snakes = new ArrayList<>();
    private final Snake playerSnake;

    private GameStatus status = GameStatus.RUNNING;
    private String message = "";

    /**
     * Создает новую игру.
     *
     * @param settings настройки партии
     */
    public GameModel(GameSettings settings) {
        this.settings = settings;
        this.obstacles = settings.mapType().buildObstacles(rows, columns);

        playerSnake = new Snake(
                true,
                findFreeNear(new Cell(rows / 2, columns / 4)),
                Direction.RIGHT,
                null
        );
        snakes.add(playerSnake);
        createRobots(settings.enemyCount());
        fillFood();
    }

    /**
     * Выполняет один игровой шаг.
     */
    public void tick() {
        if (status != GameStatus.RUNNING) {
            return;
        }

        List<Snake> robots = robotSnakes();
        for (Snake snake : robots) {
            snake.setDirection(snake.getStrategy().chooseDirection(snake, this));
        }
        for (Snake snake : snakes) {
            snake.applyNextDirection();
        }

        Map<Snake, Cell> nextHeads = new HashMap<>();
        for (Snake snake : snakes) {
            Cell nextHead = nextCell(snake.getHead(), snake.getDirection());
            nextHeads.put(snake, nextHead);
        }

        Cell playerNextHead = nextHeads.get(playerSnake);
        if (hitsBlockedCell(playerSnake, playerNextHead)
                || robots.stream().map(nextHeads::get).anyMatch(playerNextHead::equals)) {
            lose();
            return;
        }

        Set<Snake> crashedRobots = crashedRobots(robots, nextHeads);

        boolean playerGrows = food.remove(playerNextHead);
        playerSnake.moveTo(playerNextHead, playerGrows);

        for (Snake robot : robots) {
            if (crashedRobots.contains(robot)) {
                resetRobot(robot);
                continue;
            }
            Cell nextHead = nextHeads.get(robot);
            boolean grows = food.remove(nextHead);
            robot.moveTo(nextHead, grows);
        }

        fillFood();
        if (settings.mode() == GameMode.TARGET_LENGTH
                && playerSnake.length() >= settings.targetLength()) {
            status = GameStatus.WON;
            message = "Победа: достигнута длина " + playerSnake.length();
        }
    }

    /**
     * Задает направление игрока.
     *
     * @param direction новое направление
     */
    public void setPlayerDirection(Direction direction) {
        playerSnake.setDirection(direction);
    }

    /**
     * Ставит игру на паузу.
     */
    public void pause() {
        if (status == GameStatus.RUNNING) {
            status = GameStatus.PAUSED;
            message = "Пауза";
        }
    }

    /**
     * Продолжает игру после паузы.
     */
    public void resume() {
        if (status == GameStatus.PAUSED) {
            status = GameStatus.RUNNING;
            message = "";
        }
    }

    /**
     * Возвращает допустимые направления для змейки.
     *
     * @param snake змейка
     * @return список направлений
     */
    public List<Direction> allowedDirections(Snake snake) {
        EnumSet<Direction> directions = EnumSet.allOf(Direction.class);
        if (snake.length() > 1 || !snake.isPlayer()) {
            directions.removeIf(snake.getDirection()::isOpposite);
        }
        return new ArrayList<>(directions);
    }

    /**
     * Возвращает безопасные направления для робота.
     *
     * @param robot змейка-робот
     * @return список безопасных направлений
     */
    public List<Direction> safeDirectionsForRobot(Snake robot) {
        List<Direction> safeDirections = allowedDirections(robot).stream()
                .filter(direction -> !isDangerousForRobot(robot,
                        nextCell(robot.getHead(), direction)))
                .toList();
        if (!safeDirections.isEmpty()) {
            return safeDirections;
        }

        return EnumSet.allOf(Direction.class).stream()
                .filter(direction -> !isDangerousForRobot(robot,
                        nextCell(robot.getHead(), direction)))
                .toList();
    }

    /**
     * Проверяет опасность клетки для робота.
     *
     * @param robot змейка-робот
     * @param cell проверяемая клетка
     * @return {@code true}, если клетка занята или содержит препятствие
     */
    public boolean isDangerousForRobot(Snake robot, Cell cell) {
        Cell checkedCell = wrapCell(cell);
        if (obstacles.contains(checkedCell)) {
            return true;
        }
        Set<Cell> occupied = occupiedCells();
        occupied.remove(robot.getTail());
        return occupied.contains(checkedCell);
    }

    /**
     * Возвращает следующую клетку с переносом через край поля.
     *
     * @param cell текущая клетка
     * @param direction направление движения
     * @return следующая клетка
     */
    public Cell nextCell(Cell cell, Direction direction) {
        return wrapCell(cell.move(direction));
    }

    /**
     * Возвращает расстояние до ближайшей еды.
     *
     * @param cell исходная клетка
     * @return расстояние
     */
    public int distanceToNearestFood(Cell cell) {
        return food.stream()
                .mapToInt(cell::distanceTo)
                .min()
                .orElse(rows + columns);
    }

    /**
     * Возвращает количество строк поля.
     *
     * @return строки
     */
    public int getRows() {
        return rows;
    }

    /**
     * Возвращает количество столбцов поля.
     *
     * @return столбцы
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Возвращает змейку игрока.
     *
     * @return змейка игрока
     */
    public Snake getPlayerSnake() {
        return playerSnake;
    }

    /**
     * Возвращает всех змеек.
     *
     * @return список змеек
     */
    public List<Snake> getSnakes() {
        return Collections.unmodifiableList(snakes);
    }

    /**
     * Возвращает препятствия.
     *
     * @return клетки препятствий
     */
    public Set<Cell> getObstacles() {
        return Collections.unmodifiableSet(obstacles);
    }

    /**
     * Возвращает еду на поле.
     *
     * @return клетки еды
     */
    public Set<Cell> getFood() {
        return Collections.unmodifiableSet(food);
    }

    /**
     * Возвращает состояние игры.
     *
     * @return текущий статус
     */
    public GameStatus getStatus() {
        return status;
    }

    /**
     * Возвращает сообщение для интерфейса.
     *
     * @return текст сообщения
     */
    public String getMessage() {
        return message;
    }

    private void createRobots(int count) {
        List<RobotStrategy> strategies = List.of(RobotStrategy.GREEDY, RobotStrategy.STEADY);
        List<Cell> starts = List.of(
                new Cell(rows / 2, columns * 3 / 4),
                new Cell(rows / 3, columns * 3 / 4)
        );
        List<Direction> directions = List.of(Direction.LEFT, Direction.DOWN);

        for (int index = 0; index < count; index++) {
            Snake robot = new Snake(
                    false,
                    findFreeNear(starts.get(index)),
                    directions.get(index),
                    strategies.get(index)
            );
            snakes.add(robot);
        }
    }

    private List<Snake> robotSnakes() {
        return snakes.stream()
                .filter(snake -> !snake.isPlayer())
                .toList();
    }

    private Set<Snake> crashedRobots(List<Snake> robots, Map<Snake, Cell> nextHeads) {
        Set<Snake> crashed = new HashSet<>();
        Map<Cell, List<Snake>> robotsByNextHead = new HashMap<>();

        for (Snake robot : robots) {
            Cell nextHead = nextHeads.get(robot);
            if (hitsBlockedCell(robot, nextHead)) {
                crashed.add(robot);
            }
            robotsByNextHead.computeIfAbsent(nextHead, ignored -> new ArrayList<>()).add(robot);
        }

        for (List<Snake> group : robotsByNextHead.values()) {
            if (group.size() > 1) {
                crashed.addAll(group);
            }
        }
        return crashed;
    }

    private boolean hitsBlockedCell(Snake snake, Cell nextHead) {
        if (obstacles.contains(nextHead)) {
            return true;
        }
        Set<Cell> occupied = occupiedCells();
        if (!food.contains(nextHead)) {
            occupied.remove(snake.getTail());
        }
        return occupied.contains(nextHead);
    }

    private void lose() {
        status = GameStatus.LOST;
        message = "Игра окончена";
    }

    private void resetRobot(Snake robot) {
        Cell head = robot.getHead();
        robot.reset(head, freeDirectionFrom(head, robot.getDirection()));
    }

    private Direction freeDirectionFrom(Cell cell, Direction fallback) {
        List<Direction> directions = new ArrayList<>(List.of(Direction.values()));
        Collections.shuffle(directions, random);
        for (Direction direction : directions) {
            Cell next = nextCell(cell, direction);
            if (!obstacles.contains(next)) {
                return direction;
            }
        }
        return fallback;
    }

    private Set<Cell> occupiedCells() {
        Set<Cell> occupied = new HashSet<>();
        for (Snake snake : snakes) {
            occupied.addAll(snake.getBody());
        }
        return occupied;
    }

    private boolean isInside(Cell cell) {
        return cell.row() >= 0 && cell.row() < rows
                && cell.column() >= 0 && cell.column() < columns;
    }

    private Cell wrapCell(Cell cell) {
        int row = Math.floorMod(cell.row(), rows);
        int column = Math.floorMod(cell.column(), columns);
        return new Cell(row, column);
    }

    private void fillFood() {
        int attempts = rows * columns * 2;
        while (food.size() < settings.foodCount() && attempts-- > 0) {
            Cell cell = randomFreeCell();
            if (!food.contains(cell)) {
                food.add(cell);
            }
        }
    }

    private Cell randomFreeCell() {
        List<Cell> freeCells = new ArrayList<>();
        Set<Cell> occupied = occupiedCells();
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                Cell cell = new Cell(row, column);
                if (!obstacles.contains(cell) && !occupied.contains(cell) && !food.contains(cell)) {
                    freeCells.add(cell);
                }
            }
        }
        if (freeCells.isEmpty()) {
            return findFreeNear(new Cell(rows / 2, columns / 2));
        }
        return freeCells.get(random.nextInt(freeCells.size()));
    }

    private Cell findFreeNear(Cell preferred) {
        Set<Cell> occupied = occupiedCells();
        for (int radius = 0; radius < Math.max(rows, columns); radius++) {
            for (int row = preferred.row() - radius; row <= preferred.row() + radius; row++) {
                for (int column = preferred.column() - radius; column
                        <= preferred.column() + radius; column++) {
                    Cell cell = new Cell(row, column);
                    if (isInside(cell) && !obstacles.contains(cell) && !occupied.contains(cell)) {
                        return cell;
                    }
                }
            }
        }
        return preferred;
    }
}
