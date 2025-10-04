package ru.nsu.filippova;

import java.util.Map;

public class Number extends Expression {
    private final int value;

    public Number(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String print() {
        return String.valueOf(value);
    }

    @Override
    public Expression derivative(String variable) {
        return new Number(0);
    }

    @Override
    public int eval(Map<String, Integer> variables) {
        return value;
    }

    @Override
    public Expression simplify() {
        return this;
    }
}
