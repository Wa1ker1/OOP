package ru.nsu.filippova;

import java.util.Map;

/**
 * Представляет числовую константу в выражении.
 */
public class Number extends Expression {
    private final int value;

    /**
     * Создает числовую константу.
     *
     * @param value значение константы
     */
    public Number(int value) {
        this.value = value;
    }

    /**
     * Возвращает значение числовой константы.
     *
     * @return величина константы
     */
    public int getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String print() {
        return String.valueOf(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression derivative(String variable) {
        return new Number(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int eval(Map<String, Integer> variables) {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression simplify() {
        return this;
    }
}
