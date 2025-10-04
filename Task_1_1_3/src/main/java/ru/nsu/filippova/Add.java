package ru.nsu.filippova;

import java.util.Map;

public class Add extends BinaryOperation {
    public Add(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    protected String symbol() {
        return "+";
    }

    @Override
    public Expression derivative(String variable) {
        return new Add(left.derivative(variable), right.derivative(variable));
    }

    @Override
    public int eval(Map<String, Integer> variables) {
        return left.eval(variables) + right.eval(variables);
    }

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
