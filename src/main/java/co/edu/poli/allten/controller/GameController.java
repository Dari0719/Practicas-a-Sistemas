package co.edu.poli.allten.controller;

import co.edu.poli.allten.model.GameModel;
import co.edu.poli.allten.model.ExpressionValidationResult;
import co.edu.poli.allten.view.GameView;
import javafx.scene.Scene;

public class GameController {
    private final GameModel model;
    private final GameView view;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
        view.createScene();
        view.setOnStartGame(this::startGame);
    }

    public Scene getScene() {
        return view.createScene();
    }

    private void startGame() {
        String nickname = view.getNickname();

        if (nickname == null || nickname.isBlank()) {
            view.showNicknameRequired();
            return;
        }

        model.startGame(nickname);
        view.showGameStarted(model.getSession().getPlayer().getNickname(), this::submitAnswer, this::selectTarget);
        view.showRound(model.getCurrentRound());
    }

    private void selectTarget(int targetNumber) {
        if (model.selectTarget(targetNumber)) {
            view.showRound(model.getCurrentRound());
        }
    }

    private void submitAnswer(String answer) {
        if (answer == null || answer.isBlank()) {
            view.showAnswerRequired();
            return;
        }

        ExpressionValidationResult result = model.processAnswer(answer);
        if (result == ExpressionValidationResult.VALID) {
            view.markRoundCompleted(model.getLastSolvedTarget(), answer);
            if (model.isGameStarted()) {
                view.showRound(model.getCurrentRound());
            } else {
                view.showGameSummary(model.getSession().getTotalTime(), model.getRanking().getPlayers());
            }
        } else {
            view.showValidationError(result, model.getLastCalculatedResult());
        }
    }

    public GameModel getModel() {
        return model;
    }

    public GameView getView() {
        return view;
    }
}
