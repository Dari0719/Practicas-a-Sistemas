package co.edu.poli.allten.model;

public class Round {
    private final int roundNumber;
    private final Challenge challenge;
    private boolean solved;
    private long timeSpent;
    private String answer;

    public Round(int roundNumber, Challenge challenge) {
        this.roundNumber = roundNumber;
        this.challenge = challenge;
        this.solved = false;
        this.timeSpent = 0L;
        this.answer = "";
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public boolean isSolved() {
        return solved;
    }

    public long getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(long timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getAnswer() {
        return answer;
    }

    public ExpressionValidationResult solve(String answerText) {
        this.answer = answerText;
        ExpressionValidationResult result = challenge.validateExpressionResult(answerText);
        this.solved = result == ExpressionValidationResult.VALID;
        return result;
    }
}
