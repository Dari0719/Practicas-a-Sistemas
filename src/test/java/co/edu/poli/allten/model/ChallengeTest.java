package co.edu.poli.allten.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class ChallengeTest {
    @Test
    public void acceptsExpressionThatUsesFourNumbersAndReachesTarget() {
        Challenge challenge = new Challenge(1, Arrays.asList(3, 3, 3, 1));

        assertTrue(challenge.validateExpression("((3 - 3) * 3) + 1"));
    }

    @Test
    public void rejectsExpressionThatDoesNotUseAllNumbers() {
        Challenge challenge = new Challenge(1, Arrays.asList(3, 3, 3, 1));

        assertFalse(challenge.validateExpression("3 + 1"));
    }

    @Test
    public void rejectsExpressionWithWrongTarget() {
        Challenge challenge = new Challenge(1, Arrays.asList(3, 3, 3, 1));

        assertFalse(challenge.validateExpression("3 + 3 + 3 + 1"));
    }

    @Test
    public void rejectsDivisionByZero() {
        Challenge challenge = new Challenge(1, Arrays.asList(3, 3, 3, 1));

        assertFalse(challenge.validateExpression("3 / (3 - 3) + 1"));
    }

    @Test
    public void guaranteedNumbersCanReachEveryTarget() {
        int[] targets = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        String[] expressions = {
            "((2 / (4 - 3)) - 1)",
            "(1 / ((4 - 3) / 2))",
            "(1 + (2 / (4 - 3)))",
            "((4 / 3) * (1 + 2))",
            "(((3 * 4) / 2) - 1)",
            "(2 / ((4 / 3) - 1))",
            "(1 + ((3 * 4) / 2))",
            "(2 / (1 - (3 / 4)))",
            "(((3 * 4) - 2) - 1)",
            "(((3 * 4) - 2) / 1)"
        };

        for (int index = 0; index < targets.length; index++) {
            Challenge challenge = new Challenge(targets[index], Arrays.asList(1, 2, 3, 4));
            assertTrue("No se pudo resolver el objetivo " + targets[index],
                    challenge.validateExpression(expressions[index]));
        }
    }
}
