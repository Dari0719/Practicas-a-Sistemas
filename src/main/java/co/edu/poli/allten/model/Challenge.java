package co.edu.poli.allten.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Challenge {
    private final int targetNumber;
    private final List<Integer> availableNumbers;
    private String expression;

    public Challenge(int targetNumber, List<Integer> availableNumbers) {
        this.targetNumber = targetNumber;
        this.availableNumbers = new ArrayList<>(availableNumbers);
        this.expression = "";
    }

    public int getTargetNumber() {
        return targetNumber;
    }

    public List<Integer> getAvailableNumbers() {
        return Collections.unmodifiableList(availableNumbers);
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public boolean validateExpression(String expressionToValidate) {
        return validateExpressionResult(expressionToValidate) == ExpressionValidationResult.VALID;
    }

    public ExpressionValidationResult validateExpressionResult(String expressionToValidate) {
        return ExpressionValidator.validate(expressionToValidate, targetNumber, availableNumbers);
    }

    public Double calculateResult(String expressionToEvaluate) {
        return ExpressionValidator.calculateResult(expressionToEvaluate, availableNumbers);
    }
}
