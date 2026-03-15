package ru.nsu.filippova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class OrderQueueTest {
    @Test
    void offerAndTakeOrder() throws InterruptedException {
        OrderQueue queue = new OrderQueue();
        Order input = new Order(1);

        assertTrue(queue.offer(input));
        Order output = queue.take();

        assertEquals(input, output);
        assertEquals(OrderState.QUEUED, output.getState());
    }

    @Test
    void closePreventsFurtherOrders() throws InterruptedException {
        OrderQueue queue = new OrderQueue();
        queue.close();

        assertFalse(queue.offer(new Order(2)));
        assertNull(queue.take());
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void takeWaitsUntilOrderAppears() throws InterruptedException {
        OrderQueue queue = new OrderQueue();
        AtomicReference<Order> taken = new AtomicReference<>();

        Thread consumer = new Thread(() -> {
            try {
                taken.set(queue.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();

        Thread.sleep(100);
        Order expected = new Order(3);
        assertTrue(queue.offer(expected));

        consumer.join(1000);
        assertEquals(expected, taken.get());
        assertEquals(OrderState.QUEUED, expected.getState());
    }
}
