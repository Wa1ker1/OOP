package ru.nsu.filippova;

public abstract class BinaryOperation extends Expression {
    protected final Expression left, right;

    public BinaryOperation(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    protected abstract String symbol();

    @Override
    public String print() {
        return "(" + left.print() + symbol() + right.print() + ")";
    }
}
