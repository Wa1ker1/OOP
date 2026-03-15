package ru.nsu.filippova;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Минималистичный загрузчик конфигурации из JSON.
 *
 * <p>
 * Реализован без внешних библиотек для ограничения зависимостей и
 * учебной демонстрации ручного разбора структуры.
 */
public class ConfigLoader {
    /**
     * Загружает конфигурацию из файла.
     *
     * @param path путь к JSON-конфигурации
     * @return заполненный объект конфигурации
     * @throws IOException если файл недоступен/нечитаем
     */
    public static PizzeriaConfig load(String path) throws IOException {
        String json = Files.readString(Path.of(path));

        long workDurationMs = parseLong(json, "workDurationMs", 30000L);
        long orderIntervalMs = parseLong(json, "orderIntervalMs", 400L);
        int totalOrders = parseInt(json, "totalOrders", -1);
        int warehouseCapacity = parseInt(json, "warehouseCapacity", 10);
        long courierDeliveryMs = parseLong(json, "courierDeliveryMs", 800L);

        List<PizzeriaConfig.BakerConfig> bakers = parseBakers(json);
        if (bakers.isEmpty()) {
            bakers.add(new PizzeriaConfig.BakerConfig(1, 1000L));
        }

        List<PizzeriaConfig.CourierConfig> couriers = parseCouriers(json);
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

    private static List<PizzeriaConfig.BakerConfig> parseBakers(String json) {
        List<String> objects = extractTopLevelObjects(json, "bakers");
        List<PizzeriaConfig.BakerConfig> result = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            String objectText = objects.get(i);
            int id = parseInt(objectText, "id", i + 1);
            long speed = parseLong(objectText, "bakingTimeMs", 1000L);
            result.add(new PizzeriaConfig.BakerConfig(id, speed));
        }
        return result;
    }

    private static List<PizzeriaConfig.CourierConfig> parseCouriers(String json) {
        List<String> objects = extractTopLevelObjects(json, "couriers");
        List<PizzeriaConfig.CourierConfig> result = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            String objectText = objects.get(i);
            int id = parseInt(objectText, "id", i + 1);
            int capacity = parseInt(objectText, "capacity", 1);
            result.add(new PizzeriaConfig.CourierConfig(id, capacity));
        }
        return result;
    }

    private static List<String> extractTopLevelObjects(String json, String arrayKey) {
        int arrayStart = findArrayStart(json, arrayKey);
        if (arrayStart == -1) {
            return Collections.emptyList();
        }

        int arrayEnd = findMatchingBracket(json, arrayStart);
        if (arrayEnd == -1) {
            return Collections.emptyList();
        }

        String content = json.substring(arrayStart + 1, arrayEnd);
        if (content.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<String> objects = new ArrayList<>();
        int depth = 0;
        int objectStart = -1;
        boolean inString = false;
        char prev = 0;

        for (int i = 0; i < content.length(); i++) {
            char current = content.charAt(i);

            if (current == '"' && prev != '\\') {
                inString = !inString;
            } else if (!inString) {
                if (current == '{') {
                    if (depth == 0) {
                        objectStart = i;
                    }
                    depth++;
                } else if (current == '}') {
                    depth--;
                    if (depth == 0 && objectStart >= 0) {
                        objects.add(content.substring(objectStart, i + 1));
                        objectStart = -1;
                    }
                }
            }
            prev = current;
        }
        return objects;
    }

    private static int findArrayStart(String json, String key) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\\[");
        Matcher m = p.matcher(json);
        if (!m.find()) {
            return -1;
        }
        return m.end() - 1;
    }

    private static int findMatchingBracket(String json, int arrayStart) {
        int depth = 0;
        for (int i = arrayStart; i < json.length(); i++) {
            char current = json.charAt(i);
            if (current == '[') {
                depth++;
            } else if (current == ']') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static int parseInt(String json, String key, int defaultValue) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(-?\\d+)");
        Matcher m = p.matcher(json);
        if (!m.find()) {
            return defaultValue;
        }
        return Integer.parseInt(m.group(1));
    }

    private static long parseLong(String json, String key, long defaultValue) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(-?\\d+)");
        Matcher m = p.matcher(json);
        if (!m.find()) {
            return defaultValue;
        }
        return Long.parseLong(m.group(1));
    }
}

