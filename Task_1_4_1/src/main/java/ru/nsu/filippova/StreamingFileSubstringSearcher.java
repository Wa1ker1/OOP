package ru.nsu.filippova;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Потоковый поиск подстроки в текстовом файле большого размера.
 * Работает с UTF-8, индексирует по символам.
 */

public class StreamingFileSubstringSearcher implements SubstringSearcher {

    private final int bufferSize;

    /**
     * Создает поисковик с пользовательским размером буфера.
     *
     * @param bufferSize размер буфера чтения (в символах), должен быть > 0
     */
    public StreamingFileSubstringSearcher(int bufferSize) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("bufferSize must be > 0");
        }
        this.bufferSize = bufferSize;
    }

    /**
     * Конструктор с буфером по умолчанию (8 КБ).
     */
    public StreamingFileSubstringSearcher() {
        this(8192);
    }

    /**
     * Потоково читает файл и возвращает индексы всех вхождений pattern.
     * Реализует поиск с перекрывающимися блоками, чтобы корректно обрабатывать границы буфера.
     *
     * @param file    путь к текстовому файлу UTF-8
     * @param pattern искомая подстрока
     * @return список индексов начала всех вхождений
     * @throws IOException              если произошла ошибка ввода-вывода
     * @throws IllegalArgumentException если pattern null/пустая или файл не существует
     */
    @Override
    public List<Long> findOccurrences(Path file, String pattern) throws IOException {
        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("Pattern must not be null or empty");
        }

        List<Long> result = new ArrayList<>();
        char[] patternChars = pattern.toCharArray();
        int m = patternChars.length;

        char[] buffer = new char[bufferSize];
        StringBuilder tail = new StringBuilder();

        long globalIndex = 0L;

        try (Reader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(file), StandardCharsets.UTF_8))) {

            int read;
            while ((read = reader.read(buffer)) != -1) {
                StringBuilder chunk = new StringBuilder(tail.length() + read);
                chunk.append(tail);
                chunk.append(buffer, 0, read);

                char[] chunkChars = new char[chunk.length()];
                chunk.getChars(0, chunk.length(), chunkChars, 0);

                long baseIndex = globalIndex - tail.length();
                findInChunk(chunkChars, patternChars, result, baseIndex);

                tail.setLength(0);
                int tailLen = Math.min(m - 1, chunk.length());
                if (tailLen > 0) {
                    tail.append(chunk, chunk.length() - tailLen, chunk.length());
                }

                globalIndex += read;
            }
        }

        return result;
    }

    /**
     * Наивный поиск patternChars в chunkChars.
     * Проверяется только диапазон [0..n-m].
     *
     * @param chunkChars   текущий блок символов (хвост + новые данные)
     * @param patternChars искомая подстрока
     * @param result       сюда добавляются найденные индексы
     * @param baseIndex    индекс chunkChars[0] в общем потоке символов
     */
    private void findInChunk(char[] chunkChars,
                             char[] patternChars,
                             List<Long> result,
                             long baseIndex) {

        int n = chunkChars.length;
        int m = patternChars.length;

        outer:
        for (int i = 0; i <= n - m; i++) {
            for (int j = 0; j < m; j++) {
                if (chunkChars[i + j] != patternChars[j]) {
                    continue outer;
                }
            }
            long position = baseIndex + i;
            if (position >= 0) {
                result.add(position);
            }
        }
    }
}
