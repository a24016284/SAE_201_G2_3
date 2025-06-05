package fr.amu.iut.exercice10;

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
            BorderPane root = FXMLLoader.load(getClass().getClassLoader().getResource("exercice10/BomberMan.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Bomberman | GAME");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}