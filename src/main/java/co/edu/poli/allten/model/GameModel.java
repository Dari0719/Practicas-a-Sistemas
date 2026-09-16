package co.edu.poli.allten.model;

import java.util.ArrayList;
import java.util.List;

public class GameModel {
    private boolean gameStarted;
    private GameSession session;
    private final Ranking ranking;
    private int lastSolvedTarget;
    private Double lastCalculatedResult;

    public GameModel() {
        this.gameStarted = false;
        this.session = null;
        this.ranking = new Ranking();
        this.lastSolvedTarget = 0;
        this.lastCalculatedResult = null;
    }

    public void startGame(String nickname) {
        Player player = new Player(nickname);
        session = new GameSession(player);
        session.startSession();
        generateRounds();
        gameStarted = true;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public GameSession getSession() {
        return session;
    }

    public Ranking getRanking() {
        return ranking;
    }

    public Round getCurrentRound() {
        return session == null ? null : session.getCurrentRound();
    }

    public boolean selectTarget(int targetNumber) {
        return session != null && session.selectRound(targetNumber);
    }

    public int getLastSolvedTarget() {
        return lastSolvedTarget;
    }

    public Double getLastCalculatedResult() {
        return lastCalculatedResult;
    }

    public ExpressionValidationResult processAnswer(String answer) {
        lastCalculatedResult = null;
        if (session == null || session.getRounds().isEmpty()) {
            return ExpressionValidationResult.INVALID_OPERATION;
        }

        ExpressionValidationResult invalidResult = ExpressionValidationResult.WRONG_TARGET;
        for (Round round : session.getRounds()) {
            if (round.isSolved()) {
                continue;
            }

            ExpressionValidationResult result = round.getChallenge().validateExpressionResult(answer);
            if (result == ExpressionValidationResult.VALID) {
                round.solve(answer);
                round.setTimeSpent(0L);
                lastSolvedTarget = round.getRoundNumber();
                completeSessionIfNecessary();
                return ExpressionValidationResult.VALID;
            }
            if (result == ExpressionValidationResult.INVALID_NUMBERS) {
                invalidResult = result;
            } else if (result == ExpressionValidationResult.INVALID_OPERATION
                    && invalidResult == ExpressionValidationResult.WRONG_TARGET) {
                invalidResult = result;
            } else if (result == ExpressionValidationResult.WRONG_TARGET && lastCalculatedResult == null) {
                lastCalculatedResult = round.getChallenge().calculateResult(answer);
            }
        }

        return invalidResult;
    }

    private void completeSessionIfNecessary() {
        if (session.allRoundsSolved()) {
            session.setStatus(GameStatus.COMPLETED);
            session.finishSession();
            session.getPlayer().updateBestTime(session.getTotalTime());
            ranking.addPlayer(session.getPlayer());
            gameStarted = false;
        }
    }

    public void restartGame() {
        if (session != null) {
            session.restartSession();
        }
        gameStarted = true;
    }

    private void generateRounds() {
        if (session == null) {
            return;
        }

        List<Integer> sharedNumbers = NumberSetGenerator.generate();

        for (int i = 1; i <= 10; i++) {
            Challenge challenge = new Challenge(i, sharedNumbers);
            session.addRound(new Round(i, challenge));
        }
    }
}
