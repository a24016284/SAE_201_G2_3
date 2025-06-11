//AccueilController
package bomberman;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

public class AccueilController {

    @FXML
    private Button playButton;

    @FXML
    private Button exitButton;


    private Timeline countdown;
    private int remainingTime;
    Label countdownLabel;
    double longueur;
    Label scoreLabel;

    // Labels pour les scores en mode multi
    private Label player1ScoreLabel;
    private Label player2ScoreLabel;

    public static String choixJoueur1;
    public static String choixJoueur2;

    // Éléments pour le joueur 1 (toujours présent)
    @FXML private ImageView imagePerso;

    // Éléments pour le joueur 2 (seulement en mode multi)
    @FXML private ImageView imagePerso2;

    private String[] personnages = {
            "/bomberman/images/player1.png",
            "/bomberman/images/player2.png",
            "/bomberman/images/player3.png",
            "/bomberman/images/player4.png"
    };

    // Correspondance entre les index et les noms de personnages
    private String[] nomsPersonnages = {
            "Personnage 1",
            "Personnage 2",
            "Personnage 3",
            "Personnage 4"
    };

    // Index séparés pour chaque joueur
    private int indexPersoActuelJ1 = 0;  // Pour joueur 1
    private int indexPersoActuelJ2 = 1;  // Pour joueur 2 (commence sur un personnage différent)

    private boolean[] persoChoisi = new boolean[2];
    private int joueurActuel = 0;
    private String[] choixJoueurImages = new String[2];

    // Variable pour savoir si on est en mode multi
    private boolean modeMulti = false;


    @FXML
    public void initialize() {
        // Détecter le mode en fonction de la présence des éléments du joueur 2
        modeMulti = (imagePerso2 != null);



        // Initialiser les choix avec les personnages par défaut
        choixJoueur1 = nomsPersonnages[indexPersoActuelJ1];
        if (modeMulti) {
            choixJoueur2 = nomsPersonnages[indexPersoActuelJ2];
        }

        // Initialiser les images
        updateImageJ1();
        if (modeMulti) {
            updateImageJ2();
        }

        // Initialiser boutons seulement s'ils existent
        if (playButton != null) {
            playButton.setOnAction(this::lancerJeu);
        }
        if (exitButton != null) {
            exitButton.setOnAction(e -> Platform.exit());
        }

    }

    @FXML
    private void handlePrev(ActionEvent event) {
        indexPersoActuelJ1--;
        if (indexPersoActuelJ1 < 0) indexPersoActuelJ1 = personnages.length - 1;
        updateImageJ1();
        // Mettre à jour le choix du joueur 1
        choixJoueur1 = nomsPersonnages[indexPersoActuelJ1];
    }

    @FXML
    private void handleNext(ActionEvent event) {
        indexPersoActuelJ1++;
        if (indexPersoActuelJ1 >= personnages.length) indexPersoActuelJ1 = 0;
        updateImageJ1();
        // Mettre à jour le choix du joueur 1
        choixJoueur1 = nomsPersonnages[indexPersoActuelJ1];
    }

    // Gestion du carrousel du joueur 2 (mode multi uniquement)
    @FXML
    private void handlePrev2(ActionEvent event) {
        if (modeMulti) {
            indexPersoActuelJ2--;
            if (indexPersoActuelJ2 < 0) indexPersoActuelJ2 = personnages.length - 1;
            updateImageJ2();
            // Mettre à jour le choix du joueur 2
            choixJoueur2 = nomsPersonnages[indexPersoActuelJ2];
        }
    }

    @FXML
    private void handleNext2(ActionEvent event) {
        if (modeMulti) {
            indexPersoActuelJ2++;
            if (indexPersoActuelJ2 >= personnages.length) indexPersoActuelJ2 = 0;
            updateImageJ2();
            // Mettre à jour le choix du joueur 2
            choixJoueur2 = nomsPersonnages[indexPersoActuelJ2];
        }
    }

    // Mise à jour de l'image du joueur 1
    private void updateImageJ1() {
        updatePlayerImage(imagePerso, indexPersoActuelJ1);
    }

    // Mise à jour de l'image du joueur 2
    private void updateImageJ2() {
        if (modeMulti ) {
            updatePlayerImage(imagePerso2, indexPersoActuelJ2);
        }
    }

    // Méthode générique pour mettre à jour une image de joueur
    private void updatePlayerImage(ImageView imageView, int playerIndex) {
        try {
            InputStream imageStream = getClass().getResourceAsStream(personnages[playerIndex]);
            Image image = new Image(imageStream);
            imageView.setImage(image);

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }
    }

    private FXMLLoader lancementJeu(){
        FXMLLoader loader;
        if(modeMulti){
            loader = new FXMLLoader(getClass().getResource("/bomberman/GameMulti.fxml"));
            remainingTime = 90;
            countdownLabel = new Label("1:30");
            longueur  = 480;
            return loader;
        }
        else{
            loader = new FXMLLoader(getClass().getResource("/bomberman/Game.fxml"));
            remainingTime = 600;
            countdownLabel = new Label("10:00");
            longueur  = 510;
            return loader;
        }
    }


    private void lancerJeu(ActionEvent event) {
        try {
            FXMLLoader loader = lancementJeu();
            BorderPane root = loader.load();

            // Récupérer le contrôleur génériquement
            Object controller = loader.getController();

            // Créer la barre supérieure avec HBox pour l'alignement horizontal
            HBox topBar = new HBox();
            topBar.setStyle("-fx-background-color: orange; -fx-padding: 10;");
            topBar.setAlignment(Pos.CENTER);
            topBar.setSpacing(50); // Espacement entre les éléments

            // Créer le label du score (seulement en mode solo)
            if (!modeMulti && controller instanceof GameMap gameController) {
                scoreLabel = new Label("Score: 0");
                scoreLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");

                // Lier le label du score à la propriété score du contrôleur
                scoreLabel.textProperty().bind(gameController.scoreProperty().asString("Score: %d"));
            }

            // Créer les éléments de score pour le mode multi
            HBox player1ScoreBox = null;
            HBox player2ScoreBox = null;

            if (modeMulti && controller instanceof GameMapMulti gameMultiController) {
                // Créer les images des joueurs
                Image player1FaceImage = new Image(getClass().getResourceAsStream("/bomberman/images/player1_face.png"));
                ImageView player1FaceView = new ImageView(player1FaceImage);
                player1FaceView.setFitWidth(30);
                player1FaceView.setFitHeight(30);

                Image player2FaceImage = new Image(getClass().getResourceAsStream("/bomberman/images/player2_face.png"));
                ImageView player2FaceView = new ImageView(player2FaceImage);
                player2FaceView.setFitWidth(30);
                player2FaceView.setFitHeight(30);

                // Créer les labels de score simples
                player1ScoreLabel = new Label("0");
                player1ScoreLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");

                player2ScoreLabel = new Label("0");
                player2ScoreLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");

                // Créer les HBox pour combiner image + score
                player1ScoreBox = new HBox(10);
                player1ScoreBox.setAlignment(Pos.CENTER_LEFT);
                player1ScoreBox.getChildren().addAll(player1FaceView, player1ScoreLabel);

                player2ScoreBox = new HBox(10);
                player2ScoreBox.setAlignment(Pos.CENTER_RIGHT);
                player2ScoreBox.getChildren().addAll(player2ScoreLabel, player2FaceView);

                // Passer les références des labels au contrôleur
                gameMultiController.setScoreLabels(player1ScoreLabel, player2ScoreLabel);
            }

            // Créer une VBox pour le timer et la vague (centré)
            VBox timerAndWaveBox = new VBox();
            timerAndWaveBox.setAlignment(Pos.CENTER);
            timerAndWaveBox.setSpacing(5);

            countdownLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");
            timerAndWaveBox.getChildren().add(countdownLabel);

            // Si mode solo, ajouter le label de vague
            if (!modeMulti && controller instanceof GameMap gameController) {
                Label waveLabel = new Label();
                waveLabel.textProperty().bind(gameController.waveNumberProperty().asString("Vague %d"));
                waveLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
                timerAndWaveBox.getChildren().add(waveLabel);
            }

            // Organiser les éléments dans la barre supérieure
            if (!modeMulti) {
                // Mode solo : Score à gauche, Timer + Vague au centre, espace vide à droite
                Label spacer = new Label(); // Élément vide pour équilibrer
                spacer.setStyle("-fx-font-size: 18px;");

                topBar.getChildren().addAll(scoreLabel, timerAndWaveBox, spacer);

                // Répartir l'espace équitablement
                HBox.setHgrow(scoreLabel, javafx.scene.layout.Priority.ALWAYS);
                HBox.setHgrow(timerAndWaveBox, javafx.scene.layout.Priority.ALWAYS);
                HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                scoreLabel.setAlignment(Pos.CENTER_LEFT);
                timerAndWaveBox.setAlignment(Pos.CENTER);
                spacer.setAlignment(Pos.CENTER_RIGHT);
            } else {
                // Mode multi : Images + Scores des joueurs à gauche et droite, timer au centre
                topBar.getChildren().addAll(player1ScoreBox, timerAndWaveBox, player2ScoreBox);

                // Répartir l'espace équitablement
                HBox.setHgrow(player1ScoreBox, javafx.scene.layout.Priority.ALWAYS);
                HBox.setHgrow(timerAndWaveBox, javafx.scene.layout.Priority.ALWAYS);
                HBox.setHgrow(player2ScoreBox, javafx.scene.layout.Priority.ALWAYS);

                player1ScoreBox.setAlignment(Pos.CENTER_LEFT);
                timerAndWaveBox.setAlignment(Pos.CENTER);
                player2ScoreBox.setAlignment(Pos.CENTER_RIGHT);
            }

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

            // Appeler setGameTimer() quel que soit le type du contrôleur
            if (modeMulti && controller instanceof GameMapMulti gameMultiController) {
                gameMultiController.setGameTimer(countdown);
            } else if (!modeMulti && controller instanceof GameMap gameController) {
                gameController.setGameTimer(countdown);
            }

            // Changement de scène
            double largeur = 1160;
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, largeur, longueur));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showGameOver() {
        // 🎵 MUSIQUE GAME OVER
        //Main.playGameOverMusic();

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
    public void JeuSolo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bomberman/Solo.fxml"));
            BorderPane root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1160, 755));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void JeuMulti(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bomberman/Multi.fxml"));
            BorderPane root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1160, 755));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bomberman/BomberMan.fxml"));
            BorderPane root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1160, 755));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



