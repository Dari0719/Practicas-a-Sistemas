package co.edu.poli.allten.view;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        GestorTema.inicializar(stage);
        Parent root = FXMLLoader.load(
                App.class.getResource("/co/edu/poli/allten/views/pantalla-inicio.fxml"));
        stage.setTitle("Allten");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
