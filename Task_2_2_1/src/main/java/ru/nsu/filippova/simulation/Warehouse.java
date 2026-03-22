package ru.nsu.filippova.simulation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import ru.nsu.filippova.model.Order;
import ru.nsu.filippova.model.OrderState;

/**
 * Склад готовой продукции с фиксированной вместимостью.
 *
 * <p>
 * Реализует ручную синхронизацию для безопасного доступа пекарей и курьеров.
 */
public class Warehouse {
    private final int capacity;
    private final ArrayDeque<Order> pizzas = new ArrayDeque<>();
    private boolean closedForDelivery = false;

    /**
     * Создает склад с заданной максимальной вместимостью.
     *
     * @param capacity вместимость складских ячеек
     */
    public Warehouse(int capacity) {
        this.capacity = Math.max(1, capacity);
    }

    /**
     * Помещает готовую пиццу на склад.
     * Если склад заполнен, пекарь блокируется до освобождения места.
     *
     * @param order заказ-пицца
     * @throws InterruptedException если поток был прерван во время ожидания места
     */
    public synchronized void put(Order order) throws InterruptedException {
        while (pizzas.size() >= capacity) {
            order.setState(OrderState.WAITING_STORAGE);
            wait();
        }
        order.setState(OrderState.READY_FOR_STORAGE);
        pizzas.addLast(order);
        order.setState(OrderState.STORED);
        notifyAll();
    }

    /**
     * Забирает с склада одну или несколько пицц не более {@code max}.
     * Если склад пуст, поток ожидает появления товара.
     * Если склад закрыт для доставки и пуст, возвращает пустой список.
     *
     * @param max максимальное число пицц в партии
     * @return список взятых заказов
     * @throws InterruptedException если поток был прерван во время ожидания
     */
    public synchronized List<Order> takeBatch(int max) throws InterruptedException {
        while (pizzas.isEmpty()) {
            if (closedForDelivery) {
                return new ArrayList<>();
            }
            wait();
        }
        int count = Math.min(max, pizzas.size());
        List<Order> batch = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            batch.add(pizzas.removeFirst());
        }
        notifyAll();
        return batch;
    }

    /**
     * Закрывает склад для новых выдач курьерам.
     * При закрытии и пустом складе последующие вызовы {@link #takeBatch(int)} вернут пустой список.
     */
    public synchronized void closeForDelivery() {
        closedForDelivery = true;
        notifyAll();
    }

    /**
     * Проверяет пустоту склада.
     */
    public synchronized boolean isEmpty() {
        return pizzas.isEmpty();
    }

    /**
     * Возвращает текущее количество пицц на складе.
     */
    public synchronized int size() {
        return pizzas.size();
    }
}
