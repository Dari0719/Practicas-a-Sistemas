package co.edu.poli.allten.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import co.edu.poli.allten.model.ExpressionValidationResult;
import co.edu.poli.allten.model.Player;
import co.edu.poli.allten.model.Round;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

public class GameView {
    private final Label statusLabel;
    private final Button startButton;
    private final TextField nicknameField;
    private final BorderPane homeLayout;
    private final Scene scene;
    private BorderPane gameLayout;
    private Label operationLabel;
    private GridPane objectivesGrid;
    private TextField answerField;
    private Label gameStatusLabel;
    private final List<HBox> objectiveRows = new ArrayList<>();
    private final List<Label> objectiveExpressions = new ArrayList<>();
    private final List<Button> numberKeys = new ArrayList<>();

    public GameView() {
        homeLayout = loadLayout("game-home.fxml", "No se pudo cargar la vista principal de JavaFX.");

        statusLabel = find(homeLayout, "status-label", Label.class);
        startButton = find(homeLayout, "start-button", Button.class);
        nicknameField = find(homeLayout, "nickname-field", TextField.class);

        statusLabel.setVisible(false);
        statusLabel.setManaged(false);

        scene = new Scene(homeLayout, 800, 700);
        addStylesheet("game-home.css");
    }

    public Scene createScene() {
        return scene;
    }

    public void setOnStartGame(Runnable action) {
        startButton.setOnAction(event -> action.run());
        nicknameField.setOnAction(event -> action.run());
    }

    public String getNickname() {
        return nicknameField.getText().trim();
    }

    public void showNicknameRequired() {
        statusLabel.setText("Debes escribir un nickname para continuar.");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
        nicknameField.requestFocus();
    }

    public void showGameStarted(String nickname, Consumer<String> answerAction,
            Consumer<Integer> targetAction) {
            gameLayout = loadLayout("game.fxml", "No se pudo cargar la vista del juego de JavaFX.");

            addStylesheet("game.css");
            bindGameControls(nickname, answerAction);
        createObjectiveRows(targetAction);
        scene.setRoot(gameLayout);
    }

    public void showGameStarted(String nickname) {
        showGameStarted(nickname, answer -> { }, target -> { });
    }

    public void showGameStarted(String nickname, Consumer<String> answerAction) {
        showGameStarted(nickname, answerAction, target -> { });
    }

    public void showRound(Round round) {
        if (round == null) {
            return;
        }

        operationLabel.setText("OBJETIVO " + round.getRoundNumber() + "/10");
        for (int index = 0; index < numberKeys.size()
            && index < round.getChallenge().getAvailableNumbers().size(); index++) {
            numberKeys.get(index).setText(String.valueOf(
                round.getChallenge().getAvailableNumbers().get(index)));
        }
        objectiveRows.forEach(row -> row.getStyleClass().remove("active"));
        objectiveRows.get(round.getRoundNumber() - 1).getStyleClass().add("active");
        answerField.clear();
        updateNumberKeyState();
        gameStatusLabel.setText("");
        answerField.requestFocus();
    }

    public void markRoundCompleted(int roundNumber, String expression) {
        int index = roundNumber - 1;
        if (index < 0 || index >= objectiveRows.size()) {
            return;
        }

        objectiveRows.get(index).getStyleClass().remove("active");
        objectiveRows.get(index).getStyleClass().add("completed");
        objectiveExpressions.get(index).setText(expression);
    }

    public void showAnswerRequired() {
        gameStatusLabel.setText("Escribe una respuesta para continuar.");
        answerField.requestFocus();
    }

    public void showGameCompleted(long elapsedTimeMillis) {
        showGameSummary(elapsedTimeMillis, List.of());
    }

    public void showGameSummary(long elapsedTimeMillis, List<Player> players) {
        BorderPane summaryLayout = loadLayout("game-summary.fxml", "No se pudo cargar el resumen de la partida.");

        Label timeLabel = find(summaryLayout, "summary-time-label", Label.class);
        Label rankingLabel = find(summaryLayout, "summary-ranking-label", Label.class);
        Button rankingButton = find(summaryLayout, "ranking-button", Button.class);
        Button homeButton = find(summaryLayout, "home-button", Button.class);
        timeLabel.setText("Tiempo total: " + formatTime(elapsedTimeMillis));
        rankingButton.setOnAction(event -> {
            rankingLabel.setText(formatRanking(players));
            rankingLabel.setVisible(true);
            rankingLabel.setManaged(true);
        });
        homeButton.setOnAction(event -> scene.setRoot(homeLayout));
        scene.setRoot(summaryLayout);
    }

    public void showValidationError(ExpressionValidationResult result, Double calculatedResult) {
        String message;
        switch (result) {
            case INVALID_NUMBERS:
                message = "Usa los cuatro números disponibles exactamente una vez.";
                break;
            case INVALID_OPERATION:
                message = "Operación matemática no válida. Revisa operadores y paréntesis.";
                break;
            case WRONG_TARGET:
                String resultText = calculatedResult == null ? "ese resultado" : formatResult(calculatedResult);
                message = "El resultado " + resultText + " no hace parte de las soluciones.";
                break;
            default:
                message = "La expresión no es válida.";
                break;
        }
        gameStatusLabel.setText(message);
        answerField.requestFocus();
    }

    private void createObjectiveRows(Consumer<Integer> targetAction) {
        objectiveRows.clear();
        objectiveExpressions.clear();
        objectivesGrid.getChildren().clear();

        for (int index = 0; index < 10; index++) {
            final int targetNumber = index + 1;
            Label expression = new Label();
            expression.getStyleClass().add("objective-expression");
            expression.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(expression, Priority.ALWAYS);

            Label number = new Label(String.valueOf(targetNumber));
            number.getStyleClass().add("objective-number");
            HBox row = new HBox(expression, number);
            row.getStyleClass().add("objective-row");
            row.setAlignment(Pos.CENTER_RIGHT);
            row.setPrefHeight(32);
            row.setPrefWidth(278);
            row.setOnMouseClicked(event -> targetAction.accept(targetNumber));

            objectiveRows.add(row);
            objectiveExpressions.add(expression);
            objectivesGrid.add(row, index < 5 ? 0 : 1, index % 5);
        }
    }

    private void bindGameControls(String nickname, Consumer<String> answerAction) {
        Button backButton = find(gameLayout, "back-button", Button.class);
        Label playerLabel = find(gameLayout, "player-label", Label.class);
        operationLabel = find(gameLayout, "operation-label", Label.class);
        objectivesGrid = find(gameLayout, "objectives-grid", GridPane.class);
        answerField = find(gameLayout, "answer-field", TextField.class);
        Button checkButton = find(gameLayout, "check-button", Button.class);
        Button clearButton = find(gameLayout, "clear-button", Button.class);
        Button resetButton = find(gameLayout, "reset-expression-button", Button.class);
        gameStatusLabel = find(gameLayout, "game-status-label", Label.class);

        playerLabel.setText("Jugador: " + nickname);
        numberKeys.clear();
        gameLayout.lookupAll(".number-key").forEach(node -> numberKeys.add((Button) node));

        backButton.setOnAction(event -> scene.setRoot(homeLayout));
        checkButton.setOnAction(event -> answerAction.accept(answerField.getText().trim()));
        answerField.setOnAction(event -> answerAction.accept(answerField.getText().trim()));
        answerField.addEventHandler(KeyEvent.KEY_TYPED, this::normalizeKeyboardOperator);
        answerField.textProperty().addListener((observable, oldValue, newValue) -> updateNumberKeyState());
        clearButton.setOnAction(event -> removeLastCharacter());
        resetButton.setOnAction(event -> answerField.clear());
        bindKeyButtons(".number-key");
        bindKeyButtons(".operator-key");
    }

    private void removeLastCharacter() {
        String expression = answerField.getText();
        if (!expression.isEmpty()) {
            answerField.deleteText(expression.length() - 1, expression.length());
        }
    }

    private String formatTime(long elapsedTimeMillis) {
        long totalSeconds = elapsedTimeMillis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        long milliseconds = elapsedTimeMillis % 1000;
        return String.format("%02d:%02d.%03d", minutes, seconds, milliseconds);
    }

    private String formatResult(double result) {
        if (result == Math.rint(result)) {
            return String.valueOf((long) result);
        }
        return String.valueOf(result);
    }

    private String formatRanking(List<Player> players) {
        if (players.isEmpty()) {
            return "Aún no hay resultados en el ranking.";
        }

        StringBuilder ranking = new StringBuilder("RANKING\n");
        for (int index = 0; index < players.size(); index++) {
            Player player = players.get(index);
            ranking.append(index + 1).append(". ")
                    .append(player.getNickname()).append(" - ")
                    .append(formatTime(player.getBestTime())).append("\n");
        }
        return ranking.toString();
    }

    private void bindKeyButtons(String selector) {
        gameLayout.lookupAll(selector).forEach(node -> {
            Button button = (Button) node;
            button.setOnAction(event -> answerField.appendText(button.getText()));
        });
    }

    private void updateNumberKeyState() {
        String expression = answerField.getText();
        List<String> seenKeys = new ArrayList<>();
        for (Button numberKey : numberKeys) {
            String value = numberKey.getText();
            int sameKeyIndex = 0;
            for (String seenKey : seenKeys) {
                if (seenKey.equals(value)) {
                    sameKeyIndex++;
                }
            }
            seenKeys.add(value);

            int occurrences = 0;
            for (int index = 0; index < expression.length(); index++) {
                if (expression.substring(index, index + 1).equals(value)) {
                    occurrences++;
                }
            }
            boolean shouldDisable = occurrences > sameKeyIndex;
            boolean wasEnabled = !numberKey.isDisabled();
            numberKey.setDisable(shouldDisable);
            if (shouldDisable && wasEnabled) {
                animateUsedNumber(numberKey);
            }
        }
    }

    private void animateUsedNumber(Button numberKey) {
        FadeTransition transition = new FadeTransition(Duration.millis(180), numberKey);
        transition.setFromValue(0.45);
        transition.setToValue(1.0);
        transition.play();
    }

    private void normalizeKeyboardOperator(KeyEvent event) {
        String character = event.getCharacter();
        if (!"*".equals(character) && !"/".equals(character)) {
            return;
        }

        event.consume();
        String visibleOperator = "*".equals(character) ? "×" : "÷";
        answerField.insertText(answerField.getCaretPosition(), visibleOperator);
    }

    private BorderPane loadLayout(String resourceName, String errorMessage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/co/edu/poli/allten/views/" + resourceName));
            return loader.load();
        } catch (IOException e) {
            throw new IllegalStateException(errorMessage, e);
        }
    }

    private void addStylesheet(String stylesheetName) {
        String stylesheet = getClass()
                .getResource("/co/edu/poli/allten/styles/" + stylesheetName)
                .toExternalForm();
        if (!scene.getStylesheets().contains(stylesheet)) {
            scene.getStylesheets().add(stylesheet);
        }
    }

    private <T extends Node> T find(Node root, String id, Class<T> type) {
        Node node = root.lookup("#" + id);
        if (!type.isInstance(node)) {
            throw new IllegalStateException("La vista no contiene el nodo esperado: #" + id);
        }
        return type.cast(node);
    }
}
