package ru.nsu.filippova;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;



class BlackjackApplicationTest {

    @Test
    void mainCompletesSingleRoundWithProvidedInput() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayInputStream fakeInput = new ByteArrayInputStream(
                "0\n0\n".getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        System.setIn(fakeInput);
        System.setOut(new PrintStream(sink, true));
        try {
            BlackjackApplication.main(new String[0]);
        } catch (Exception e) {
            throw new AssertionError("Main method should not throw", e);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        assertTrue(sink.toString().contains("Спасибо за игру"));
    }
}
