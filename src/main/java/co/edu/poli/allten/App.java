package co.edu.poli.allten;

import co.edu.poli.allten.controller.GameController;
import co.edu.poli.allten.model.GameModel;
import co.edu.poli.allten.view.GameView;
import co.edu.poli.allten.view.JuegoView;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        GameModel model = new GameModel();
        GameView view = new GameView();
        JuegoView juegoView = new JuegoView();
        GameController controller = new GameController(model, view, juegoView, stage);

        stage.setTitle("Allten");
        stage.setScene(controller.getScene());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}