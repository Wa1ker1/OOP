package ru.nsu.filippova;

import java.util.List;

/**
 * Конфигурация симуляции пиццерии.
 */
public class PizzeriaConfig {
    private final long workDurationMs;
    private final long orderIntervalMs;
    private final int totalOrders;
    private final int warehouseCapacity;
    private final long courierDeliveryMs;
    private final List<BakerConfig> bakers;
    private final List<CourierConfig> couriers;

    /**
     * Полный набор параметров симуляции.
     *
     * @param workDurationMs длительность рабочего времени в миллисекундах
     * @param orderIntervalMs интервал между входящими заказами в миллисекундах
     * @param totalOrders общее число заказов (или {@code -1}, если без ограничения)
     * @param warehouseCapacity вместимость склада T
     * @param courierDeliveryMs время доставки в миллисекундах
     * @param bakers настройки пекарей
     * @param couriers настройки курьеров
     */
    public PizzeriaConfig(long workDurationMs,
                          long orderIntervalMs,
                          int totalOrders,
                          int warehouseCapacity,
                          long courierDeliveryMs,
                          List<BakerConfig> bakers,
                          List<CourierConfig> couriers) {
        this.workDurationMs = workDurationMs;
        this.orderIntervalMs = orderIntervalMs;
        this.totalOrders = totalOrders;
        this.warehouseCapacity = Math.max(1, warehouseCapacity);
        this.courierDeliveryMs = courierDeliveryMs;
        this.bakers = bakers;
        this.couriers = couriers;
    }

    /**
     * Возвращает длительность рабочего дня.
     *
     * @return длительность рабочего дня в миллисекундах
     */
    public long getWorkDurationMs() {
        return workDurationMs;
    }

    /**
     * Возвращает интервал генерации заказов.
     *
     * @return интервал генерации заказов в миллисекундах
     */
    public long getOrderIntervalMs() {
        return orderIntervalMs;
    }

    /**
     * Возвращает лимит заказов.
     *
     * @return лимит заказов, {@code -1} если лимита нет
     */
    public int getTotalOrders() {
        return totalOrders;
    }

    /**
     * Возвращает вместимость склада.
     *
     * @return вместимость склада
     */
    public int getWarehouseCapacity() {
        return warehouseCapacity;
    }

    /**
     * Возвращает время курьерской доставки.
     *
     * @return время курьерской доставки в миллисекундах
     */
    public long getCourierDeliveryMs() {
        return courierDeliveryMs;
    }

    /**
     * Возвращает список конфигураций пекарей.
     *
     * @return список конфигураций пекарей
     */
    public List<BakerConfig> getBakers() {
        return bakers;
    }

    /**
     * Возвращает список конфигураций курьеров.
     *
     * @return список конфигураций курьеров
     */
    public List<CourierConfig> getCouriers() {
        return couriers;
    }

    /**
     * Конфигурация отдельного пекаря.
     */
    public static class BakerConfig {
        private final int id;
        private final long bakingTimeMs;

        /**
         * Создает конфигурацию пекаря.
         *
         * @param id идентификатор пекаря
         * @param bakingTimeMs время выпечки одной пиццы в миллисекундах
         */
        public BakerConfig(int id, long bakingTimeMs) {
            this.id = id;
            this.bakingTimeMs = bakingTimeMs;
        }

        /**
         * Возвращает идентификатор пекаря.
         *
         * @return id пекаря
         */
        public int getId() {
            return id;
        }

        /**
         * Возвращает время выпечки одной пиццы.
         *
         * @return время выпечки одной пиццы
         */
        public long getBakingTimeMs() {
            return bakingTimeMs;
        }
    }

    /**
     * Конфигурация отдельного курьера.
     */
    public static class CourierConfig {
        private final int id;
        private final int capacity;

        /**
         * Создает конфигурацию курьера.
         *
         * @param id идентификатор курьера
         * @param capacity вместимость багажника
         */
        public CourierConfig(int id, int capacity) {
            this.id = id;
            this.capacity = capacity;
        }

        /**
         * Возвращает идентификатор курьера.
         *
         * @return id курьера
         */
        public int getId() {
            return id;
        }

        /**
         * Возвращает вместимость багажника.
         *
         * @return вместимость багажника
         */
        public int getCapacity() {
            return capacity;
        }
    }
}
