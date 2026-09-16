package co.edu.poli.allten.controller;

import co.edu.poli.allten.model.GameModel;
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
        if (view.getNickname().isEmpty()) {
            view.showNicknameRequired();
            return;
        }

        model.startGame();
        view.showGameStarted(view.getNickname());
    }

    public GameModel getModel() {
        return model;
    }

    public GameView getView() {
        return view;
    }
}
