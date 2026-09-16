package co.edu.poli.allten.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class NumberSetGeneratorTest {
    @Test
    public void generatedSetAlwaysHasFourNumbersAndReachesEveryTarget() {
        List<Integer> numbers = NumberSetGenerator.generate();

        assertEquals(4, numbers.size());
        assertTrue(numbers.stream().allMatch(number -> number >= 1 && number <= 9));
        assertTrue(NumberSetGenerator.canReachAllTargets(numbers));
    }

    @Test
    public void repeatedNumbersAreAllowedByTheSolver() {
        assertTrue(NumberSetGenerator.canReachAllTargets(List.of(1, 2, 3, 4)));
    }
}
