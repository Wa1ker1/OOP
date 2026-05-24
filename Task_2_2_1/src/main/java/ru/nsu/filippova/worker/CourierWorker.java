package ru.nsu.filippova.worker;

import java.util.List;
import ru.nsu.filippova.model.Order;
import ru.nsu.filippova.model.OrderState;
import ru.nsu.filippova.simulation.Warehouse;

/**
 * Поток курьера.
 *
 * <p>
 * Берет партии пицц со склада (не более лимита багажника) и осуществляет доставку.
 */
public class CourierWorker extends Thread {
    private final int capacity;
    private final long deliveryMs;
    private final Warehouse warehouse;

    /**
     * Создает поток курьера.
     *
     * @param id идентификатор курьера (используется в имени потока)
     * @param capacity вместимость багажника в единицах пицц
     * @param deliveryMs время доставки (условная) в миллисекундах
     * @param warehouse склад готовой продукции
     */
    public CourierWorker(int id, int capacity, long deliveryMs, Warehouse warehouse) {
        super("Courier-" + id);
        this.capacity = Math.max(1, capacity);
        this.deliveryMs = Math.max(1L, deliveryMs);
        this.warehouse = warehouse;
    }

    @Override
    public void run() {
        try {
            while (true) {
                List<Order> batch = warehouse.takeBatch(capacity);
                if (batch.isEmpty()) {
                    return;
                }
                for (Order order : batch) {
                    order.setState(OrderState.OUT_FOR_DELIVERY);
                }
                try {
                    Thread.sleep(deliveryMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                for (Order order : batch) {
                    order.setState(OrderState.DELIVERED);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
