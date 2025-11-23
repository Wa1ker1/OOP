package ru.nsu.filippova;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для проверки работы StreamingFileSubstringSearcher на больших файлах.
 * Большие данные здесь генерируются на лету и НЕ лежат в системе контроля версий.
 */
class StreamingFileSubstringSearcherBigFileTest {

    @TempDir
    Path tempDir;

    /**
     * Большой файл (несколько мегабайт), одна уникальная подстрока в середине.
     * Проверяем, что:
     * - поиск не падает по памяти/исключениям;
     * - находит ровно одно вхождение;
     * - индекс совпадает с тем, что мы ожидаем.
     */
    @Test
    void veryLargeFile_singleOccurrenceInTheMiddle() throws IOException {
        Path file = tempDir.resolve("very_large_single.txt");

        int prefixLength = 2_000_000;  
        int suffixLength = 2_000_000;  
        String pattern = "PATTERN";

        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (int i = 0; i < prefixLength; i++) {
                writer.write('a');
            }
            writer.write(pattern);
            for (int i = 0; i < suffixLength; i++) {
                writer.write('b');
            }
        }

        SubstringSearcher searcher = new StreamingFileSubstringSearcher(4096);

        List<Long> result = searcher.findOccurrences(file, pattern);

        assertEquals(1, result.size(), "Должно быть ровно одно вхождение");
        assertEquals((long) prefixLength, result.get(0),
                "Индекс начала паттерна должен совпадать с длиной префикса");
    }

    /**
     * Большой файл с очень большим числом вхождений паттерна.
     * Структура файла: "abcPATTERNabcPATTERN..." много раз.
     * Проверяем:
     * - что находится ровно столько вхождений, сколько раз писали паттерн;
     * - что индексы совпадают с теоретически ожидаемыми;
     * - алгоритм успевает отработать на большом файле.
     */
    @Test
    void veryLargeFile_manyOccurrencesWithKnownPositions() throws IOException {
        Path file = tempDir.resolve("very_large_many.txt");

        String pattern = "PATTERN";
        String chunk = "abc" + pattern; 
        int repeats = 100_000;          

        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (int i = 0; i < repeats; i++) {
                writer.write(chunk);
            }
        }

        SubstringSearcher searcher = new StreamingFileSubstringSearcher(2048);

        List<Long> result = searcher.findOccurrences(file, pattern);

        assertEquals(repeats, result.size(), "Количество вхождений должно совпадать с числом повторов");

        for (int i = 0; i < 10; i++) {
            long expectedIndex = i * (long) chunk.length() + 3L;
            assertEquals(expectedIndex, result.get(i));
        }

        for (int i = repeats - 10; i < repeats; i++) {
            long expectedIndex = i * (long) chunk.length() + 3L;
            assertEquals(expectedIndex, result.get(i));
        }
    }

    /**
     * Большой файл + маленький буфер, чтобы гарантировать множество "пересечений" паттерна
     * через границы буфера.
     */
    @Test
    void veryLargeFile_smallBufferPatternCrossesManyBoundaries() throws IOException {
        Path file = tempDir.resolve("very_large_boundaries.txt");

        String pattern = "бра";
        String block = "абракадабра";
        int repeats = 200_000;

        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (int i = 0; i < repeats; i++) {
                writer.write(block);
            }
        }

        int tinyBuffer = 5;
        SubstringSearcher searcher = new StreamingFileSubstringSearcher(tinyBuffer);

        List<Long> result = searcher.findOccurrences(file, pattern);

        long expectedPerBlock = 2L;
        long expectedTotal = expectedPerBlock * repeats;

        assertEquals(expectedTotal, result.size(), "Общее количество вхождений должно совпадать");

        assertTrue(result.size() >= 4, "Для проверки нужно хотя бы 4 вхождения");

        assertEquals(1L, result.get(0));
        assertEquals(8L, result.get(1));

        long blockLen = block.length(); 
        assertEquals(1L + blockLen, result.get(2));
        assertEquals(8L + blockLen, result.get(3));
    }
}
