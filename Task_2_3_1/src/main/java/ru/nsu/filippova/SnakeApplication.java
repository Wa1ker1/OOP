package ru.nsu.filippova;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * JavaFX-приложение игры "Змейка".
 */
public class SnakeApplication extends Application {
    /**
     * Открывает главное окно с меню.
     *
     * @param stage главное окно JavaFX
     * @throws IOException если FXML-файл меню не удалось загрузить
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(SnakeApplication.class.getResource(
                "/ru/nsu/filippova/menu.fxml"));
        Scene scene = new Scene(loader.load(), 920, 680);
        stage.setTitle("Змейка");
        stage.setMinWidth(760);
        stage.setMinHeight(560);
        stage.setScene(scene);
        stage.show();
    }
}
