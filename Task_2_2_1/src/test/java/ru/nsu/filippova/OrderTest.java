package ru.nsu.filippova;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import ru.nsu.filippova.model.Order;
import ru.nsu.filippova.model.OrderState;

class OrderTest {
    @Test
    void setStateLogsOrderProgress() {
        PrintStream original = System.out;
        ByteArrayOutputStream log = new ByteArrayOutputStream();

        Order order = new Order(77);

        System.setOut(new PrintStream(log, true, StandardCharsets.UTF_8));
        try {
            order.setState(OrderState.RECEIVED);
            order.setState(OrderState.BAKING);
            order.setState(OrderState.DELIVERED);
        } finally {
            System.setOut(original);
        }

        String output = log.toString(StandardCharsets.UTF_8);
        assertEquals(
                "77 RECEIVED" + System.lineSeparator()
                        + "77 BAKING" + System.lineSeparator()
                        + "77 DELIVERED" + System.lineSeparator(),
                output
        );
        assertEquals(OrderState.DELIVERED, order.getState());
    }
}
