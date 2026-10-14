package co.edu.poli.allten.model;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class GameModel {

	public enum Mode { NORMAL, HARDCORE }

	public static final int TOTAL_OBJECTIVES = 10;

	private boolean gameStarted;
	private String nickname = "";
	private Mode mode = Mode.NORMAL;
	private final Set<Integer> completedObjectives = new TreeSet<>();
	private Instant startTime;
	private Instant endTime;

	public void startGame(String nickname, Mode mode) {
		this.nickname = nickname;
		this.mode = mode;
		this.completedObjectives.clear();
		this.startTime = Instant.now();
		this.endTime = null;
		this.gameStarted = true;
	}

	/** Marca un objetivo (1 a 10) como logrado. Devuelve true si era nuevo. */
	public boolean completeObjective(int objective) {
		if (!gameStarted || endTime != null
				|| objective < 1 || objective > TOTAL_OBJECTIVES) {
			return false;
		}
		boolean isNew = completedObjectives.add(objective);
		if (isFinished()) {
			endTime = Instant.now(); // el reloj se detiene al lograr los 10
		}
		return isNew;
	}

	public boolean isFinished() {
		return completedObjectives.size() == TOTAL_OBJECTIVES;
	}

	public Set<Integer> getCompletedObjectives() {
		return Collections.unmodifiableSet(completedObjectives);
	}

	public int getCompletedCount() {
		return completedObjectives.size();
	}

	public long getElapsedSeconds() {
		if (startTime == null) {
			return 0;
		}
		Instant end = (endTime != null) ? endTime : Instant.now();
		return Duration.between(startTime, end).getSeconds();
	}

	public boolean isGameStarted() {
		return gameStarted;
	}

	public String getNickname() {
		return nickname;
	}

	public Mode getMode() {
		return mode;
	}
}