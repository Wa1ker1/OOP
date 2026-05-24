package ru.nsu.filippova.model;

/**
 * Набор настроек, выбранных игроком перед началом партии.
 *
 * @param mode режим игры
 * @param targetLength длина, нужная для победы в режиме {@link GameMode#TARGET_LENGTH}
 * @param enemyCount количество змей-роботов
 * @param mapType выбранная карта
 * @param foodCount количество еды, одновременно находящейся на поле
 * @param difficulty сложность игры
 */
public record GameSettings(
        GameMode mode,
        int targetLength,
        int enemyCount,
        MapType mapType,
        int foodCount,
        Difficulty difficulty
) {
    /** Количество строк игрового поля. */
    public static final int ROWS = 24;
    /** Количество столбцов игрового поля. */
    public static final int COLUMNS = 32;
}
