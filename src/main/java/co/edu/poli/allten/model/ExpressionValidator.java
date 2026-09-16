package co.edu.poli.allten.model;

import java.util.ArrayList;
import java.util.List;

final class ExpressionValidator {
    private final String expression;
    private final List<Integer> availableNumbers;
    private final List<Integer> usedNumbers;
    private int position;

    private ExpressionValidator(String expression, List<Integer> availableNumbers) {
        this.expression = expression.replace('×', '*').replace('÷', '/').replace('−', '-');
        this.availableNumbers = availableNumbers;
        this.usedNumbers = new ArrayList<>();
        this.position = 0;
    }

    static boolean isValid(String expression, int targetNumber, List<Integer> availableNumbers) {
        return validate(expression, targetNumber, availableNumbers) == ExpressionValidationResult.VALID;
    }

    static Double calculateResult(String expression, List<Integer> availableNumbers) {
        if (expression == null || expression.isBlank() || availableNumbers == null) {
            return null;
        }

        try {
            ExpressionValidator validator = new ExpressionValidator(expression, availableNumbers);
            double result = validator.parseExpression();
            validator.skipSpaces();
            if (validator.position != validator.expression.length()
                    || validator.usedNumbers.size() != availableNumbers.size()
                    || !validator.haveSameNumbers()) {
                return null;
            }
            return result;
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    static ExpressionValidationResult validate(String expression, int targetNumber,
            List<Integer> availableNumbers) {
        if (expression == null || expression.isBlank() || availableNumbers == null || availableNumbers.size() != 4) {
            return ExpressionValidationResult.INVALID_OPERATION;
        }

        try {
            ExpressionValidator validator = new ExpressionValidator(expression, availableNumbers);
            double result = validator.parseExpression();
            validator.skipSpaces();

            if (validator.position != validator.expression.length()) {
                return ExpressionValidationResult.INVALID_OPERATION;
            }
            if (validator.usedNumbers.size() != availableNumbers.size() || !validator.haveSameNumbers()) {
                return ExpressionValidationResult.INVALID_NUMBERS;
            }
            if (Math.abs(result - targetNumber) >= 0.000001) {
                return ExpressionValidationResult.WRONG_TARGET;
            }
            return ExpressionValidationResult.VALID;
        } catch (NumberNotAvailableException exception) {
            return ExpressionValidationResult.INVALID_NUMBERS;
        } catch (IllegalArgumentException exception) {
            return ExpressionValidationResult.INVALID_OPERATION;
        }
    }

    private double parseExpression() {
        double result = parseTerm();
        while (true) {
            skipSpaces();
            if (match('+')) {
                result += parseTerm();
            } else if (match('-')) {
                result -= parseTerm();
            } else {
                return result;
            }
        }
    }

    private double parseTerm() {
        double result = parseFactor();
        while (true) {
            skipSpaces();
            if (match('*')) {
                result *= parseFactor();
            } else if (match('/')) {
                double divisor = parseFactor();
                if (Math.abs(divisor) < 0.000001) {
                    throw new IllegalArgumentException("No se puede dividir entre cero.");
                }
                result /= divisor;
            } else {
                return result;
            }
        }
    }

    private double parseFactor() {
        skipSpaces();
        if (match('(')) {
            double result = parseExpression();
            if (!match(')')) {
                throw new IllegalArgumentException("Paréntesis incompletos.");
            }
            return result;
        }

        if (match('-')) {
            return -parseFactor();
        }

        return parseNumber();
    }

    private double parseNumber() {
        skipSpaces();
        int start = position;
        while (position < expression.length() && Character.isDigit(expression.charAt(position))) {
            position++;
        }
        if (start == position) {
            throw new IllegalArgumentException("Se esperaba un número.");
        }

        int number;
        try {
            number = Integer.parseInt(expression.substring(start, position));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Número inválido.", exception);
        }

        if (!availableNumbers.contains(number)
                || count(usedNumbers, number) >= count(availableNumbers, number)) {
            throw new NumberNotAvailableException();
        }
        usedNumbers.add(number);
        return number;
    }

    private boolean haveSameNumbers() {
        for (Integer number : availableNumbers) {
            if (count(usedNumbers, number) != count(availableNumbers, number)) {
                return false;
            }
        }
        return true;
    }

    private int count(List<Integer> numbers, int value) {
        int occurrences = 0;
        for (Integer number : numbers) {
            if (number == value) {
                occurrences++;
            }
        }
        return occurrences;
    }

    private boolean match(char expected) {
        skipSpaces();
        if (position < expression.length() && expression.charAt(position) == expected) {
            position++;
            return true;
        }
        return false;
    }

    private void skipSpaces() {
        while (position < expression.length() && Character.isWhitespace(expression.charAt(position))) {
            position++;
        }
    }

    private static final class NumberNotAvailableException extends IllegalArgumentException {
        private static final long serialVersionUID = 1L;
    }
}
