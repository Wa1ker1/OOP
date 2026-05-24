package ru.nsu.filippova.simulation;

import java.util.ArrayList;
import java.util.List;
import ru.nsu.filippova.config.PizzeriaConfig;
import ru.nsu.filippova.worker.BakerWorker;
import ru.nsu.filippova.worker.CourierWorker;

/**
 * Оркестратор симуляции пиццерии.
 *
 * <p>
 * Координирует запуск и завершение потоков: генератора заказов, пекарей и курьеров.
 * Реализует логику "рабочий день закончился" — новые заказы больше не принимаются,
 * но все взятые и незавершенные заказы доводятся до доставки.
 */
public class PizzaSimulation {
    private final PizzeriaConfig config;
    private final OrderQueue orderQueue;
    private final Warehouse warehouse;
    private final List<BakerWorker> bakers;
    private final List<CourierWorker> couriers;

    /**
     * Создает симулятор по заданной конфигурации.
     *
     * @param config конфигурация
     */
    public PizzaSimulation(PizzeriaConfig config) {
        this.config = config;
        this.orderQueue = new OrderQueue();
        this.warehouse = new Warehouse(config.getWarehouseCapacity());
        this.bakers = createBakers(config);
        this.couriers = createCouriers(config);
    }

    /**
     * Запускает и завершает все потоки симуляции.
     *
     * @throws InterruptedException если текущий поток ожидавания join был прерван
     */
    public void run() throws InterruptedException {
        for (Thread courier : couriers) {
            courier.start();
        }
        for (Thread baker : bakers) {
            baker.start();
        }

        Thread orderGenerator = new Thread(new OrderGenerator(config, orderQueue),
                "OrderGenerator");
        orderGenerator.start();

        orderGenerator.join();
        for (Thread baker : bakers) {
            baker.join();
        }

        warehouse.closeForDelivery();
        for (Thread courier : couriers) {
            courier.join();
        }
    }

    private List<BakerWorker> createBakers(PizzeriaConfig config) {
        List<BakerWorker> result = new ArrayList<>();
        for (PizzeriaConfig.BakerConfig bakerConfig : config.getBakers()) {
            result.add(new BakerWorker(
                    bakerConfig.getId(),
                    bakerConfig.getBakingTimeMs(),
                    orderQueue,
                    warehouse
            ));
        }
        return result;
    }

    private List<CourierWorker> createCouriers(PizzeriaConfig config) {
        List<CourierWorker> result = new ArrayList<>();
        for (PizzeriaConfig.CourierConfig courierConfig : config.getCouriers()) {
            result.add(new CourierWorker(
                    courierConfig.getId(),
                    courierConfig.getCapacity(),
                    config.getCourierDeliveryMs(),
                    warehouse
            ));
        }
        return result;
    }
}
