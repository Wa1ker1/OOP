package ru.nsu.filippova;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Интерфейс для поиска всех вхождений подстроки в текстовом файле (UTF-8).
 */
public interface SubstringSearcher {

    /**
     * Находит все вхождения подстроки pattern в текстовом файле file.
     * Индексы считаются по символам, начиная с 0.
     *
     * @param file    путь к файлу (UTF-8)
     * @param pattern искомая подстрока
     * @return список индексов начала каждого вхождения
     * @throws IOException              при ошибках ввода-вывода
     * @throws IllegalArgumentException если pattern пустая или null
     */
    List<Long> findOccurrences(Path file, String pattern) throws IOException;
}
