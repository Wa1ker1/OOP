package ru.nsu.filippova.controller;

import java.io.IOException;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ru.nsu.filippova.Main;
import ru.nsu.filippova.model.Direction;
import ru.nsu.filippova.model.GameMode;
import ru.nsu.filippova.model.GameModel;
import ru.nsu.filippova.model.GameSettings;
import ru.nsu.filippova.model.GameStatus;
import ru.nsu.filippova.view.GameRenderer;


/**
 * Контроллер игрового экрана.
 */
public class GameController {
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
    private GameRenderer renderer;
    private AnimationTimer timer;
    private long lastTick;

    @FXML
    private void initialize() {
        renderer = new GameRenderer(gameCanvas);
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
        if (renderer != null) {
            renderer.draw(model);
        }
    }
}
