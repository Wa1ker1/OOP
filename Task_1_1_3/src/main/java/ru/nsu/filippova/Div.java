package ru.nsu.filippova;

import java.util.Map;

/**
 * Представляет операцию деления одного выражения на другое.
 */
public class Div extends BinaryOperation {
    /**
     * Создает операцию деления.
     *
     * @param left  левый операнд
     * @param right правый операнд
     */
    public Div(Expression left, Expression right) {
        super(left, right);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String symbol() {
        return "/";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression derivative(String variable) {
        // (u/v)' = (u'*v - u*v') / (v*v)
        return new Div(
                new Sub(new Mul(left.derivative(variable), right),
                        new Mul(left, right.derivative(variable))),
                new Mul(right, right));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int eval(Map<String, Integer> variables) {
        return left.eval(variables) / right.eval(variables);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression simplify() {
        Expression l = left.simplify();
        Expression r = right.simplify();
        if (l instanceof Number && r instanceof Number) {
            return new Number(((Number) l).getValue() / ((Number) r).getValue());
        }
        return new Div(l, r);
    }
}
