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
    private static MediaPlayer deathMusic;
    private static MediaPlayer explosionSound;

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
            backgroundMusic.setVolume(0.5);
            backgroundMusic.play();
        }
    }

    public static void playGameOverMusic() {
        stopAllMusic();

        URL resource = Main.class.getResource("/bomberman/audio/fatalité.mp3");
        if (resource != null) {
            Media media = new Media(resource.toExternalForm());
            gameOverMusic = new MediaPlayer(media);
            gameOverMusic.setVolume(0.7);
            gameOverMusic.play();
        }
    }

    public static void playDeathMusic() {
        stopAllMusic();

        URL resource = Main.class.getResource("/bomberman/audio/fatalité.mp3");
        if (resource != null) {
            Media media = new Media(resource.toExternalForm());
            deathMusic = new MediaPlayer(media);
            deathMusic.setVolume(0.8);
            deathMusic.play();
        }
    }

    public static void playExplosionSound() {
        URL resource = Main.class.getResource("/bomberman/audio/boom(1).mp3");
        if (resource != null) {
            Media media = new Media(resource.toExternalForm());
            explosionSound = new MediaPlayer(media);
            explosionSound.setVolume(0.7);
            explosionSound.play();
        }
    }

    public static void stopAllMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
        if (gameOverMusic != null) {
            gameOverMusic.stop();
        }
        if (deathMusic != null) {
            deathMusic.stop();
        }
    }

    @Override
    public void stop() {
        stopAllMusic();
    }
}