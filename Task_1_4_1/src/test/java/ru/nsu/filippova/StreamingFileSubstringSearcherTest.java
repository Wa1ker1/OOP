package ru.nsu.filippova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;



/**
 * Тесты для StreamingFileSubstringSearcher.
 */
class StreamingFileSubstringSearcherTest {

    @TempDir
    Path tempDir;

    @Test
    void simpleRussianExample_abraKadabra() throws IOException {
        Path file = tempDir.resolve("input.txt");
        Files.writeString(file, "абракадабра", StandardCharsets.UTF_8);

        SubstringSearcher searcher = new StreamingFileSubstringSearcher();

        List<Long> result = searcher.findOccurrences(file, "бра");

        assertEquals(List.of(1L, 8L), result);
    }

    @Test
    void noOccurrences_returnsEmptyList() throws IOException {
        Path file = tempDir.resolve("no_match.txt");
        Files.writeString(file, "hello world", StandardCharsets.UTF_8);

        SubstringSearcher searcher = new StreamingFileSubstringSearcher();

        List<Long> result = searcher.findOccurrences(file, "xyz");

        assertTrue(result.isEmpty());
    }

    @Test
    void singleCharPattern_manyMatches() throws IOException {
        Path file = tempDir.resolve("singlechar.txt");
        Files.writeString(file, "aaaабвa", StandardCharsets.UTF_8);

        SubstringSearcher searcher = new StreamingFileSubstringSearcher();

        List<Long> result = searcher.findOccurrences(file, "a");

        assertEquals(List.of(0L, 1L, 2L, 6L), result);
    }

    @Test
    void overlappingOccurrences_areAllFound() throws IOException {
        Path file = tempDir.resolve("overlap.txt");
        Files.writeString(file, "aaaaa", StandardCharsets.UTF_8);

        SubstringSearcher searcher = new StreamingFileSubstringSearcher();

        List<Long> result = searcher.findOccurrences(file, "aaa");

        assertEquals(List.of(0L, 1L, 2L), result);
    }

    @Test
    void patternAcrossBufferBoundary_smallBuffer() throws IOException {
        int bufferSize = 4;
        SubstringSearcher searcher = new StreamingFileSubstringSearcher(bufferSize);

        Path file = tempDir.resolve("boundary.txt");
        Files.writeString(file, "абракадабра", StandardCharsets.UTF_8);

        List<Long> result = searcher.findOccurrences(file, "бра");

        assertEquals(List.of(1L, 8L), result);
    }

    @Test
    void patternLongerThanBufferStillWorks() throws IOException {
        int bufferSize = 3;
        SubstringSearcher searcher = new StreamingFileSubstringSearcher(bufferSize);

        Path file = tempDir.resolve("longPattern.txt");
        String content = "xxxHELLO_WORLDxxx";
        Files.writeString(file, content, StandardCharsets.UTF_8);

        List<Long> result = searcher.findOccurrences(file, "HELLO_WORLD");

        assertEquals(List.of(3L), result);
    }

    @Test
    void mixedUnicodeContent_indicesCorrect() throws IOException {
        Path file = tempDir.resolve("unicode.txt");
        String content = "αβγабракадабра漢字бра";
        Files.writeString(file, content, StandardCharsets.UTF_8);

        SubstringSearcher searcher = new StreamingFileSubstringSearcher();

        List<Long> result = searcher.findOccurrences(file, "бра");

        assertEquals(List.of(4L, 11L, 16L), result);
    }

    @Test
    void emptyPatternThrowsException() throws IOException {
        Path file = tempDir.resolve("emptyPattern.txt");
        Files.writeString(file, "something", StandardCharsets.UTF_8);

        SubstringSearcher searcher = new StreamingFileSubstringSearcher();

        assertThrows(IllegalArgumentException.class,
                () -> searcher.findOccurrences(file, ""));
    }

    @Test
    void nullPatternThrowsException() throws IOException {
        Path file = tempDir.resolve("nullPattern.txt");
        Files.writeString(file, "something", StandardCharsets.UTF_8);

        SubstringSearcher searcher = new StreamingFileSubstringSearcher();

        assertThrows(IllegalArgumentException.class,
                () -> searcher.findOccurrences(file, null));
    }

    @Test
    void invalidBufferSizeThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new StreamingFileSubstringSearcher(0));
        assertThrows(IllegalArgumentException.class,
                () -> new StreamingFileSubstringSearcher(-5));
    }

    @Test
    void largeFileButSmallPattern_basicSanity() throws IOException {
        Path file = tempDir.resolve("large.txt");

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 10_000; i++) {
            builder.append("abc");
        }

        String pattern = "PATTERN";
        builder.append(pattern);
        Files.writeString(file, builder.toString(), StandardCharsets.UTF_8);

        SubstringSearcher searcher = new StreamingFileSubstringSearcher(1024);

        List<Long> result = searcher.findOccurrences(file, pattern);

        int pos = builder.length() - pattern.length();

        assertEquals(1, result.size());
        assertEquals((long) pos, result.get(0));
    }
}
