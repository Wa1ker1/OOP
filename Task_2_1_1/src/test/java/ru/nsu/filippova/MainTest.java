package ru.nsu.filippova;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class MainTest {
    @Test
    void buildPrimeDatasetRejectsNegativeSize() {
        assertThrows(IllegalArgumentException.class, () -> Main.buildPrimeDataset(-1));
    }

    @Test
    void buildPrimeDatasetRepeatsPrimeTemplate() {
        int[] expected = {2_147_483_647, 2_147_483_629, 2_147_483_587, 2_147_483_579,
            2_147_483_563, 2_147_483_549, 2_147_483_477, 2_147_483_647, 2_147_483_629,
            2_147_483_587, 2_147_483_579, 2_147_483_563, 2_147_483_549, 2_147_483_477
        };

        assertArrayEquals(expected, Main.buildPrimeDataset(expected.length));
    }

    @Test
    void privateConstructorCanBeInvokedReflectively() throws Exception {
        Constructor<Main> constructor = Main.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    void mainPrintsScenarioNamesAndAllStrategies() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            Main.main(new String[0]);
        } finally {
            System.setOut(originalOut);
        }

        String text = output.toString(StandardCharsets.UTF_8);

        assertTrue(text.contains("Первые элементы набора с простыми числами"));
        assertTrue(text.contains("Размер массива: 10000"));
        assertTrue(text.contains("Sequential"));
        assertTrue(text.contains("Thread (1)"));
        assertTrue(text.contains("Thread (2)"));
        assertTrue(text.contains("Thread (4)"));
        assertTrue(text.contains("Thread (8)"));
        assertTrue(text.contains("Parallel Stream"));
        assertTrue(text.contains("result=false"));
    }
}
