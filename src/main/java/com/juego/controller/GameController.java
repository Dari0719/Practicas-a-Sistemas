package com.juego.controller;

import com.juego.model.GameModel;
import com.juego.view.GameView;
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
        model.startGame();
        view.showGameStarted();
    }

    public GameModel getModel() {
        return model;
    }

    public GameView getView() {
        return view;
    }
}
