package ru.nsu.filippova;

import java.util.ArrayDeque;

/**
 * Потокобезопасная ручная очередь заказов.
 *
 * <p>
 * Реализует собственную синхронизацию через {@code synchronized}/{@code wait}/{@code notifyAll}
 * без использования готовых конкурентных коллекций.
 */
public class OrderQueue {
    private final ArrayDeque<Order> orders = new ArrayDeque<>();
    private boolean accepting = true;

    /**
     * Добавляет заказ в очередь.
     *
     * @param order заказ
     * @return {@code true}, если заказ принят; {@code false}, если смена уже закончилась
     */
    public synchronized boolean offer(Order order) {
        if (!accepting) {
            return false;
        }
        order.setState(OrderState.QUEUED);
        orders.addLast(order);
        notifyAll();
        return true;
    }

    /**
     * Извлекает следующий заказ из очереди. Если очередь пуста, поток ожидает появления заказа.
     *
     * @return заказ или {@code null}, если новых заказов больше не будет
     * @throws InterruptedException если поток прерван во время ожидания
     */
    public synchronized Order take() throws InterruptedException {
        while (orders.isEmpty()) {
            if (!accepting) {
                return null;
            }
            wait();
        }
        return orders.removeFirst();
    }

    /**
     * Завершает прием новых заказов после рабочего дня.
     */
    public synchronized void close() {
        accepting = false;
        notifyAll();
    }

    /**
     * Возвращает признак того, что очередь еще принимает заказы.
     */
    public synchronized boolean isAccepting() {
        return accepting;
    }

    /**
     * Проверяет, пуста ли очередь.
     */
    public synchronized boolean isEmpty() {
        return orders.isEmpty();
    }
}
