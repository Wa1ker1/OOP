package ru.nsu.filippova;

/**
 * Поток пекаря.
 *
 * <p>
 * Берет заказ из общей очереди, «готовит» его в течение заданного времени и
 * передает пиццу на склад.
 */
public class BakerWorker extends Thread {
    private final long bakingTimeMs;
    private final OrderQueue orderQueue;
    private final Warehouse warehouse;

    /**
     * Создает поток пекаря.
     *
     * @param id идентификатор пекаря (используется в имени потока)
     * @param bakingTimeMs время приготовления одной пиццы в миллисекундах
     * @param orderQueue очередь входящих заказов
     * @param warehouse склад готовой продукции
     */
    public BakerWorker(int id, long bakingTimeMs, OrderQueue orderQueue, Warehouse warehouse) {
        super("Baker-" + id);
        this.bakingTimeMs = Math.max(1L, bakingTimeMs);
        this.orderQueue = orderQueue;
        this.warehouse = warehouse;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Order order = orderQueue.take();
                if (order == null) {
                    return;
                }
                order.setState(OrderState.BAKING);
                try {
                    Thread.sleep(bakingTimeMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                warehouse.put(order);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
