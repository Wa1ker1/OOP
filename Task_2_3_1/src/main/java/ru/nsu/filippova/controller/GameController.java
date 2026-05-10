package ru.nsu.filippova.controller;

import java.io.IOException;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ru.nsu.filippova.Main;
import ru.nsu.filippova.model.Cell;
import ru.nsu.filippova.model.Direction;
import ru.nsu.filippova.model.GameMode;
import ru.nsu.filippova.model.GameModel;
import ru.nsu.filippova.model.GameSettings;
import ru.nsu.filippova.model.GameStatus;
import ru.nsu.filippova.model.Snake;


/**
 * Контроллер игрового экрана.
 */
public class GameController {
    private static final Color BACKGROUND = Color.web("#172126");
    private static final Color GRID_LIGHT = Color.web("#243238");
    private static final Color GRID_DARK = Color.web("#1e2b30");
    private static final Color OBSTACLE = Color.web("#687077");
    private static final Color FOOD = Color.web("#e85151");
    private static final Color PLAYER_HEAD = Color.web("#72d95b");
    private static final Color PLAYER_BODY = Color.web("#3fb463");
    private static final Color[] ROBOT_HEADS = {Color.web("#f1bd4b"), Color.web("#5ab9e8")};
    private static final Color[] ROBOT_BODIES = {Color.web("#cc8f35"), Color.web("#347fa8")};

    @FXML
    private BorderPane root;
    @FXML
    private Pane gamePane;
    @FXML
    private Canvas gameCanvas;
    @FXML
    private Label statusLabel;
    @FXML
    private Label lengthLabel;
    @FXML
    private Label modeLabel;
    @FXML
    private Button pauseButton;

    private GameSettings settings;
    private GameModel model;
    private AnimationTimer timer;
    private long lastTick;

    @FXML
    private void initialize() {
        gameCanvas.widthProperty().bind(gamePane.widthProperty());
        gameCanvas.heightProperty().bind(gamePane.heightProperty());
        gameCanvas.widthProperty().addListener((ignored, oldValue, newValue) -> draw());
        gameCanvas.heightProperty().addListener((ignored, oldValue, newValue) -> draw());

        root.setFocusTraversable(true);
        root.setOnKeyPressed(event -> {
            handleKey(event.getCode());
            event.consume();
        });
    }

    /**
     * Создает новую партию с выбранными настройками.
     *
     * @param settings настройки игры
     */
    public void startGame(GameSettings settings) {
        this.settings = settings;
        model = new GameModel(settings);
        configureLabels();
        startTimer();
        Platform.runLater(root::requestFocus);
        draw();
    }

    @FXML
    private void togglePause() {
        if (model == null) {
            return;
        }
        if (model.getStatus() == GameStatus.RUNNING) {
            model.pause();
            pauseButton.setText("Продолжить");
        } else if (model.getStatus() == GameStatus.PAUSED) {
            model.resume();
            pauseButton.setText("Пауза");
            lastTick = 0;
        }
        updateLabels();
        draw();
        root.requestFocus();
    }

    @FXML
    private void backToMenu() {
        stopTimer();
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(
                    "/ru/nsu/filippova/menu.fxml"));
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setTitle("Змейка");
            stage.setScene(new Scene(loader.load(), 920, 680));
            stage.centerOnScreen();
        } catch (IOException exception) {
            statusLabel.setText("Ошибка загрузки меню");
        }
    }

    private void startTimer() {
        stopTimer();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTick == 0) {
                    lastTick = now;
                    return;
                }
                if (now - lastTick >= settings.difficulty().tickNanos()) {
                    model.tick();
                    lastTick = now;
                    updateLabels();
                    draw();
                    if (model.getStatus() == GameStatus.WON || model.getStatus()
                            == GameStatus.LOST) {
                        stopTimer();
                    }
                }
            }
        };
        timer.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    private void handleKey(KeyCode code) {
        if (model == null) {
            return;
        }
        if (code == KeyCode.UP || code == KeyCode.W) {
            model.setPlayerDirection(Direction.UP);
        } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
            model.setPlayerDirection(Direction.RIGHT);
        } else if (code == KeyCode.DOWN || code == KeyCode.S) {
            model.setPlayerDirection(Direction.DOWN);
        } else if (code == KeyCode.LEFT || code == KeyCode.A) {
            model.setPlayerDirection(Direction.LEFT);
        } else if (code == KeyCode.SPACE) {
            togglePause();
        } else if (code == KeyCode.ESCAPE) {
            backToMenu();
        } else if (code == KeyCode.ENTER && (model.getStatus() == GameStatus.WON
                || model.getStatus() == GameStatus.LOST)) {
            startGame(settings);
        }
    }

    private void configureLabels() {
        String modeText = settings.mode() == GameMode.INFINITE
                ? "Режим: бесконечный"
                : "Режим: до длины " + settings.targetLength();
        modeLabel.setText(modeText + " | " + settings.difficulty() + " | " + settings.mapType());
        updateLabels();
    }

    private void updateLabels() {
        if (model == null) {
            return;
        }
        lengthLabel.setText("Длина: " + model.getPlayerSnake().length());
        if (model.getStatus() == GameStatus.RUNNING) {
            statusLabel.setText("Еда: " + model.getFood().size()
                    + " | Роботы: " + (model.getSnakes().size() - 1));
        } else {
            statusLabel.setText(model.getMessage());
        }
    }

    private void draw() {
        if (gameCanvas == null || model == null) {
            return;
        }
        GraphicsContext graphics = gameCanvas.getGraphicsContext2D();
        double width = gameCanvas.getWidth();
        double height = gameCanvas.getHeight();
        graphics.setFill(BACKGROUND);
        graphics.fillRect(0, 0, width, height);

        double cellSize = Math.min(width / model.getColumns(), height / model.getRows());
        double boardWidth = cellSize * model.getColumns();
        double boardHeight = cellSize * model.getRows();
        double offsetX = (width - boardWidth) / 2.0;
        double offsetY = (height - boardHeight) / 2.0;

        drawGrid(graphics, cellSize, offsetX, offsetY);
        drawCells(graphics, model.getObstacles(), OBSTACLE, cellSize, offsetX, offsetY, 0.10);
        drawFood(graphics, cellSize, offsetX, offsetY);
        drawSnakes(graphics, cellSize, offsetX, offsetY);
        drawBorder(graphics, boardWidth, boardHeight, offsetX, offsetY);

        if (model.getStatus() == GameStatus.WON || model.getStatus() == GameStatus.LOST
                || model.getStatus() == GameStatus.PAUSED) {
            drawOverlay(graphics, width, height);
        }
    }

    private void drawGrid(GraphicsContext graphics, double cellSize,
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
                           double cellSize, double offsetX,
                           double offsetY, double insetRatio) {
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

    private void drawFood(GraphicsContext graphics, double cellSize,
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

    private void drawSnakes(GraphicsContext graphics, double cellSize,
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

    private void drawOverlay(GraphicsContext graphics, double width, double height) {
        graphics.setFill(Color.rgb(0, 0, 0, 0.56));
        graphics.fillRect(0, 0, width, height);
        graphics.setFill(Color.WHITE);
        graphics.setFont(javafx.scene.text.Font.font("System", 34));
        String title = switch (model.getStatus()) {
            case WON -> "Победа";
            case LOST -> "Поражение";
            case PAUSED -> "Пауза";
            default -> "";
        };
        double titleWidth = graphics.getFont().getSize() * title.length() * 0.55;
        graphics.fillText(title, (width - titleWidth) / 2.0, height / 2.0 - 20);

        graphics.setFont(javafx.scene.text.Font.font("System", 16));
        String hint = model.getStatus() == GameStatus.PAUSED
                ? "Пробел - продолжить | Esc - меню"
                : "Enter - новая игра | Esc - меню";
        double hintWidth = graphics.getFont().getSize() * hint.length() * 0.47;
        graphics.fillText(hint, (width - hintWidth) / 2.0, height / 2.0 + 18);
    }
}
