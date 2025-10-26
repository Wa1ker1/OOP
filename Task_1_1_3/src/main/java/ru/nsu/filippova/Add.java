package ru.nsu.filippova;

import java.util.Map;

/**
 * Представляет операцию сложения двух подвыражений.
 */
public class Add extends BinaryOperation {
    /**
     * Создает операцию сложения.
     *
     * @param left  левый операнд
     * @param right правый операнд
     */
    public Add(Expression left, Expression right) {
        super(left, right);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String symbol() {
        return "+";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression derivative(String variable) {
        return new Add(left.derivative(variable), right.derivative(variable));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int eval(Map<String, Integer> variables) {
        return left.eval(variables) + right.eval(variables);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression simplify() {
        Expression l = left.simplify();
        Expression r = right.simplify();
        if (l instanceof Number && r instanceof Number) {
            return new Number(((Number) l).getValue() + ((Number) r).getValue());
        }
        return new Add(l, r);
    }
}
