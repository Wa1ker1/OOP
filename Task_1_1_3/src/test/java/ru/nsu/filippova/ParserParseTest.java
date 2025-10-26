package ru.nsu.filippova;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class ParserParseTest {

    @Test
    void parseTripleNestedVariable() {
        Expression expression = Expression.parse("(((x)))");
        assertInstanceOf(Variable.class, expression);
        assertEquals("x", expression.print());
    }

    @Test
    void parseSimpleAddWithNestedParentheses() {
        Expression expression = Expression.parse("((x)+(y))");
        Expression expected = new Add(new Variable("x"), new Variable("y"));
        assertEquals(expected.print(), expression.print());
    }

    @Test
    void parseAddWithMultiplication() {
        Expression expression = Expression.parse("((a)+((b)*(c)))");
        Expression expected = new Add(new Variable("a"),
                new Mul(new Variable("b"), new Variable("c")));
        assertEquals(expected.print(), expression.print());
    }

    @Test
    void parseMultiLetterVariables() {
        Expression expression = Expression.parse("(alpha+beta)");
        Expression expected = new Add(new Variable("alpha"), new Variable("beta"));
        assertEquals(expected.print(), expression.print());
    }

    @Test
    void parseMultiLetterVariablesWithMultiplication() {
        Expression expression = Expression.parse("(veryLongVariableName * short)");
        Expression expected = new Mul(new Variable("veryLongVariableName"), new Variable("short"));
        assertEquals(expected.print(), expression.print());
    }

    @Test
    void parseExpressionWithSpaces() {
        assertEquals("(x+y)", Expression.parse("( x + y )").print());
        assertEquals("(x+y)", Expression.parse("(x+ y)").print());
        assertEquals("(x+y)", Expression.parse("(x +y)").print());
    }

    @Test
    void parseExpressionWithNegativeNumbers() {
        Expression negative = Expression.parse("(-5)");
        assertEquals("-5", negative.print());

        Expression mixed = Expression.parse("(x + (-3))");
        Expression expected = new Add(new Variable("x"), new Number(-3));
        assertEquals(expected.print(), mixed.print());
    }

    @Test
    void parseComplexNestedExpression() {
        Expression expression = Expression.parse("((a*b)+((c-d)/(1+2)))");
        Expression expected = new Add(
                new Mul(new Variable("a"), new Variable("b")),
                new Div(
                        new Sub(new Variable("c"), new Variable("d")),
                        new Add(new Number(1), new Number(2))));
        assertEquals(expected.print(), expression.print());
    }

    @Test
    void readExpressionFromReader() throws IOException {
        Parser parser = new Parser(new StringReader(" ( x + y ) \n"));
        Expression expression = parser.readExpression();
        assertEquals("(x+y)", expression.print());
    }

    @Test
    void rejectUnbalancedParentheses() {
        assertThrows(IllegalArgumentException.class, () -> Expression.parse("(x+y"));
        assertThrows(IllegalArgumentException.class, () -> Expression.parse("x+y)"));
    }

    @Test
    void rejectInvalidCharacters() {
        assertThrows(IllegalArgumentException.class, () -> Expression.parse("(x # y)"));
    }

    @Test
    void rejectInvalidSyntax() {
        assertThrows(IllegalArgumentException.class, () -> Expression.parse("()"));
        assertThrows(IllegalArgumentException.class, () -> Expression.parse("(+)"));
        assertThrows(IllegalArgumentException.class, () -> Expression.parse("(x++)"));
    }
}
