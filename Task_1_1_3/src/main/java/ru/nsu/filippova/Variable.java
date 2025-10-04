package ru.nsu.filippova;

import java.util.Map;

public class Variable extends Expression {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public String print() {
        return name;
    }

    @Override
    public Expression derivative(String variable) {
        return new Number(name.equals(variable) ? 1 : 0);
    }

    @Override
    public int eval(Map<String, Integer> variables) {
        if (!variables.containsKey(name))
            throw new IllegalArgumentException("Не задано значение переменной " + name);
        return variables.get(name);
    }

    @Override
    public Expression simplify() {
        return this;
    }
}
