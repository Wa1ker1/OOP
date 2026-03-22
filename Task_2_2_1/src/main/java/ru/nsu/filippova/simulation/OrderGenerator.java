package ru.nsu.filippova.simulation;

import java.util.concurrent.atomic.AtomicInteger;
import ru.nsu.filippova.config.PizzeriaConfig;
import ru.nsu.filippova.model.Order;
import ru.nsu.filippova.model.OrderState;

/**
 * Генератор поступления заказов.
 *
 * <p>
 * Заказы создаются с заданным интервалом, пока не истечет длительность смены
 * и пока не исчерпана общая квота (totalOrders, если она задана).
 */
public class OrderGenerator implements Runnable {
    private final PizzeriaConfig config;
    private final OrderQueue orderQueue;
    private final AtomicInteger orderId = new AtomicInteger(1);

    /**
     * Создает генератор заказов.
     *
     * @param config конфигурация симуляции
     * @param orderQueue очередь входящих заказов
     */
    public OrderGenerator(PizzeriaConfig config, OrderQueue orderQueue) {
        this.config = config;
        this.orderQueue = orderQueue;
    }

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
