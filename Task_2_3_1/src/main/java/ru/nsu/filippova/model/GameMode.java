package ru.nsu.filippova.model;

/**
 * Режим победы в игре.
 */
public enum GameMode {
    /** Игра идет до столкновения игрока. */
    INFINITE,
    /** Игра завершается победой при достижении заданной длины. */
    TARGET_LENGTH
}
