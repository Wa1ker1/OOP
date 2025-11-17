package ru.nsu.filippova;

import java.util.Map;

/**
 * Представляет операцию вычитания одного выражения из другого.
 */
public class Sub extends BinaryOperation {
    /**
     * Создает операцию вычитания.
     *
     * @param left  левый операнд
     * @param right правый операнд
     */
    public Sub(Expression left, Expression right) {
        super(left, right);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String symbol() {
        return "-";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression derivative(String variable) {
        return new Sub(left.derivative(variable), right.derivative(variable));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int eval(Map<String, Integer> variables) {
        return left.eval(variables) - right.eval(variables);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Expression simplify() {
        Expression l = left.simplify();
        Expression r = right.simplify();
        if (l instanceof Number && r instanceof Number) {
            return new Number(((Number) l).getValue() - ((Number) r).getValue());
        }
        if (l.print().equals(r.print())) {
            return new Number(0);
        }
        return new Sub(l, r);
    }
}
