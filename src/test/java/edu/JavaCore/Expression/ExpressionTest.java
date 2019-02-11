package edu.JavaCore.Expression;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class ExpressionTest {
    private Expression expression;

    public ExpressionTest() {
        System.out.println("Constructing...");
        expression = new Expression();
    }

    private Object[] parametersForPositiveTests() {
        System.out.println("Getting positive test params...");
        return new Object[] {
                new Object[] { "1 + 2", 3.0 },
                new Object[] { "7 - 3", 4.0 },
                new Object[] { "5 * 3", 15.0 },
                new Object[] { "6 / 2", 3.0 },
                new Object[] { "-6 + 3 * 8 / (-16) * (-5 + 4 * (-3)) + 6 * (2 / (-6 - (-8))) - 8 + 5 * 2 / 4 * 6 + 1", 33.5 }
        };
    }
    @Test
    @Parameters
    public void positiveTests(String input, Double expectedOutput) throws Expression.ExpressionValidationException {
        System.out.println("In the positive test: " + input + "; " + expectedOutput);
        assertEquals(expression.evaluate(input), expectedOutput, 0.0);
    }

    private Object[] parametersForNegativeTests() {
        System.out.println("Getting negative test params...");
        return new Object[] {
                new Object[] { "(1" },
                new Object[] { "1)" },
                new Object[] { "((1)" },
                new Object[] { "(1))" },
                new Object[] { "(1 + 2 + (3 + 4)" },
                new Object[] { "2 + a + 3" },
                new Object[] { "1 & 5" },
                new Object[] { "3 + () + 8" },
                new Object[] { "6 ++ 2" },
                new Object[] { "5 + (* 8)" },
                new Object[] { "3 + (8 -)" },
                new Object[] { "/ 3 + 8" },
                new Object[] { "5 +" },
                new Object[] { "(3 + 2) (8 + 6)" },
                new Object[] { "3 (8 + 10)" },
                new Object[] { "(1 + 2) 5" }
        };
    }
    @Test(expected = Expression.ExpressionValidationException.class)
    @Parameters
    public void negativeTests(String input) throws Expression.ExpressionValidationException {
        System.out.println("In the negative test: " + input);
        expression.evaluate(input);
    }
}
