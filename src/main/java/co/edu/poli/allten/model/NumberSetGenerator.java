package co.edu.poli.allten.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

final class NumberSetGenerator {
    private static final int MAX_ATTEMPTS = 10_000;
    private static final Random RANDOM = new Random();

    private NumberSetGenerator() {
    }

    static List<Integer> generate() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            List<Integer> numbers = new ArrayList<>();
            for (int index = 0; index < 4; index++) {
                numbers.add(RANDOM.nextInt(9) + 1);
            }
            if (canReachAllTargets(numbers)) {
                return numbers;
            }
        }

        return List.of(1, 2, 3, 4);
    }

    static boolean canReachAllTargets(List<Integer> numbers) {
        Set<Double> results = new HashSet<>();
        collectResults(toValues(numbers), results);
        for (int target = 1; target <= 10; target++) {
            final int expectedTarget = target;
            boolean reachable = results.stream()
                .anyMatch(result -> Math.abs(result - expectedTarget) < 0.000001);
            if (!reachable) {
                return false;
            }
        }
        return true;
    }

    private static List<Value> toValues(List<Integer> numbers) {
        List<Value> values = new ArrayList<>();
        for (Integer number : numbers) {
            values.add(new Value(number.doubleValue()));
        }
        return values;
    }

    private static void collectResults(List<Value> values, Set<Double> results) {
        if (values.size() == 1) {
            double result = values.get(0).value;
            if (Double.isFinite(result) && Math.abs(result) < 1_000_000) {
                results.add(result);
            }
            return;
        }

        for (int first = 0; first < values.size(); first++) {
            for (int second = first + 1; second < values.size(); second++) {
                Value left = values.get(first);
                Value right = values.get(second);
                List<Value> remaining = new ArrayList<>();
                for (int index = 0; index < values.size(); index++) {
                    if (index != first && index != second) {
                        remaining.add(values.get(index));
                    }
                }

                addAndCollect(remaining, left.value + right.value, results);
                addAndCollect(remaining, left.value - right.value, results);
                addAndCollect(remaining, right.value - left.value, results);
                addAndCollect(remaining, left.value * right.value, results);
                if (Math.abs(right.value) > 0.000001) {
                    addAndCollect(remaining, left.value / right.value, results);
                }
                if (Math.abs(left.value) > 0.000001) {
                    addAndCollect(remaining, right.value / left.value, results);
                }
            }
        }
    }

    private static void addAndCollect(List<Value> remaining, double result, Set<Double> results) {
        if (!Double.isFinite(result) || Math.abs(result) >= 1_000_000) {
            return;
        }
        List<Value> next = new ArrayList<>(remaining);
        next.add(new Value(result));
        collectResults(next, results);
    }

    private record Value(double value) {
    }
}
