package ru.nsu.filippova;

/**
 * Заказ на пиццу.
 *
 * <p>
 * Для каждого сменного этапа печати/хранения/доставки заказу присваивается новое состояние.
 * Каждое изменение состояния логируется в стандартный вывод в формате:
 * {@code [номер заказа] [состояние]}.
 */
public class Order {
    private final int id;
    private OrderState state;

    /**
     * Создает новый заказ с заданным идентификатором.
     *
     * @param id уникальный номер заказа
     */
    public Order(int id) {
        this.id = id;
    }

    /**
     * Возвращает идентификатор заказа.
     *
     * @return номер заказа
     */
    public int getId() {
        return id;
    }

    /**
     * Устанавливает состояние заказа и печатает изменение на stdout.
     *
     * @param state новое состояние заказа
     */
    public synchronized void setState(OrderState state) {
        this.state = state;
        logState();
    }

    /**
     * Возвращает текущее состояние заказа.
     *
     * @return текущее состояние
     */
    public synchronized OrderState getState() {
        return state;
    }

    private void logState() {
        System.out.println(id + " " + state);
    }
}
