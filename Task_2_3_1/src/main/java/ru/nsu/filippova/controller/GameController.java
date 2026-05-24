package ru.nsu.filippova.controller;

import javafx.animation.AnimationTimer;
import ru.nsu.filippova.model.Direction;
import ru.nsu.filippova.model.GameModel;
import ru.nsu.filippova.model.GameSettings;
import ru.nsu.filippova.model.GameStatus;

/**
 * Управляет игровой моделью.
 */
public class GameController {
    private final GameSettings settings;
    private GameModel model;
    private AnimationTimer timer;
    private long lastTick;

    /**
     * Создает контроллер партии.
     *
     * @param settings настройки игры
     */
    public GameController(GameSettings settings) {
        this.settings = settings;
        model = new GameModel(settings);
    }

    /**
     * Возвращает модель текущей партии.
     *
     * @return модель игры
     */
    public GameModel getModel() {
        return model;
    }

    /**
     * Запускает игровой таймер.
     */
    public void start() {
        stop();
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
                    if (model.getStatus() == GameStatus.WON
                            || model.getStatus() == GameStatus.LOST) {
                        stop();
                    }
                }
            }
        };
        timer.start();
    }

    /**
     * Останавливает игровой таймер.
     */
    public void stop() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    /**
     * Меняет направление змейки игрока.
     *
     * @param direction новое направление
     */
    public void setPlayerDirection(Direction direction) {
        model.setPlayerDirection(direction);
    }

    /**
     * Переключает паузу.
     */
    public void togglePause() {
        if (model.getStatus() == GameStatus.RUNNING) {
            model.pause();
        } else if (model.getStatus() == GameStatus.PAUSED) {
            model.resume();
            lastTick = 0;
        }
    }
}
