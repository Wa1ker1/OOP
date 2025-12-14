package ru.nsu.filippova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Интеграционные тесты для CLI {@link Main}.
 */
class MainCliTest {

    @TempDir
    Path tempDir;

    @Test
    void printsOccurrencesAndExitsZero() throws Exception {
        Path file = tempDir.resolve("input.txt");
        Files.writeString(file, "абракадабра", StandardCharsets.UTF_8);

        CliResult result = runMain(file.toString(), "бра");

        assertEquals(0, result.exitCode);
        assertEquals("[1, 8]", result.stdout);
        assertEquals("", result.stderr);
    }

    @Test
    void missingArgsPrintsUsageAndExitsOne() throws Exception {
        CliResult result = runMain();

        assertEquals(1, result.exitCode);
        assertEquals("", result.stdout);
        assertEquals("Использование: java ru.nsu.filippova.Main <file> <pattern>", result.stderr);
    }

    @Test
    void missingFileReportsIoErrorAndExitsTwo() throws Exception {
        Path missing = tempDir.resolve("not_exists.txt");

        CliResult result = runMain(missing.toString(), "abra");

        assertEquals(2, result.exitCode);
        assertEquals("", result.stdout);
        assertTrue(result.stderr.startsWith("Ошибка чтения файла: "));
    }

    @Test
    void emptyPatternReportsIllegalArgumentAndExitsThree() throws Exception {
        Path file = tempDir.resolve("input.txt");
        Files.writeString(file, "hello", StandardCharsets.UTF_8);

        CliResult result = runMain(file.toString(), "");

        assertEquals(3, result.exitCode);
        assertEquals("", result.stdout);
        assertEquals("Неверные аргументы: Pattern must not be null or empty", result.stderr);
    }

    private CliResult runMain(String... args) throws IOException, InterruptedException {
        String javaExecutable = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        String classpath = System.getProperty("java.class.path");

        List<String> command = new ArrayList<>();
        command.add(javaExecutable);
        command.add("-cp");
        command.add(classpath);
        command.add(Main.class.getName());
        command.addAll(List.of(args));

        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        int exitCode = process.waitFor();
        String stdout = new String(process.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8).strip();
        String stderr = new String(process.getErrorStream().readAllBytes(), 
                StandardCharsets.UTF_8).strip();

        return new CliResult(exitCode, stdout, stderr);
    }

    private static class CliResult {
        private final int exitCode;
        private final String stdout;
        private final String stderr;

        private CliResult(int exitCode, String stdout, String stderr) {
            this.exitCode = exitCode;
            this.stdout = stdout;
            this.stderr = stderr;
        }
    }
}
