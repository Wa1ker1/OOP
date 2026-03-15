package ru.nsu.filippova;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class PizzaSimulationTest {
    @Test
    @Timeout(10)
    void simulationProcessesOnlyPlannedOrdersToDelivery() throws Exception {
        Path configPath = Files.createTempFile("pizza-simulation-config", ".json");
        Files.writeString(
                configPath,
                "{"
                        + "\"workDurationMs\":400,"
                        + "\"orderIntervalMs\":20,"
                        + "\"totalOrders\":4,"
                        + "\"warehouseCapacity\":4,"
                        + "\"courierDeliveryMs\":10,"
                        + "\"bakers\":[{\"id\":1,\"bakingTimeMs\":20}],"
                        + "\"couriers\":[{\"id\":1,\"capacity\":2}]"
                        + "}"
        );

        ByteArrayOutputStream logs = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(logs, true, StandardCharsets.UTF_8));

        try {
            Main.main(new String[]{configPath.toString()});
        } finally {
            System.setOut(originalOut);
        }

        String output = logs.toString(StandardCharsets.UTF_8);
        long received = countState(output, "RECEIVED");
        long delivered = countState(output, "DELIVERED");

        assertEquals(4, received);
        assertEquals(4, delivered);
    }

    private long countState(String output, String state) {
        return Arrays.stream(output.split("\\R"))
                .filter(line -> line.trim().endsWith(" " + state))
                .count();
    }
}
