package co.edu.poli.allten.model;

public class Player {
    private final String nickname;
    private long bestTime;
    private int attempts;

    public Player(String nickname) {
        this.nickname = nickname;
        this.bestTime = 0L;
        this.attempts = 0;
    }

    public String getNickname() {
        return nickname;
    }

    public long getBestTime() {
        return bestTime;
    }

    public void updateBestTime(long time) {
        if (bestTime == 0L || time < bestTime) {
            bestTime = time;
        }
    }

    public int getAttempts() {
        return attempts;
    }

    public void incrementAttempts() {
        attempts++;
    }
}
