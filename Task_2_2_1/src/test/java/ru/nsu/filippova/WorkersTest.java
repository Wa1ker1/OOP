package ru.nsu.filippova;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import ru.nsu.filippova.model.Order;
import ru.nsu.filippova.model.OrderState;
import ru.nsu.filippova.simulation.OrderQueue;
import ru.nsu.filippova.simulation.Warehouse;
import ru.nsu.filippova.worker.BakerWorker;
import ru.nsu.filippova.worker.CourierWorker;

class WorkersTest {
    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void bakerTransfersBakedOrderToWarehouse() throws InterruptedException {
        OrderQueue orderQueue = new OrderQueue();
        Warehouse warehouse = new Warehouse(2);
        Order order = new Order(1);

        orderQueue.offer(order);
        orderQueue.close();

        Thread baker = new BakerWorker(1, 20, orderQueue, warehouse);
        baker.start();
        baker.join();

        List<Order> batch = warehouse.takeBatch(1);
        assertEquals(1, batch.size());
        assertEquals(OrderState.STORED, batch.get(0).getState());
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void courierDeliversBatchRespectingCapacity() throws InterruptedException {
        Warehouse warehouse = new Warehouse(10);
        Order first = new Order(1);
        Order second = new Order(2);
        Order third = new Order(3);
        warehouse.put(first);
        warehouse.put(second);
        warehouse.put(third);

        warehouse.closeForDelivery();

        Thread courier = new CourierWorker(1, 2, 10, warehouse);
        courier.start();
        courier.join();
        assertEquals(OrderState.DELIVERED, first.getState());
        assertEquals(OrderState.DELIVERED, second.getState());
        assertEquals(OrderState.DELIVERED, third.getState());
    }
}
