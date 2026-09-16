package co.edu.poli.allten.model;

import java.util.ArrayList;
import java.util.List;

public class GameSession {
    private final Player player;
    private final List<Round> rounds;
    private int currentRoundIndex;
    private long totalTime;
    private long startedAt;
    private GameStatus status;

    public GameSession(Player player) {
        this.player = player;
        this.rounds = new ArrayList<>();
        this.currentRoundIndex = 0;
        this.totalTime = 0L;
        this.startedAt = 0L;
        this.status = GameStatus.IN_PROGRESS;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public int getCurrentRoundIndex() {
        return currentRoundIndex;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public void startSession() {
        rounds.clear();
        currentRoundIndex = 0;
        totalTime = 0L;
        startedAt = System.currentTimeMillis();
        status = GameStatus.IN_PROGRESS;
    }

    public void addRound(Round round) {
        rounds.add(round);
    }

    public void nextRound() {
        if (currentRoundIndex < rounds.size() - 1) {
            currentRoundIndex++;
        } else {
            status = GameStatus.COMPLETED;
        }
    }

    public Round getCurrentRound() {
        if (rounds.isEmpty()) {
            return null;
        }
        return rounds.get(currentRoundIndex);
    }

    public Round getRoundByTarget(int targetNumber) {
        for (Round round : rounds) {
            if (round.getRoundNumber() == targetNumber) {
                return round;
            }
        }
        return null;
    }

    public boolean selectRound(int targetNumber) {
        Round round = getRoundByTarget(targetNumber);
        if (round == null || round.isSolved()) {
            return false;
        }

        currentRoundIndex = rounds.indexOf(round);
        return true;
    }

    public boolean allRoundsSolved() {
        for (Round round : rounds) {
            if (!round.isSolved()) {
                return false;
            }
        }
        return !rounds.isEmpty();
    }

    public boolean isCompleted() {
        return status == GameStatus.COMPLETED;
    }

    public long getElapsedTimeMillis() {
        if (status == GameStatus.COMPLETED) {
            return totalTime;
        }
        return startedAt == 0L ? 0L : System.currentTimeMillis() - startedAt;
    }

    public void finishSession() {
        totalTime = startedAt == 0L ? 0L : System.currentTimeMillis() - startedAt;
        status = GameStatus.COMPLETED;
    }

    public void restartSession() {
        startSession();
    }
}
