package ru.nsu.filippova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.nsu.filippova.model.Order;
import ru.nsu.filippova.model.OrderState;
import ru.nsu.filippova.simulation.Warehouse;

class WarehouseTest {
    @Test
    void putAndTakeBatchReturnsStoredOrders() throws InterruptedException {
        Warehouse warehouse = new Warehouse(2);
        Order first = new Order(1);
        Order second = new Order(2);

        warehouse.put(first);
        warehouse.put(second);

        List<Order> batch = warehouse.takeBatch(10);

        assertEquals(2, batch.size());
        assertEquals(OrderState.STORED, first.getState());
        assertEquals(OrderState.STORED, second.getState());
        assertTrue(warehouse.isEmpty());
    }

    @Test
    void closeForDeliveryReturnsEmptyWhenNoPizza() throws InterruptedException {
        Warehouse warehouse = new Warehouse(2);
        warehouse.closeForDelivery();
        assertTrue(warehouse.takeBatch(1).isEmpty());
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void putWaitsWhileStorageIsFull() throws InterruptedException {
        Warehouse warehouse = new Warehouse(1);
        Order first = new Order(1);
        Order second = new Order(2);
        warehouse.put(first);

        AtomicBoolean secondStored = new AtomicBoolean(false);
        Thread producer = new Thread(() -> {
            try {
                warehouse.put(second);
                secondStored.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        producer.start();

        Thread.sleep(100);
        assertFalse(secondStored.get());

        warehouse.takeBatch(1);
        producer.join(1000);

        assertTrue(secondStored.get());
        List<Order> secondBatch = warehouse.takeBatch(1);
        assertEquals(1, secondBatch.size());
        assertEquals(OrderState.STORED, secondBatch.get(0).getState());
    }
}
