package ru.nsu.filippova;

import java.util.Map;

public class Variable extends Expression {
    private final String name;

    /**
     * Создает переменную с указанным именем.
     *
     * @param name имя переменной
     */
    public Variable(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String print() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression derivative(String variable) {
        return new Number(name.equals(variable) ? 1 : 0);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException если значение переменной не задано
     */
    @Override
    public int eval(Map<String, Integer> variables) {
        if (!variables.containsKey(name))
            throw new IllegalArgumentException("Не задано значение переменной " + name);
        return variables.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression simplify() {
        return this;
    }
}
