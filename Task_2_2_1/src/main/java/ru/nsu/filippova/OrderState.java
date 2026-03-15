package ru.nsu.filippova;

/**
 * Состояния жизненного цикла заказа в симуляции пиццерии.
 */
public enum OrderState {
    RECEIVED,
    QUEUED,
    BAKING,
    WAITING_STORAGE,
    READY_FOR_STORAGE,
    STORED,
    OUT_FOR_DELIVERY,
    DELIVERED
}
