package bomberman;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            BorderPane root = new BorderPane();
            root.setCenter(FXMLLoader.load(getClass().getResource("/bomberman/BomberMan.fxml")));
            //orange
            HBox topBar = new HBox();
            topBar.setStyle("-fx-background-color: orange; -fx-padding: 10;");
            topBar.setAlignment(javafx.geometry.Pos.CENTER);

            Label timerLabel = new Label("00:00");
            timerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
            topBar.getChildren().add(timerLabel);
            root.setTop(topBar);
            final int[] secondsElapsed = {0};
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                secondsElapsed[0]++;
                int minutes = secondsElapsed[0] / 60;
                int seconds = secondsElapsed[0] % 60;
                timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();

            double largeur = 1160;
            double longueur = 440 + 40; // Un peu de place pour la barre
            stage.setScene(new Scene(root, largeur, longueur));
            stage.setTitle("Bomberman | GAME");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
