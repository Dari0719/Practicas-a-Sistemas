package co.edu.poli.allten;

import co.edu.poli.allten.controller.GameController;
import co.edu.poli.allten.model.GameModel;
import co.edu.poli.allten.view.GameView;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        GameModel model = new GameModel();
        GameView view = new GameView();
        GameController controller = new GameController(model, view);

        stage.setTitle("All Ten Game");
        stage.setScene(controller.getScene());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
