package ru.nsu.filippova;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExpressionEvaluationTest {

    @Test
    void evaluateExpressionWithAssignments() {
        Expression expression = new Add(
                new Mul(new Variable("x"), new Variable("y")),
                new Div(new Variable("z"), new Number(2))
        );

        assertEquals(17, expression.eval("x=3; y=4; z=10"));
        assertEquals(4, expression.eval("x=0; y=100; z=8"));
        assertEquals(17, expression.eval("z=10; x=3; y=4"));
        assertEquals(17, expression.eval("x=3; y=4; z=10; w=100"));
        assertThrows(IllegalArgumentException.class, () -> expression.eval("x=3; y=4"));
    }

    @Test
    void evaluateExpressionWithVariableSpacing() {
        Expression expression = new Add(new Variable("x"), new Variable("y"));
        assertEquals(7, expression.eval(" x = 3 ; y = 4 ; ; "));
    }

    @Test
    void evaluateNumberWithEmptyAssignment() {
        assertEquals(5, new Number(5).eval(""));
        assertEquals(5, new Add(new Number(2), new Number(3)).eval(""));
    }

    @Test
    void rejectAssignmentsWithEmptyValues() {
        Expression expression = new Add(new Variable("x"), new Variable("y"));
        assertThrows(NumberFormatException.class, () -> expression.eval("x=; y=5"));
    }

    @Test
    void rejectAssignmentsWithNonNumericValues() {
        Expression expression = new Add(new Variable("x"), new Number(1));
        assertThrows(NumberFormatException.class, () -> expression.eval("x=abc"));
    }

    @Test
    void rejectAssignmentsWithoutSeparators() {
        Expression expression = new Add(new Variable("x"), new Variable("y"));
        assertThrows(NumberFormatException.class, () -> expression.eval("x=5 y=3"));
    }

    @Test
    void rejectAssignmentsWithIncorrectDelimiter() {
        Expression expression = new Add(new Variable("x"), new Variable("y"));
        assertThrows(NumberFormatException.class, () -> expression.eval("x=3, y=4"));
    }
}
