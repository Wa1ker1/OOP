package ru.nsu.filippova;

import java.io.IOException;
import ru.nsu.filippova.config.ConfigLoader;
import ru.nsu.filippova.config.PizzeriaConfig;
import ru.nsu.filippova.simulation.PizzaSimulation;

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
     */
    public static void main(String[] args) {
        String configPath = args.length > 0 ? args[0] : "config.json";
        try {
            PizzeriaConfig config = ConfigLoader.load(configPath);
            new PizzaSimulation(config).run();
        } catch (IOException e) {
            System.err.println("Failed to load config: " + configPath);
            System.err.println(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Pizza simulation was interrupted.");
            System.err.println(e.getMessage());
        }
    }
}
