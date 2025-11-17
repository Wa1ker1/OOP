package ru.nsu.filippova;

/**
 * Базовый класс для бинарных операций над выражениями.
 */
public abstract class BinaryOperation extends Expression {
    protected final Expression left;
    protected final Expression right;

    /**
     * Создает бинарную операцию с указанными операндами.
     *
     * @param left  левый операнд
     * @param right правый операнд
     */
    public BinaryOperation(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Возвращает символ, который используется в текстовом представлении операции.
     *
     * @return символьное представление операции
     */
    protected abstract String symbol();

    /**
     * {@inheritDoc}
     */
    @Override
    public String print() {
        return "(" + left.print() + symbol() + right.print() + ")";
    }
}
