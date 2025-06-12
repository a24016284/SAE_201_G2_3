package bomberman;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.io.InputStream;

/**
 * Contrôleur de l'écran d'accueil du jeu Bomberman.
 * Permet de choisir un personnage, lancer le jeu en mode solo ou multi,
 * et gérer les scènes de transition.
 */
public class AccueilController {

    // Boutons pour jouer ou quitter le jeu
    @FXML
    private Button playButton;

    @FXML
    private Button exitButton;

    // Timer pour le compte à rebours
    private Timeline countdown;
    private int remainingTime; // Durée restante du jeu en secondes
    Label countdownLabel;      // Label affichant le temps restant
    double longueur;           // Hauteur de la fenêtre selon le mode

    // Label pour afficher le score (mode solo)
    Label scoreLabel;

    // Labels pour afficher les scores en mode multijoueur
    private Label player1ScoreLabel;
    private Label player2ScoreLabel;
    HBox player1Box;
    HBox player2Box;

    // Choix des personnages sélectionnés par les joueurs
    public static String choixJoueur1;
    public static String choixJoueur2;

    // Image du personnage sélectionné (joueur 1)
    @FXML private ImageView imagePerso;

    // Image du personnage sélectionné (joueur 2 - mode multi)
    @FXML private ImageView imagePerso2;

    // Chemins vers les images des personnages
    private String[] personnages = {
            "/bomberman/images/player1.png",
            "/bomberman/images/player2.png",
            "/bomberman/images/player3.png",
            "/bomberman/images/player4.png"
    };

    // Noms correspondants aux personnages
    private String[] nomsPersonnages = {
            "Personnage 1",
            "Personnage 2",
            "Personnage 3",
            "Personnage 4"
    };

    // Indices des personnages actuellement sélectionnés
    private int indexPersoActuelJ1 = 0;
    private int indexPersoActuelJ2 = 1;

    // Vrai si on est en mode multijoueur
    private boolean modeMulti = false;

    /**
     * Méthode appelée automatiquement à l'initialisation du contrôleur.
     * Elle configure les images, les choix de personnages et les boutons.
     */
    @FXML
    public void initialize() {
        // On détecte le mode multi selon la présence d'un second ImageView
        modeMulti = (imagePerso2 != null);

        // Initialiser les noms des personnages sélectionnés
        choixJoueur1 = nomsPersonnages[indexPersoActuelJ1];
        if (modeMulti) {
            choixJoueur2 = nomsPersonnages[indexPersoActuelJ2];
        }

        // Afficher les images
        updateImageJ1();
        if (modeMulti) updateImageJ2();

        // Ajouter les actions des boutons
        if (playButton != null) playButton.setOnAction(this::lancerJeu);
        if (exitButton != null) exitButton.setOnAction(e -> Platform.exit());
    }

    // Gestion de la sélection du personnage précédent pour le joueur 1
    @FXML
    private void handlePrev(ActionEvent event) {
        indexPersoActuelJ1 = (indexPersoActuelJ1 - 1 + personnages.length) % personnages.length;
        updateImageJ1();
        choixJoueur1 = nomsPersonnages[indexPersoActuelJ1];
    }

    // Gestion de la sélection du personnage suivant pour le joueur 1
    @FXML
    private void handleNext(ActionEvent event) {
        indexPersoActuelJ1 = (indexPersoActuelJ1 + 1) % personnages.length;
        updateImageJ1();
        choixJoueur1 = nomsPersonnages[indexPersoActuelJ1];
    }

    // Sélection précédente pour le joueur 2 (mode multi uniquement)
    @FXML
    private void handlePrev2(ActionEvent event) {
        if (modeMulti) {
            indexPersoActuelJ2 = (indexPersoActuelJ2 - 1 + personnages.length) % personnages.length;
            updateImageJ2();
            choixJoueur2 = nomsPersonnages[indexPersoActuelJ2];
        }
    }

    // Sélection suivante pour le joueur 2 (mode multi uniquement)
    @FXML
    private void handleNext2(ActionEvent event) {
        if (modeMulti) {
            indexPersoActuelJ2 = (indexPersoActuelJ2 + 1) % personnages.length;
            updateImageJ2();
            choixJoueur2 = nomsPersonnages[indexPersoActuelJ2];
        }
    }

    // Met à jour l'image du joueur 1
    private void updateImageJ1() {
        updatePlayerImage(imagePerso, indexPersoActuelJ1);
    }

    // Met à jour l'image du joueur 2
    private void updateImageJ2() {
        if (modeMulti) updatePlayerImage(imagePerso2, indexPersoActuelJ2);
    }

    // Méthode générique pour changer l'image d'un joueur
    private void updatePlayerImage(ImageView imageView, int index) {
        try {
            InputStream stream = getClass().getResourceAsStream(personnages[index]);
            imageView.setImage(new Image(stream));
        } catch (Exception e) {
            System.err.println("Erreur image : " + e.getMessage());
        }
    }

    /**
     * Lance la scène de jeu en mode solo.
     * @param event Événement de clic
     */
    @FXML
    public void JeuSolo(ActionEvent event) {
        changerScene("/bomberman/Solo.fxml", event);
    }

    /**
     * Lance la scène de jeu en mode multijoueur.
     * @param event Événement de clic
     */
    @FXML
    public void JeuMulti(ActionEvent event) {
        changerScene("/bomberman/Multi.fxml", event);
    }

    // Retour à la page d’accueil principale
    @FXML
    private void handleBack(ActionEvent event) {
        changerScene("/bomberman/BomberMan.fxml", event);
    }

    // Méthode utilitaire pour changer de scène
    private void changerScene(String cheminFXML, ActionEvent event) {
        try {
            BorderPane root = FXMLLoader.load(getClass().getResource(cheminFXML));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1160, 755));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Initialise la scène du jeu selon le mode choisi
    private FXMLLoader lancementJeu() {
        FXMLLoader loader;
        if (modeMulti) {
            loader = new FXMLLoader(getClass().getResource("/bomberman/GameMulti.fxml"));
            remainingTime = 90;
            countdownLabel = new Label("1:30");
            longueur = 505;
        } else {
            loader = new FXMLLoader(getClass().getResource("/bomberman/Game.fxml"));
            remainingTime = 600;
            countdownLabel = new Label("10:00");
            longueur = 510;
        }
        return loader;
    }

    /**
     * Lance le jeu en affichant la scène correspondante et initialise le score, le timer, etc.
     * @param event Événement de clic sur "Play"
     */
    private void lancerJeu(ActionEvent event) {
        try {
            FXMLLoader loader = lancementJeu();
            BorderPane root = loader.load();
            Object controller = loader.getController();

            // Barre supérieure contenant timer, score, vague, etc.
            HBox topBar = new HBox();
            topBar.setStyle("-fx-background-color: orange; -fx-padding: 10;");
            topBar.setAlignment(Pos.CENTER);
            topBar.setSpacing(50);

            // Score en mode solo
            if (!modeMulti && controller instanceof GameMap gameController) {
                scoreLabel = new Label("Score: 0");
                scoreLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");
                scoreLabel.textProperty().bind(gameController.scoreProperty().asString("Score: %d"));
            }

            // Score en mode multi
            if (modeMulti && controller instanceof GameMapMulti gameMultiController) {
                // Image joueur 1
                ImageView player1Face = new ImageView(new Image(getClass().getResourceAsStream("/bomberman/images/player1_face.png")));
                player1Face.setFitWidth(45);
                player1Face.setFitHeight(45);

                // Image joueur 2
                ImageView player2Face = new ImageView(new Image(getClass().getResourceAsStream("/bomberman/images/player2_face.png")));
                player2Face.setFitWidth(45);
                player2Face.setFitHeight(45);

                // Labels des scores
                player1ScoreLabel = new Label("0");
                player1ScoreLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");

                player2ScoreLabel = new Label("0");
                player2ScoreLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");

                // Box gauche : score joueur 1
                player1Box = new HBox(10, player1Face, player1ScoreLabel);
                player1Box.setAlignment(Pos.CENTER_LEFT);

                // Box droite : score joueur 2
                player2Box = new HBox(10, player2ScoreLabel, player2Face);
                player2Box.setAlignment(Pos.CENTER_RIGHT);

                gameMultiController.setScoreLabels(player1ScoreLabel,player2ScoreLabel);

            }

            // Timer et vague (centré)
            VBox timerBox = new VBox();
            timerBox.setAlignment(Pos.CENTER);
            timerBox.setSpacing(5);
            countdownLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");
            timerBox.getChildren().add(countdownLabel);

            if (!modeMulti && controller instanceof GameMap gameController) {
                Label waveLabel = new Label();
                waveLabel.textProperty().bind(gameController.waveNumberProperty().asString("Vague %d"));
                waveLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
                timerBox.getChildren().add(waveLabel);
            }

            // Ajouter les éléments selon le mode
            if (!modeMulti) {
                Label spacer = new Label();
                topBar.getChildren().addAll(scoreLabel, timerBox, spacer);
                HBox.setHgrow(scoreLabel, javafx.scene.layout.Priority.ALWAYS);
                HBox.setHgrow(timerBox, javafx.scene.layout.Priority.ALWAYS);
                HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            } else {
                topBar.getChildren().addAll(player1Box, timerBox, player2Box);
                HBox.setHgrow(player1ScoreLabel, javafx.scene.layout.Priority.ALWAYS);
                HBox.setHgrow(timerBox, javafx.scene.layout.Priority.ALWAYS);
                HBox.setHgrow(player2ScoreLabel, javafx.scene.layout.Priority.ALWAYS);
            }

            root.setTop(topBar);

            // Lancer le timer de jeu
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

            // Lier le timer au contrôleur
            if (modeMulti && controller instanceof GameMapMulti gameMultiController) {
                gameMultiController.setGameTimer(countdown);
            } else if (!modeMulti && controller instanceof GameMap gameController) {
                gameController.setGameTimer(countdown);
            }

            // Afficher la scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1160, longueur));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Affiche une alerte Game Over quand le temps est écoulé
    private void showGameOver() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Temps écoulé");
            alert.setHeaderText("Game Over");
            alert.setContentText("Vous avez perdu : le temps est écoulé !");
            alert.showAndWait();
            Platform.exit();
        });
    }
}
