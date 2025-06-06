package bomberman;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class AccueilController {

    @FXML
    private Button playButton;

    @FXML
    private Button exitButton;

    @FXML
    private ImageView imageView;

    @FXML
    public void initialize() {
        playButton.setOnAction(this::lancerJeu);
        exitButton.setOnAction(e -> Platform.exit());
    }

    private void lancerJeu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bomberman/Game.fxml"));
            BorderPane root = loader.load();  // Correction ici
            GameMap controller = loader.getController();

            HBox topBar = new HBox();
            topBar.setStyle("-fx-background-color: orange; -fx-padding: 10;");
            topBar.setAlignment(javafx.geometry.Pos.CENTER);

            Label timerLabel = new Label("00:00");
            timerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
            topBar.getChildren().add(timerLabel);
            root.setTop(topBar);  // root est maintenant un BorderPane

            final int[] secondsElapsed = {0};
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
                secondsElapsed[0]++;
                int minutes = secondsElapsed[0] / 60;
                int seconds = secondsElapsed[0] % 60;
                timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();

            controller.setGameTimer(timeline); // Ajoute cette ligne pour lier le timer au contrôleur

            double largeur = 1160;
            double longueur = 480;
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, largeur, longueur);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}


