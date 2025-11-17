package ru.nsu.filippova;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ExpressionDerivativeTest {

    @Test
    void derivativeOfConstantIsZero() {
        Expression derivative = new Number(5).derivative("x");
        assertEquals("0", derivative.print());
    }

    @Test
    void derivativeOfVariableWithRespectToItselfIsOne() {
        Expression derivative = new Variable("x").derivative("x");
        assertEquals("1", derivative.print());
    }

    @Test
    void derivativeOfVariableWithRespectToOtherVariableIsZero() {
        Expression derivative = new Variable("x").derivative("y");
        assertEquals("0", derivative.print());
    }

    @Test
    void derivativeOfConstantExpressionSimplifiesToZero() {
        Expression expression = new Add(new Number(3), new Number(5));
        Expression derivative = expression.derivative("x").simplify();
        assertEquals("0", derivative.print());
    }

    @Test
    void repeatedDerivativeOfVariable() {
        Expression expression = new Variable("x");
        Expression firstDerivative = expression.derivative("x");
        assertEquals("1", firstDerivative.print());
        Expression secondDerivative = firstDerivative.derivative("x");
        assertEquals("0", secondDerivative.print());
    }

    @Test
    void derivativeOfCubicPolynomialCompositionMatchesExpected() {
        Expression inner = new Add(
                new Mul(new Variable("x"), new Variable("x")),
                new Mul(new Number(3), new Variable("x"))
        );
        Expression function = new Mul(inner, new Mul(inner, inner));
        Expression derivative = function.derivative("x");

        Expression expectedInner = new Add(
                new Mul(new Variable("x"), new Variable("x")),
                new Mul(new Number(3), new Variable("x"))
        );
        Expression expected = new Mul(
                new Number(3),
                new Mul(
                        new Mul(expectedInner, expectedInner),
                        new Add(
                                new Mul(new Number(2), new Variable("x")),
                                new Number(3)
                        )
                )
        );

        for (int value : new int[]{-2, -1, 0, 1, 2}) {
            Map<String, Integer> variables = Map.of("x", value);
            assertEquals(expected.eval(variables), derivative.eval(variables));
        }
    }
}
