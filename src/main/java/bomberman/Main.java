package bomberman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

/**
 * Classe principale du jeu Bomberman. Elle lance l'application JavaFX
 * et permet la gestion de la musique de fond et des effets sonores.
 */
public class Main extends Application {


    // Déclaration des objets MediaPlayer pour gérer différentes musiques et sons
    private static MediaPlayer backgroundMusic;  // Musique jouée en continu pendant le jeu
    private static MediaPlayer gameOverMusic;    // Musique jouée en cas de game over
    private static MediaPlayer deathMusic;       // Son joué quand un joueur meurt
    private static MediaPlayer explosionSound;   // Effet sonore d'explosion

    /**
     * Méthode principale. Point d'entrée de l'application.
     *
     * @param args les arguments de la ligne de commande
     */
    public static void main(String[] args) {
        launch(args); // Lance l'application JavaFX
    }

    /**
     * Méthode appelée au lancement de l'application.
     * Charge l'interface FXML et affiche la fenêtre principale.
     *
     * @param stage la scène principale
     */
    @Override
    public void start(Stage stage) {
        try {
            // playBackgroundMusic();

            // Chargement du fichier FXML (interface graphique)
            BorderPane root = FXMLLoader.load(getClass().getResource("/bomberman/BomberMan.fxml"));

            // Dimensions de la fenêtre
            double largeur = 1160;
            double longueur = 755;

            // Création de la scène avec le layout chargé
            stage.setScene(new Scene(root, largeur, longueur));
            stage.setTitle("Bomberman | GAME");
            stage.show(); // Affiche la fenêtre
        } catch (Exception e) {
            e.printStackTrace(); // Affiche l'erreur en cas de problème de chargement
        }
    }

    /**
     * Joue la musique de fond en boucle.
     */
    public static void playBackgroundMusic() {
        // Pour éviter la superposition de musiques, on peut arrêter toutes les musiques en cours
        // stopAllMusic();

        // Chargement du fichier audio
        URL resource = Main.class.getResource("/bomberman/audio/musique.mp3");
        if (resource != null) {
            Media media = new Media(resource.toExternalForm());
            backgroundMusic = new MediaPlayer(media);
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE); // Musique en boucle
            backgroundMusic.setVolume(0.5); // Volume moyen
            backgroundMusic.play(); // Démarre la musique
        }
    }

    /**
     * Joue la musique de fin de partie (game over).
     */
    /*
    public static void playGameOverMusic() {
        stopAllMusic(); // Stoppe toutes les autres musiques

        URL resource = Main.class.getResource("/bomberman/audio/fatalité.mp3");
        if (resource != null) {
            Media media = new Media(resource.toExternalForm());
            gameOverMusic = new MediaPlayer(media);
            gameOverMusic.setVolume(0.7); // Volume légèrement plus élevé
            gameOverMusic.play(); // Joue la musique de fin de partie
        }
    }
    */

    /**
     * Joue la musique de mort du joueur.
     */
    /*
    public static void playDeathMusic() {
        stopAllMusic(); // Stoppe les autres sons

        URL resource = Main.class.getResource("/bomberman/audio/fatalité.mp3");
        if (resource != null) {
            Media media = new Media(resource.toExternalForm());
            deathMusic = new MediaPlayer(media);
            deathMusic.setVolume(0.8); // Volume un peu plus fort pour insister sur l’événement
            deathMusic.play(); // Joue le son de mort
        }
    }
    */

    /**
     * Joue le son d'explosion.
     */
    /*
    public static void playExplosionSound() {
        URL resource = Main.class.getResource("/bomberman/audio/boom(1).mp3");
        if (resource != null) {
            Media media = new Media(resource.toExternalForm());
            explosionSound = new MediaPlayer(media);
            explosionSound.setVolume(0.7); // Volume modéré
            explosionSound.play(); // Joue l'effet sonore
        }
    }
    */

    /**
     * Arrête toutes les musiques ou sons en cours.
     */
    /*
     public static void stopAllMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop(); // Arrête la musique de fond
        }
        if (gameOverMusic != null) {
            gameOverMusic.stop(); // Arrête la musique de fin
        }
        if (deathMusic != null) {
            deathMusic.stop(); // Arrête le son de mort
        }
    }
    */

    /**
     * Méthode appelée à la fermeture de l'application.
     * Elle permet d’arrêter tous les sons proprement.
     */
    /*
    @Override
    public void stop() {
        stopAllMusic(); // Nettoyage des sons à la fin
    }
    */
}
