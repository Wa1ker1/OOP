package ru.nsu.filippova.model;

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
