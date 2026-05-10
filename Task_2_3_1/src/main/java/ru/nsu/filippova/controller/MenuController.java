package ru.nsu.filippova.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import ru.nsu.filippova.Main;
import ru.nsu.filippova.model.Difficulty;
import ru.nsu.filippova.model.GameMode;
import ru.nsu.filippova.model.GameSettings;
import ru.nsu.filippova.model.MapType;


/**
 * Контроллер меню настроек игры.
 */
public class MenuController {
    @FXML
    private RadioButton infiniteModeButton;
    @FXML
    private RadioButton targetModeButton;
    @FXML
    private Spinner<Integer> targetLengthSpinner;
    @FXML
    private Spinner<Integer> enemyCountSpinner;
    @FXML
    private Spinner<Integer> foodCountSpinner;
    @FXML
    private ComboBox<Difficulty> difficultyComboBox;
    @FXML
    private ComboBox<MapType> mapComboBox;
    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        ToggleGroup modeGroup = new ToggleGroup();
        infiniteModeButton.setToggleGroup(modeGroup);
        targetModeButton.setToggleGroup(modeGroup);
        infiniteModeButton.setSelected(true);

        targetLengthSpinner.setValueFactory(new
                SpinnerValueFactory.IntegerSpinnerValueFactory(3, 150, 20));
        targetLengthSpinner.disableProperty().bind(targetModeButton.selectedProperty().not());

        enemyCountSpinner.setValueFactory(new
                SpinnerValueFactory.IntegerSpinnerValueFactory(0, 2, 1));
        foodCountSpinner.setValueFactory(new
                SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 5));

        difficultyComboBox.getItems().setAll(Difficulty.values());
        difficultyComboBox.getSelectionModel().select(Difficulty.MEDIUM);

        mapComboBox.getItems().setAll(MapType.values());
        mapComboBox.getSelectionModel().select(MapType.EMPTY);
    }

    @FXML
    private void startGame() {
        try {
            GameSettings settings = new GameSettings(
                    targetModeButton.isSelected() ? GameMode.TARGET_LENGTH : GameMode.INFINITE,
                    targetLengthSpinner.getValue(),
                    enemyCountSpinner.getValue(),
                    mapComboBox.getValue(),
                    foodCountSpinner.getValue(),
                    difficultyComboBox.getValue()
            );

            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/ru/nsu/filippova/game.fxml"));
            Scene scene = new Scene(loader.load(), 980, 720);
            GameController controller = loader.getController();
            controller.startGame(settings);

            Stage stage = (Stage) targetLengthSpinner.getScene().getWindow();
            stage.setTitle("Змейка - игра");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException exception) {
            errorLabel.setText("Не удалось открыть игру: " + exception.getMessage());
        }
    }
}
