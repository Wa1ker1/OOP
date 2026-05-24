package ru.nsu.filippova.view;

import java.io.IOException;
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
import ru.nsu.filippova.controller.GameController;
import ru.nsu.filippova.model.Direction;
import ru.nsu.filippova.model.GameMode;
import ru.nsu.filippova.model.GameModel;
import ru.nsu.filippova.model.GameSettings;
import ru.nsu.filippova.model.GameStatus;

/**
 * View игрового экрана.
 */
public class GameView {
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
    private GameController controller;
    private GameModel model;
    private GameRenderer renderer;

    @FXML
    private void initialize() {
        renderer = new GameRenderer(gameCanvas);
        gameCanvas.widthProperty().bind(gamePane.widthProperty());
        gameCanvas.heightProperty().bind(gamePane.heightProperty());
        gameCanvas.widthProperty().addListener((ignored, oldValue, newValue) -> updateView());
        gameCanvas.heightProperty().addListener((ignored, oldValue, newValue) -> updateView());

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
        controller = new GameController(settings);
        model = controller.getModel();
        model.addChangeListener(this::updateView);

        configureLabels();
        controller.start();
        Platform.runLater(root::requestFocus);
        updateView();
    }

    @FXML
    private void togglePause() {
        if (controller != null) {
            controller.togglePause();
            root.requestFocus();
        }
    }

    @FXML
    private void backToMenu() {
        if (controller != null) {
            controller.stop();
        }
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

    private void handleKey(KeyCode code) {
        if (controller == null || model == null) {
            return;
        }
        if (code == KeyCode.UP || code == KeyCode.W) {
            controller.setPlayerDirection(Direction.UP);
        } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
            controller.setPlayerDirection(Direction.RIGHT);
        } else if (code == KeyCode.DOWN || code == KeyCode.S) {
            controller.setPlayerDirection(Direction.DOWN);
        } else if (code == KeyCode.LEFT || code == KeyCode.A) {
            controller.setPlayerDirection(Direction.LEFT);
        } else if (code == KeyCode.SPACE) {
            togglePause();
        } else if (code == KeyCode.ESCAPE) {
            backToMenu();
        } else if (code == KeyCode.ENTER && isFinished()) {
            controller.stop();
            startGame(settings);
        }
    }

    private boolean isFinished() {
        return model.getStatus() == GameStatus.WON || model.getStatus() == GameStatus.LOST;
    }

    private void configureLabels() {
        String modeText = settings.mode() == GameMode.INFINITE
                ? "Режим: бесконечный"
                : "Режим: до длины " + settings.targetLength();
        modeLabel.setText(modeText + " | " + settings.difficulty() + " | " + settings.mapType());
    }

    private void updateView() {
        if (model == null) {
            return;
        }
        lengthLabel.setText("Длина: " + model.getPlayerSnake().length());
        pauseButton.setText(model.getStatus() == GameStatus.PAUSED ? "Продолжить" : "Пауза");

        if (model.getStatus() == GameStatus.RUNNING) {
            statusLabel.setText("Еда: " + model.getFood().size()
                    + " | Роботы: " + (model.getSnakes().size() - 1));
        } else {
            statusLabel.setText(model.getMessage());
        }
        renderer.draw(model);
    }
}
