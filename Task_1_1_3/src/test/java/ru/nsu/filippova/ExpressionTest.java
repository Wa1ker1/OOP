package ru.nsu.filippova;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExpressionTest {

    @Test
    public void testPrintAndEval() {
        Expression e = new Add(new Number(3), new Mul(new Number(2), new Variable("x")));
        assertEquals("(3+(2*x))", e.print());
        assertEquals(23, e.eval("x = 10; y = 13"));
    }

    @Test
    public void testDerivative() {
        Expression e = new Add(new Number(3), new Mul(new Number(2), new Variable("x")));
        Expression de = e.derivative("x");
        assertEquals("(0+((0*x)+(2*1)))", de.print());
    }

    @Test
    public void testParse() {
        Expression e = Expression.parse("(3+(2*x))");
        assertEquals("(3+(2*x))", e.print());
        assertEquals(23, e.eval("x = 10"));
    }

    @Test
    public void testSimplify() {
        Expression e = new Mul(new Number(0), new Variable("x"));
        Expression simplified = e.simplify();
        assertInstanceOf(Number.class, simplified);
        assertEquals("0", simplified.print());

        Expression e2 = new Sub(new Variable("x"), new Variable("x"));
        assertEquals("0", e2.simplify().print());

        Expression e3 = new Mul(new Number(1), new Variable("y"));
        assertEquals("y", e3.simplify().print());
    }

    @Test
    public void testDivisionDerivative() {
        Expression e = new Div(new Variable("x"), new Number(2));
        Expression de = e.derivative("x");
        assertEquals("(((1*2)-(x*0))/(2*2))", de.print());
    }

    @Test
    public void testVariableEval() {
        Expression x = new Variable("abc");
        assertEquals(5, x.eval("abc=5"));
        assertThrows(IllegalArgumentException.class, () -> x.eval("x=10"));
    }
}
