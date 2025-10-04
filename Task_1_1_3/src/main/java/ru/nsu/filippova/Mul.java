package ru.nsu.filippova;

import java.util.Map;

public class Mul extends BinaryOperation {
    public Mul(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    protected String symbol() {
        return "*";
    }

    @Override
    public Expression derivative(String variable) {
        // (u*v)' = u'*v + u*v'
        return new Add(new Mul(left.derivative(variable), right),
                new Mul(left, right.derivative(variable)));
    }

    @Override
    public int eval(Map<String, Integer> variables) {
        return left.eval(variables) * right.eval(variables);
    }

    @Override
    public Expression simplify() {
        Expression l = left.simplify();
        Expression r = right.simplify();
        if (l instanceof Number && r instanceof Number) {
            return new Number(((Number) l).getValue() * ((Number) r).getValue());
        }
        if (l instanceof Number) {
            int val = ((Number) l).getValue();
            if (val == 0) return new Number(0);
            if (val == 1) return r;
        }
        if (r instanceof Number) {
            int val = ((Number) r).getValue();
            if (val == 0) return new Number(0);
            if (val == 1) return l;
        }
        return new Mul(l, r);
    }
}
