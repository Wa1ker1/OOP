package ru.nsu.filippova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


class MainTest {

    @TempDir
    Path tempDir;

    @Test
    void mainPrintsOccurrencesForValidInput() throws IOException {
        Path file = tempDir.resolve("input.txt");
        Files.writeString(file, "abracadabra", StandardCharsets.UTF_8);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        int status = Main.run(
                new String[]{file.toString(), "abra"},
                new PrintStream(outContent, true, StandardCharsets.UTF_8),
                System.err
        );

        String output = outContent.toString(StandardCharsets.UTF_8).trim();
        assertEquals("[0, 7]", output);
        assertEquals(0, status);
    }

    @Test
    void mainRequiresTwoArguments() {
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        int status = Main.run(
                new String[]{},
                System.out,
                new PrintStream(errContent, true, StandardCharsets.UTF_8)
        );

        assertEquals(1, status);
        assertTrue(errContent.toString(StandardCharsets.UTF_8).contains("Использование"));
    }

    @Test
    void mainReportsIoErrors() {
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        Path missingFile = tempDir.resolve("missing.txt");
        int status = Main.run(
                new String[]{missingFile.toString(), "abra"},
                System.out,
                new PrintStream(errContent, true, StandardCharsets.UTF_8)
        );

        assertEquals(2, status);
        assertTrue(errContent.toString(StandardCharsets.UTF_8).contains("Ошибка чтения файла"));
    }

    @Test
    void mainReportsIllegalArguments() throws IOException {
        Path file = tempDir.resolve("input.txt");
        Files.writeString(file, "abc", StandardCharsets.UTF_8);

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        int status = Main.run(
                new String[]{file.toString(), ""},
                System.out,
                new PrintStream(errContent, true, StandardCharsets.UTF_8)
        );

        assertEquals(3, status);
        assertTrue(errContent.toString(StandardCharsets.UTF_8).contains("Неверные аргументы"));
    }
}
