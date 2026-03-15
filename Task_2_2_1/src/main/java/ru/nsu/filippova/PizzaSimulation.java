package ru.nsu.filippova;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

        Thread orderGenerator = new Thread(new OrderGenerator(), "OrderGenerator");
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

    /**
     * Генератор поступления заказов.
     *
     * <p>
     * Заказы создаются с заданным интервалом, пока не истечет длительность смены
     * и пока не исчерпана общая квота (totalOrders, если она задана).
     */
    private class OrderGenerator implements Runnable {
        private final AtomicInteger orderId = new AtomicInteger(1);

        @Override
        public void run() {
            long endOfWorkingDay = System.currentTimeMillis() + config.getWorkDurationMs();
            while (true) {
                if (config.getTotalOrders() > 0 && orderId.get() > config.getTotalOrders()) {
                    break;
                }
                if (System.currentTimeMillis() > endOfWorkingDay) {
                    break;
                }

                Order order = new Order(orderId.getAndIncrement());
                order.setState(OrderState.RECEIVED);
                if (!orderQueue.offer(order)) {
                    return;
                }

                sleepFor(config.getOrderIntervalMs());
            }
            orderQueue.close();
        }

        private void sleepFor(long interval) {
            if (interval <= 0) {
                return;
            }
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

