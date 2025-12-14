package ru.nsu.filippova;

import java.io.IOException;
import java.io.PrintStream;
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
        int exitCode = run(args, System.out, System.err);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    /**
     * Реализация CLI-логики, выделенная для удобства тестирования.
     *
     * @param args аргументы командной строки
     * @param out  поток стандартного вывода
     * @param err  поток стандартного вывода ошибок
     * @return код завершения (0 – успех, иначе ошибка)
     */
    static int run(String[] args, PrintStream out, PrintStream err) {
        if (args.length < 2) {
            err.println("Использование: java ru.nsu.filippova.Main <file> <pattern>");
            return 1;
        }

        Path file = Path.of(args[0]);
        String pattern = args[1];

        SubstringSearcher searcher = new StreamingFileSubstringSearcher();

        try {
            List<Long> occurrences = searcher.findOccurrences(file, pattern);
            out.println(occurrences);
            return 0;
        } catch (IOException e) {
            err.println("Ошибка чтения файла: " + e.getMessage());
            return 2;
        } catch (IllegalArgumentException e) {
            err.println("Неверные аргументы: " + e.getMessage());
            return 3;
        }
    }
}
