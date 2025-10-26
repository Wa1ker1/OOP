package ru.nsu.filippova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

class ExpressionSimplifyTest {

    @Test
    void simplifyZeroAndUnitMultiplication() {
        Expression zero = new Mul(new Number(0), new Variable("x"));
        Expression one = new Mul(new Number(1), new Variable("x"));
        assertEquals("0", zero.simplify().print());
        assertEquals("x", one.simplify().print());
    }

    @Test
    void simplifyAdditionAndSubtractionOfNumbers() {
        Expression add = new Add(new Number(5), new Number(3));
        Expression sub = new Sub(new Number(10), new Number(4));
        assertEquals("8", add.simplify().print());
        assertEquals("6", sub.simplify().print());
    }

    @Test
    void simplifySubtractionOfSameVariable() {
        Expression expression = new Sub(new Variable("x"), new Variable("x"));
        assertEquals("0", expression.simplify().print());
    }

    @Test
    void simplifyDivisionOfNumbers() {
        Expression expression = new Div(new Number(8), new Number(2));
        Expression simplified = expression.simplify();
        assertInstanceOf(Number.class, simplified);
        assertEquals("4", simplified.print());
    }
}
