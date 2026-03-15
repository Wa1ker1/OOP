package ru.nsu.filippova;

import java.io.IOException;

/**
 * Точка входа в приложение.
 *
 * <p>
 * Запускает симуляцию пиццерии по конфигурации из JSON-файла.
 * Если путь к конфигу не указан, используется {@code config.json} в рабочей директории.
 */
public class Main {
    /**
     * Точка входа.
     *
     * @param args аргументы командной строки; первым аргументом может быть путь к JSON-конфигу
     * @throws IOException если не удалось прочитать конфиг
     * @throws InterruptedException если один из потоков симуляции был прерван
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        String configPath = args.length > 0 ? args[0] : "config.json";
        PizzeriaConfig config = ConfigLoader.load(configPath);
        new PizzaSimulation(config).run();
    }
}
