package ru.nsu.filippova.view;

import java.util.List;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import ru.nsu.filippova.model.Cell;
import ru.nsu.filippova.model.GameModel;
import ru.nsu.filippova.model.GameStatus;
import ru.nsu.filippova.model.Snake;

/**
 * Отрисовывает состояние игры на Canvas.
 */
public class GameRenderer {
    private static final Color BACKGROUND = Color.web("#172126");
    private static final Color GRID_LIGHT = Color.web("#243238");
    private static final Color GRID_DARK = Color.web("#1e2b30");
    private static final Color OBSTACLE = Color.web("#687077");
    private static final Color FOOD = Color.web("#e85151");
    private static final Color PLAYER_HEAD = Color.web("#72d95b");
    private static final Color PLAYER_BODY = Color.web("#3fb463");
    private static final Color[] ROBOT_HEADS = {Color.web("#f1bd4b"), Color.web("#5ab9e8")};
    private static final Color[] ROBOT_BODIES = {Color.web("#cc8f35"), Color.web("#347fa8")};

    private final Canvas canvas;

    /**
     * Создает renderer для игрового поля.
     *
     * @param canvas холст для отрисовки
     */
    public GameRenderer(Canvas canvas) {
        this.canvas = canvas;
    }

    /**
     * Отрисовывает текущее состояние модели.
     *
     * @param model модель игры
     */
    public void draw(GameModel model) {
        if (model == null) {
            return;
        }

        GraphicsContext graphics = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        graphics.setFill(BACKGROUND);
        graphics.fillRect(0, 0, width, height);

        double cellSize = Math.min(width / model.getColumns(), height / model.getRows());
        double boardWidth = cellSize * model.getColumns();
        double boardHeight = cellSize * model.getRows();
        double offsetX = (width - boardWidth) / 2.0;
        double offsetY = (height - boardHeight) / 2.0;

        drawGrid(graphics, model, cellSize, offsetX, offsetY);
        drawCells(graphics, model.getObstacles(), OBSTACLE, cellSize, offsetX, offsetY, 0.10);
        drawFood(graphics, model, cellSize, offsetX, offsetY);
        drawSnakes(graphics, model, cellSize, offsetX, offsetY);
        drawBorder(graphics, boardWidth, boardHeight, offsetX, offsetY);

        if (model.getStatus() == GameStatus.WON || model.getStatus() == GameStatus.LOST
                || model.getStatus() == GameStatus.PAUSED) {
            drawOverlay(graphics, model, width, height);
        }
    }

    private void drawGrid(GraphicsContext graphics, GameModel model, double cellSize,
                          double offsetX, double offsetY) {
        for (int row = 0; row < model.getRows(); row++) {
            for (int column = 0; column < model.getColumns(); column++) {
                graphics.setFill((row + column) % 2 == 0 ? GRID_LIGHT : GRID_DARK);
                graphics.fillRect(offsetX + column * cellSize,
                        offsetY + row * cellSize, cellSize, cellSize);
            }
        }
    }

    private void drawCells(GraphicsContext graphics, Iterable<Cell> cells, Color color,
                           double cellSize, double offsetX, double offsetY,
                           double insetRatio) {
        graphics.setFill(color);
        double inset = Math.max(1.0, cellSize * insetRatio);
        for (Cell cell : cells) {
            graphics.fillRoundRect(
                    offsetX + cell.column() * cellSize + inset,
                    offsetY + cell.row() * cellSize + inset,
                    cellSize - inset * 2,
                    cellSize - inset * 2,
                    Math.max(2, cellSize * 0.25),
                    Math.max(2, cellSize * 0.25)
            );
        }
    }

    private void drawFood(GraphicsContext graphics, GameModel model, double cellSize,
                          double offsetX, double offsetY) {
        graphics.setFill(FOOD);
        double inset = Math.max(2.0, cellSize * 0.22);
        for (Cell cell : model.getFood()) {
            graphics.fillOval(
                    offsetX + cell.column() * cellSize + inset,
                    offsetY + cell.row() * cellSize + inset,
                    cellSize - inset * 2,
                    cellSize - inset * 2
            );
        }
    }

    private void drawSnakes(GraphicsContext graphics, GameModel model, double cellSize,
                            double offsetX, double offsetY) {
        int robotIndex = 0;
        for (Snake snake : model.getSnakes()) {
            Color headColor = snake.isPlayer() ? PLAYER_HEAD
                    : ROBOT_HEADS[robotIndex % ROBOT_HEADS.length];
            Color bodyColor = snake.isPlayer() ? PLAYER_BODY
                    : ROBOT_BODIES[robotIndex % ROBOT_BODIES.length];
            drawSnake(graphics, snake.getBody(), headColor, bodyColor, cellSize, offsetX, offsetY);
            if (!snake.isPlayer()) {
                robotIndex++;
            }
        }
    }

    private void drawSnake(GraphicsContext graphics, List<Cell> body,
                           Color headColor, Color bodyColor, double cellSize,
                           double offsetX, double offsetY) {
        for (int index = body.size() - 1; index >= 0; index--) {
            Cell cell = body.get(index);
            graphics.setFill(index == 0 ? headColor : bodyColor);
            double inset = Math.max(1.0, cellSize * 0.12);
            graphics.fillRoundRect(
                    offsetX + cell.column() * cellSize + inset,
                    offsetY + cell.row() * cellSize + inset,
                    cellSize - inset * 2,
                    cellSize - inset * 2,
                    Math.max(3, cellSize * 0.35),
                    Math.max(3, cellSize * 0.35)
            );
        }
    }

    private void drawBorder(GraphicsContext graphics, double boardWidth,
                            double boardHeight, double offsetX, double offsetY) {
        graphics.setStroke(Color.web("#9aa7ad"));
        graphics.setLineWidth(2);
        graphics.strokeRect(offsetX, offsetY, boardWidth, boardHeight);
    }

    private void drawOverlay(GraphicsContext graphics, GameModel model,
                             double width, double height) {
        graphics.setFill(Color.rgb(0, 0, 0, 0.56));
        graphics.fillRect(0, 0, width, height);
        graphics.setFill(Color.WHITE);
        graphics.setFont(Font.font("System", 34));
        String title = switch (model.getStatus()) {
            case WON -> "Победа";
            case LOST -> "Поражение";
            case PAUSED -> "Пауза";
            default -> "";
        };
        double titleWidth = graphics.getFont().getSize() * title.length() * 0.55;
        graphics.fillText(title, (width - titleWidth) / 2.0, height / 2.0 - 20);

        graphics.setFont(Font.font("System", 16));
        String hint = model.getStatus() == GameStatus.PAUSED
                ? "Пробел - продолжить | Esc - меню"
                : "Enter - новая игра | Esc - меню";
        double hintWidth = graphics.getFont().getSize() * hint.length() * 0.47;
        graphics.fillText(hint, (width - hintWidth) / 2.0, height / 2.0 + 18);
    }
}
