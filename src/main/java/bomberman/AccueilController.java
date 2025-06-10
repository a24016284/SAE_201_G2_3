package bomberman;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

    private Timeline countdown;
    private int remainingTime = 90;


    @FXML private Label choixJoueur;
    @FXML private Button perso1, perso2, perso3, perso4;

    private String imageJoueur1;
    private String imageJoueur2;

    private int joueurActuel = 1;

    // Propriétés observables pour le binding
    private final BooleanProperty joueur1Choisi = new SimpleBooleanProperty(false);
    private final BooleanProperty joueur2Choisi = new SimpleBooleanProperty(false);

    public static String choixJoueur1;
    public static String choixJoueur2;


    @FXML
    public void initialize() {
        playButton.setOnAction(this::lancerJeu);
        exitButton.setOnAction(e -> Platform.exit());
        playButton.disableProperty().bind(joueur1Choisi.not().or(joueur2Choisi.not()));
    }

    private void lancerJeu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bomberman/Game.fxml"));
            BorderPane root = loader.load();
            GameMap controller = loader.getController();

            // Barre supérieure avec timer
            HBox topBar = new HBox();
            topBar.setStyle("-fx-background-color: orange; -fx-padding: 10;");
            topBar.setAlignment(javafx.geometry.Pos.CENTER);

            Label countdownLabel = new Label("01:30");
            countdownLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
            topBar.getChildren().add(countdownLabel);
            root.setTop(topBar);

            // Compte à rebours
            countdown = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
                remainingTime--;
                int minutes = remainingTime / 60;
                int seconds = remainingTime % 60;
                countdownLabel.setText(String.format("%02d:%02d", minutes, seconds));

                if (remainingTime <= 0) {
                    countdown.stop();
                    showGameOver();
                }
            }));
            countdown.setCycleCount(remainingTime);
            countdown.play();

            controller.setGameTimer(countdown); // Toujours utile si le controller veut l'arrêter

            // Changement de scène
            double largeur = 1160;
            double longueur = 480;
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, largeur, longueur));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showGameOver() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Temps écoulé");
            alert.setHeaderText("Game Over");
            alert.setContentText("Vous avez perdu : le temps est écoulé !");
            alert.showAndWait();
            Platform.exit(); // Ferme le jeu
        });
    }


    @FXML
    private void handleChoix(ActionEvent event) {
        Button boutonClique = (Button) event.getSource();

        // Empêche le bouton d’être choisi à nouveau
        boutonClique.setDisable(true);
        boutonClique.setVisible(false);

        if (joueurActuel == 1) {
            joueur1Choisi.set(true);
            joueurActuel = 2;
            choixJoueur.setText("A toi joueur 2 :");
            choixJoueur1 = boutonClique.getText();
        } else if (joueurActuel == 2) {
            joueur2Choisi.set(true);
            choixJoueur.setText("Les deux joueurs ont choisi !");
            choixJoueur2 = boutonClique.getText();
        }
    }


}
