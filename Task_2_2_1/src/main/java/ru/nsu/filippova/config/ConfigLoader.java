package ru.nsu.filippova.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Загрузчик конфигурации из JSON.
 *
 * <p>
 * Использует готовую библиотеку Jackson для разбора файла конфигурации.
 */
public class ConfigLoader {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Загружает конфигурацию из файла.
     *
     * @param path путь к JSON-конфигурации
     * @return заполненный объект конфигурации
     * @throws IOException если файл недоступен/нечитаем
     */
    public static PizzeriaConfig load(String path) throws IOException {
        JsonNode root = OBJECT_MAPPER.readTree(Path.of(path).toFile());

        long workDurationMs = root.path("workDurationMs").asLong(30000L);
        long orderIntervalMs = root.path("orderIntervalMs").asLong(400L);
        int totalOrders = root.path("totalOrders").asInt(-1);
        int warehouseCapacity = root.path("warehouseCapacity").asInt(10);
        long courierDeliveryMs = root.path("courierDeliveryMs").asLong(800L);

        List<PizzeriaConfig.BakerConfig> bakers = parseBakers(root.path("bakers"));
        if (bakers.isEmpty()) {
            bakers.add(new PizzeriaConfig.BakerConfig(1, 1000L));
        }

        List<PizzeriaConfig.CourierConfig> couriers = parseCouriers(root.path("couriers"));
        if (couriers.isEmpty()) {
            couriers.add(new PizzeriaConfig.CourierConfig(1, 1));
        }

        return new PizzeriaConfig(
                workDurationMs,
                orderIntervalMs,
                totalOrders,
                warehouseCapacity,
                courierDeliveryMs,
                bakers,
                couriers
        );
    }

    private static List<PizzeriaConfig.BakerConfig> parseBakers(JsonNode bakersNode) {
        List<PizzeriaConfig.BakerConfig> result = new ArrayList<>();
        if (!bakersNode.isArray()) {
            return result;
        }

        int index = 1;
        for (JsonNode bakerNode : bakersNode) {
            int id = bakerNode.path("id").asInt(index);
            long speed = bakerNode.path("bakingTimeMs").asLong(1000L);
            result.add(new PizzeriaConfig.BakerConfig(id, speed));
            index++;
        }
        return result;
    }

    private static List<PizzeriaConfig.CourierConfig> parseCouriers(JsonNode couriersNode) {
        List<PizzeriaConfig.CourierConfig> result = new ArrayList<>();
        if (!couriersNode.isArray()) {
            return result;
        }

        int index = 1;
        for (JsonNode courierNode : couriersNode) {
            int id = courierNode.path("id").asInt(index);
            int capacity = courierNode.path("capacity").asInt(1);
            result.add(new PizzeriaConfig.CourierConfig(id, capacity));
            index++;
        }
        return result;
    }
}
