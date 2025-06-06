package bomberman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            BorderPane root = FXMLLoader.load(getClass().getResource("/bomberman/BomberMan.fxml"));
            double largeur = 1160;
            double longueur = 655;
            stage.setScene(new Scene(root, largeur, longueur));
            stage.setTitle("Bomberman | GAME");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}