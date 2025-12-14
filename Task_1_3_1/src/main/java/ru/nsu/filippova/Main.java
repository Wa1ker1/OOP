package ru.nsu.filippova;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Консольный интерфейс:
 * args[0] – имя файла,
 * args[1] – искомая подстрока.
 * Пример:
 *   java ru.nsu.filippova.Main input.txt "бра"
 */
public class Main {

    /**
     * Точка входа CLI: читает аргументы, запускает поиск и выводит найденные индексы.
     *
     * @param args args[0] – путь к файлу, args[1] – подстрока
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Использование: java ru.nsu.filippova.Main <file> <pattern>");
            System.exit(1);
        }

        Path file = Path.of(args[0]);
        String pattern = args[1];

        SubstringSearcher searcher = new StreamingFileSubstringSearcher();

        try {
            List<Long> occurrences = searcher.findOccurrences(file, pattern);
            System.out.println(occurrences);
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
            System.exit(2);
        } catch (IllegalArgumentException e) {
            System.err.println("Неверные аргументы: " + e.getMessage());
            System.exit(3);
        }
    }
}
