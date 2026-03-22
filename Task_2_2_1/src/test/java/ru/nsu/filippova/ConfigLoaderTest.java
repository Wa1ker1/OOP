package ru.nsu.filippova;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import ru.nsu.filippova.config.ConfigLoader;
import ru.nsu.filippova.config.PizzeriaConfig;

class ConfigLoaderTest {
    @Test
    void loadParsesProvidedConfig() throws IOException {
        Path tempConfig = Files.createTempFile("pizza-config", ".json");
        Files.writeString(
                tempConfig,
                "{"
                        + "\"workDurationMs\":1000,"
                        + "\"orderIntervalMs\":50,"
                        + "\"totalOrders\":4,"
                        + "\"warehouseCapacity\":5,"
                        + "\"courierDeliveryMs\":150,"
                        + "\"bakers\":["
                        + "{\"id\":1,\"bakingTimeMs\":100},"
                        + "{\"id\":2,\"bakingTimeMs\":200}],"
                        + "\"couriers\":[{\"id\":1,\"capacity\":2}]"
                        + "}"
        );

        PizzeriaConfig config = ConfigLoader.load(tempConfig.toString());

        assertEquals(1000, config.getWorkDurationMs());
        assertEquals(50, config.getOrderIntervalMs());
        assertEquals(4, config.getTotalOrders());
        assertEquals(5, config.getWarehouseCapacity());
        assertEquals(150, config.getCourierDeliveryMs());
        assertEquals(2, config.getBakers().size());
        assertEquals(2, config.getBakers().get(1).getId());
        assertEquals(1, config.getCouriers().size());
        assertEquals(2, config.getCouriers().get(0).getCapacity());
    }

    @Test
    void loadUsesDefaultValuesForMissingSections() throws IOException {
        Path tempConfig = Files.createTempFile("pizza-config-defaults", ".json");
        Files.writeString(tempConfig, "{\"workDurationMs\":700}");

        PizzeriaConfig config = ConfigLoader.load(tempConfig.toString());

        assertEquals(700, config.getWorkDurationMs());
        assertEquals(400, config.getOrderIntervalMs());
        assertEquals(-1, config.getTotalOrders());
        assertEquals(10, config.getWarehouseCapacity());
        assertEquals(800, config.getCourierDeliveryMs());
        assertEquals(1, config.getBakers().size());
        assertEquals(1, config.getCouriers().size());
        assertEquals(1, config.getBakers().get(0).getId());
        assertEquals(1, config.getCouriers().get(0).getId());
    }
}
