package bomberman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class Main extends Application {

    private static MediaPlayer backgroundMusic;
    private static MediaPlayer gameOverMusic;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            playBackgroundMusic();

            BorderPane root = FXMLLoader.load(getClass().getResource("/bomberman/BomberMan.fxml"));
            double largeur = 1160;
            double longueur = 755;
            stage.setScene(new Scene(root, largeur, longueur));
            stage.setTitle("Bomberman | GAME");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playBackgroundMusic() {
        stopAllMusic();

        URL resource = Main.class.getResource("/bomberman/audio/musique.mp3");
        if (resource != null) {
            Media media = new Media(resource.toExternalForm());
            backgroundMusic = new MediaPlayer(media);
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundMusic.play();
        }
    }

    public static void playGameOverMusic() {
        stopAllMusic();

        URL resource = Main.class.getResource("/bomberman/audio/fatalité.mp3");
        if (resource != null) {
            Media media = new Media(resource.toExternalForm());
            gameOverMusic = new MediaPlayer(media);
            gameOverMusic.play();
        }
    }

    public static void stopAllMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
        if (gameOverMusic != null) {
            gameOverMusic.stop();
        }
    }

    @Override
    public void stop() {
        stopAllMusic();
    }
}
