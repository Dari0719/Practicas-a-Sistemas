package co.edu.poli.allten.model;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class GameModel {

	public enum Mode { NORMAL, HARDCORE }

	public static final int TOTAL_OBJECTIVES = 10;
	public static final int NUMBER_COUNT = 4;   // cuántos números se generan por reto
	public static final int MIN_NUMBER = 1;     // rango de los números (ajústalo según las reglas)
	public static final int MAX_NUMBER = 9;
	public static final int HINT_LIMIT = 10;   // cuántas expresiones de ayuda se muestran

	private boolean gameStarted;
	private String nickname = "";
	private Mode mode = Mode.NORMAL;
	private final Set<Integer> completedObjectives = new TreeSet<>();
	private Instant startTime;
	private Instant endTime;
	private final Random random = new Random();
	private final int[] numbers = new int[NUMBER_COUNT];

	public void startGame(String nickname, Mode mode) {
		this.nickname = nickname;
		this.mode = mode;
		this.completedObjectives.clear();
		this.startTime = Instant.now();
		this.endTime = null;
		this.gameStarted = true;
		generateNumbers();
	}

	/** Genera los números del reto al azar (pueden repetirse). */
	public void generateNumbers() {
		for (int i = 0; i < numbers.length; i++) {
			numbers[i] = MIN_NUMBER + random.nextInt(MAX_NUMBER - MIN_NUMBER + 1);
		}
	}

	public int[] getNumbers() {
		return Arrays.copyOf(numbers, numbers.length);
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

	/** Expresiones posibles para lograr un objetivo con los números actuales. */
	public List<String> getHints(int objective) {
		return ExpressionSolver.solve(numbers, objective, HINT_LIMIT);
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