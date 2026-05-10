package ru.nsu.filippova.model;

import java.util.Comparator;
import java.util.List;

/**
 * Стратегия выбора направления для змейки-робота.
 */
public enum RobotStrategy {
    /** Робот идет к ближайшей еде. */
    GREEDY {
        @Override
        public Direction chooseDirection(Snake snake, GameModel model) {
            return closestToFood(snake, model, model.safeDirectionsForRobot(snake));
        }
    },

    /** Робот реже поворачивает, если путь вперед безопасен. */
    STEADY {
        @Override
        public Direction chooseDirection(Snake snake, GameModel model) {
            List<Direction> directions = model.safeDirectionsForRobot(snake);
            Direction bestDirection = closestToFood(snake, model, directions);
            if (!directions.contains(snake.getDirection())) {
                return bestDirection;
            }

            int currentDistance = distanceAfterMove(snake, model, snake.getDirection());
            int bestDistance = distanceAfterMove(snake, model, bestDirection);
            return bestDistance < currentDistance ? bestDirection : snake.getDirection();
        }
    };

    /**
     * Выбирает следующий ход робота.
     *
     * @param snake робот
     * @param model модель игры
     * @return выбранное направление
     */
    public abstract Direction chooseDirection(Snake snake, GameModel model);

    private static Direction closestToFood(Snake snake, GameModel model,
                                           List<Direction> directions) {
        return directions.stream()
                .min(Comparator
                        .comparingInt((Direction direction)
                                -> distanceAfterMove(snake, model, direction))
                        .thenComparing(direction -> direction == snake.getDirection() ? 0 : 1))
                .orElse(snake.getDirection());
    }

    private static int distanceAfterMove(Snake snake, GameModel model, Direction direction) {
        return model.distanceToNearestFood(model.nextCell(snake.getHead(), direction));
    }
}
