package bomberman;

import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Classe principale du plateau de jeu de Bomberman en mode multijoueur.
 * Elle gère l'affichage de la carte, les deux joueurs, les ennemis,
 * la logique des bombes, des collisions et le système de points.
 */
public class GameMapMulti {

    static final int TILE_SIZE = Player.TILE_SIZE;

    /**
     * Carte du jeu représentée par une grille de caractères.
     * # = mur, * = obstacle destructible, espace = sol.
     */
    static final String[] MAP = {
            "#############################",
            "# ****  *   *       *  **   #",
            "# #*# # #*# # #*# #*# # # # #",
            "# *   * ***  **   *  **  ** #",
            "# # # #*# #*# # # # # # # # #",
            "# *  **   * * * ***  **   * #",
            "# # #*# #*# # #*# # # # #*# #",
            "# *   **  *   *  ** * *   * #",
            "# # # #*# # # #*#*# # #*# # #",
            "#   **  **  **  *   ***  *  #",
            "#############################"
    };

    private static final int MAP_HEIGHT = MAP.length;
    private static final int MAP_WIDTH = MAP[0].length();

    @FXML
    private Pane gamePane;

    @FXML
    private Label player1ScoreLabel;

    @FXML
    private Label player2ScoreLabel;

    private boolean isPaused = false;
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Bomb> bombs = new ArrayList<>();

    private Player player;
    private Player player2;
    private int playerX = 1;
    private int playerY = 1;
    private boolean gameOverTriggered = false;
    private String choixJoueur1;
    private String choixJoueur2;

    // Système de points
    private int player1Score = 0;
    private int player2Score = 0;
    private static final int ENEMY_KILL_POINTS = 1;
    private static final int PLAYER_KILL_POINTS = 3;

    // Cooldown pour les bombes
    private long lastBombTime = 0;
    private static final long BOMB_COOLDOWN = 1500;

    private Timeline gameTimer;

    /**
     * Initialise la carte du jeu, les deux joueurs et les ennemis.
     * Méthode appelée automatiquement lors du chargement FXML.
     */
    @FXML
    public void initialize() {
        drawMap();
        player = new Player(playerX, playerY, "Personnage 1");
        player2 = new Player(MAP_WIDTH - 2, MAP_HEIGHT - 2, "Personnage 2");

        gamePane.getChildren().addAll(player, player2);

        addEnemies(7);
        updateScoreDisplay();

        gamePane.setFocusTraversable(true);
        Platform.runLater(() -> gamePane.requestFocus());
        gamePane.setOnKeyPressed(this::handleKeyPressed);
    }

    /** ------------------ MÉTHODES DE CONFIGURATION ------------------ */

    /**
     * Définit les images des deux joueurs.
     * @param img1 nom de l'image pour le joueur 1
     * @param img2 nom de l'image pour le joueur 2
     */
    public void setPlayerImages(String img1, String img2) {
        this.choixJoueur1 = img1;
        this.choixJoueur2 = img2;
    }

    /**
     * Définit les labels d'affichage des scores.
     * @param player1ScoreLabel label pour le score du joueur 1
     * @param player2ScoreLabel label pour le score du joueur 2
     */
    public void setScoreLabels(Label player1ScoreLabel, Label player2ScoreLabel) {
        this.player1ScoreLabel = player1ScoreLabel;
        this.player2ScoreLabel = player2ScoreLabel;
        updateScoreDisplay();
    }

    /** ------------------ MÉTHODES D'ACCÈS AUX JOUEURS ------------------ */

    /**
     * @return le joueur principal (joueur 1)
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return le second joueur (joueur 2)
     */
    public Player getPlayer2() {
        return player2;
    }

    /** ------------------ MÉTHODES LIÉES AU SCORE ------------------ */

    /**
     * @return le score actuel du joueur 1
     */
    public int getPlayer1Score() {
        return player1Score;
    }

    /**
     * @return le score actuel du joueur 2
     */
    public int getPlayer2Score() {
        return player2Score;
    }

    /**
     * Ajoute des points au score du joueur 1.
     * @param points nombre de points à ajouter
     */
    private void addPointsToPlayer1(int points) {
        player1Score += points;
        updateScoreDisplay();
    }

    /**
     * Ajoute des points au score du joueur 2.
     * @param points nombre de points à ajouter
     */
    private void addPointsToPlayer2(int points) {
        player2Score += points;
        updateScoreDisplay();
    }

    /**
     * Met à jour l'affichage des scores dans l'interface.
     */
    private void updateScoreDisplay() {
        if (player1ScoreLabel != null) {
            player1ScoreLabel.setText(String.valueOf(player1Score));
        }
        if (player2ScoreLabel != null) {
            player2ScoreLabel.setText(String.valueOf(player2Score));
        }
    }

    /** ------------------ MÉTHODES DE DESSIN ET AFFICHAGE ------------------ */

    /**
     * Dessine la carte à l'écran en fonction des caractères de la grille MAP.
     */
    private void drawMap() {
        Image wallImage = new Image(getClass().getResourceAsStream("/bomberman/images/wall.png"));
        Image obstacleImage = new Image(getClass().getResourceAsStream("/bomberman/images/obstacle.png"));
        Image floorImage = new Image(getClass().getResourceAsStream("/bomberman/images/floor.png"));

        for (int y = 0; y < MAP.length; y++) {
            for (int x = 0; x < MAP[y].length(); x++) {
                char tile = MAP[y].charAt(x);

                Image imageToUse;
                switch (tile) {
                    case '#' -> imageToUse = wallImage;
                    case '*' -> imageToUse = obstacleImage;
                    default -> imageToUse = floorImage;
                }

                ImageView tileView = new ImageView(imageToUse);
                tileView.setFitWidth(TILE_SIZE);
                tileView.setFitHeight(TILE_SIZE);
                tileView.setX(x * TILE_SIZE);
                tileView.setY(y * TILE_SIZE);

                gamePane.getChildren().add(tileView);
            }
        }
    }

    /**
     * Ajoute un nombre spécifique d'ennemis à des positions aléatoires sur la carte.
     * Les ennemis ne peuvent pas apparaître sur les murs, obstacles ou près des joueurs.
     * @param numberOfEnemies nombre d'ennemis à ajouter
     */
    private void addEnemies(int numberOfEnemies) {
        Image enemyImage = new Image(getClass().getResourceAsStream("/bomberman/images/enemy.png"));
        Random random = new Random();
        int added = 0;

        while (added < numberOfEnemies) {
            int y = random.nextInt(MAP_HEIGHT);
            int x = random.nextInt(MAP[0].length());

            char tile = MAP[y].charAt(x);
            boolean isPlayerClose = (x - 2 <= playerX && playerX <= x + 2 && y - 2 <= playerY && playerY <= y + 2);
            boolean isTileEmpty = (tile == ' ');
            boolean alreadyEnemy = enemies.stream().anyMatch(e -> e.getGridX() == x && e.getGridY() == y);

            if (isTileEmpty && !isPlayerClose && !alreadyEnemy) {
                Enemy enemy = new Enemy(x, y, enemyImage, this);
                enemies.add(enemy);
                gamePane.getChildren().add(enemy);
                added++;
            }
        }
    }

    /** ------------------ MÉTHODES DE GESTION DU JEU ------------------ */

    /**
     * Définit le timer principal du jeu.
     * @param gameTimer le timer à utiliser
     */
    public void setGameTimer(Timeline gameTimer) {
        this.gameTimer = gameTimer;
    }

    /**
     * Met le jeu en pause.
     */
    public void pauseGame() {
        isPaused = true;
        for (Enemy enemy : enemies) {
            enemy.pauseMovement();
        }
        if (gameTimer != null) {
            gameTimer.pause();
        }
    }

    /**
     * Reprend le jeu après une pause.
     */
    public void resumeGame() {
        isPaused = false;
        for (Enemy enemy : enemies) {
            enemy.resumeMovement();
        }
        if (gameTimer != null) {
            gameTimer.play();
        }
    }

    /**
     * @return true si le jeu est en pause, false sinon
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * @return true si le jeu est terminé, false sinon
     */
    public boolean isGameOverTriggered() {
        return gameOverTriggered;
    }

    /**
     * Définit l'état de fin de jeu.
     * @param gameOverTriggered true pour terminer le jeu, false sinon
     */
    public void setGameOverTriggered(boolean gameOverTriggered) {
        this.gameOverTriggered = gameOverTriggered;
    }

    /** ------------------ MÉTHODES DE GESTION DES BOMBES ------------------ */

    /**
     * Place une bombe à la position spécifiée par le joueur donné.
     * Respecte le cooldown entre les bombes.
     * @param bombPlacer le joueur qui place la bombe
     * @param x position x de la bombe
     * @param y position y de la bombe
     */
    private void placeBomb(Player bombPlacer, int x, int y) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBombTime < bombPlacer.getBombCooldown()) {
            System.out.println(" Attendez encore " + ((bombPlacer.getBombCooldown() - (currentTime - lastBombTime)) / 1000.0) + " secondes !");
            return;
        }

        lastBombTime = currentTime;

        Bomb bomb = new Bomb(x, y);
        bombs.add(bomb);
        gamePane.getChildren().add(bomb);
        StringBuilder row = new StringBuilder(MAP[y]);
        row.setCharAt(x, 'B');
        MAP[y] = row.toString();

        PauseTransition explosionDelay = new PauseTransition(Duration.seconds(2));
        explosionDelay.setOnFinished(e -> {
            gamePane.getChildren().remove(bomb);
            bombs.remove(bomb);
            destroyNearbyObstacles(bombPlacer, x, y);
        });
        explosionDelay.play();
    }

    /**
     * Détruit les obstacles autour de la position d'explosion d'une bombe.
     * Gère également les dégâts aux joueurs et ennemis, ainsi que l'attribution des points.
     * @param bombOwner le joueur propriétaire de la bombe
     * @param centerX position x du centre de l'explosion
     * @param centerY position y du centre de l'explosion
     */
    private void destroyNearbyObstacles(Player bombOwner, int centerX, int centerY) {
        //Main.playExplosionSound();

        Image floorImage = new Image(getClass().getResourceAsStream("/bomberman/images/floor.png"));
        Image explosionImage = new Image(getClass().getResourceAsStream("/bomberman/images/explosion.png"));
        List<ImageView> explosionEffects = new ArrayList<>();

        // Directions d'explosion : centre et 4 directions cardinales
        int[][] directions = {
                {0, 0}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };

        for (int[] dir : directions) {
            int x = centerX + dir[0];
            int y = centerY + dir[1];

            if (x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT) {
                char tile = MAP[y].charAt(x);

                // Destruction des obstacles
                if (tile == '*') {
                    StringBuilder row = new StringBuilder(MAP[y]);
                    int randomNumber = new Random().nextInt(20);
                    if (randomNumber == 1){
                        row.setCharAt(x, 'S');
                        Powerup shield = new Powerup(x, y, "shield");
                        gamePane.getChildren().add(shield);
                    }
                    else if (randomNumber == 2){
                        row.setCharAt(x, 'C');
                        Powerup cooldown = new Powerup(x, y, "cooldown");
                        gamePane.getChildren().add(cooldown);
                    }
                    else{
                        row.setCharAt(x, ' ');
                    }
                    MAP[y] = row.toString();

                    // Suppression visuelle de l'obstacle
                    gamePane.getChildren().removeIf(node ->
                            node instanceof ImageView &&
                                    ((ImageView) node).getX() == x * TILE_SIZE &&
                                    ((ImageView) node).getY() == y * TILE_SIZE);

                    // Placement du sol à la place
                    ImageView floor = new ImageView(floorImage);
                    floor.setFitWidth(TILE_SIZE);
                    floor.setFitHeight(TILE_SIZE);
                    floor.setX(x * TILE_SIZE);
                    floor.setY(y * TILE_SIZE);
                    gamePane.getChildren().add(0, floor);
                }
                if (tile == 'B') {
                    StringBuilder row = new StringBuilder(MAP[y]);
                    row.setCharAt(x, ' ');
                    MAP[y] = row.toString();
                }

                // Effet visuel d'explosion
                ImageView explosion = new ImageView(explosionImage);
                explosion.setFitWidth(TILE_SIZE);
                explosion.setFitHeight(TILE_SIZE);
                explosion.setX(x * TILE_SIZE);
                explosion.setY(y * TILE_SIZE);
                gamePane.getChildren().add(explosion);
                explosionEffects.add(explosion);

                // Vérification des ennemis tués et attribution des points
                List<Enemy> enemiesToRemove = new ArrayList<>();
                for (Enemy enemy : enemies) {
                    if (enemy.getGridX() == x && enemy.getGridY() == y) {
                        gamePane.getChildren().remove(enemy);
                        enemiesToRemove.add(enemy);

                        // Attribution des points au joueur qui a posé la bombe
                        if (bombOwner == player) {
                            addPointsToPlayer1(ENEMY_KILL_POINTS);
                            System.out.println("Joueur 1 gagne " + ENEMY_KILL_POINTS + " point(s) pour avoir tué un ennemi!");
                        } else if (bombOwner == player2) {
                            addPointsToPlayer2(ENEMY_KILL_POINTS);
                            System.out.println("Joueur 2 gagne " + ENEMY_KILL_POINTS + " point(s) pour avoir tué un ennemi!");
                        }
                    }
                }
                enemies.removeAll(enemiesToRemove);

                // Vérification si un joueur est tué et attribution des points à l'adversaire
                if (player.getGridX() == x && player.getGridY() == y) {
                    if (player.getShield() > 0) { player.lowerShield(); }
                    else if (bombOwner == player2) {
                        addPointsToPlayer2(PLAYER_KILL_POINTS);
                        System.out.println("Joueur 2 gagne " + PLAYER_KILL_POINTS + " points pour avoir tué le Joueur 1!");
                    }
                    showExplosionKilledMessage(player);
                }
                else if (player2.getGridX() == x && player2.getGridY() == y) {
                    if (player2.getShield() > 0) { player2.lowerShield(); }
                    else if (bombOwner == player) {
                        addPointsToPlayer1(PLAYER_KILL_POINTS);
                        System.out.println("Joueur 1 gagne " + PLAYER_KILL_POINTS + " points pour avoir tué le Joueur 2!");
                    }
                    showExplosionKilledMessage(player2);
                }
            }
        }
        PauseTransition cleanup = new PauseTransition(Duration.millis(300));
        cleanup.setOnFinished(e -> gamePane.getChildren().removeAll(explosionEffects));
        cleanup.play();
    }

    /** ------------------ MÉTHODES DE MOUVEMENT ------------------ */

    /**
     * Déplace un joueur dans une direction donnée s'il n'y a pas d'obstacle.
     * @param player le joueur à déplacer
     * @param dx déplacement horizontal (-1, 0, ou 1)
     * @param dy déplacement vertical (-1, 0, ou 1)
     * @param direction direction du mouvement pour l'animation
     */
    private void movePlayer(Player player, int dx, int dy, String direction) {
        int newX = player.getGridX() + dx;
        int newY = player.getGridY() + dy;
        player.setDirection(direction);

        if (newX >= 0 && newX < MAP_WIDTH && newY >= 0 && newY < MAP_HEIGHT) {
            char destination = MAP[newY].charAt(newX);
            // Vérification des collisions avec les éléments de la carte
            if (destination != '#' && destination != '*' && destination != 'B') {
                // Vérification des collisions avec l'autre joueur
                if ((player != player2) && player2.getGridX() == newX && player2.getGridY() == newY) return;

                // Vérification des collisions avec les ennemis
                for (Enemy enemy : enemies) {
                    if (enemy.getGridX() == newX && enemy.getGridY() == newY) {
                        if (player.getShield() > 0) { player.lowerShield();}
                        else {gameOver();}
                        return;
                    }
                }
                player.moveToAnimated(newX, newY);
                if (destination == 'C') {
                    player.powerupCooldown();
                    StringBuilder row = new StringBuilder(MAP[newY]);
                    row.setCharAt(newX, ' ');
                    MAP[newY] = row.toString();
                }
                else if (destination == 'S') {
                    player.powerupShield();
                    StringBuilder row = new StringBuilder(MAP[newY]);
                    row.setCharAt(newX, ' ');
                    MAP[newY] = row.toString();
                }
            }
        }
    }

    /** ------------------ MÉTHODES DE GESTION DES ÉVÉNEMENTS ------------------ */

    /**
     * Gère les événements clavier pour contrôler les deux joueurs.
     * Joueur 1 : ZQSD + Espace pour les bombes
     * Joueur 2 : Flèches directionnelles + M pour les bombes
     * Ctrl + Espace : Pause/Reprendre
     * @param event l'événement clavier
     */
    private void handleKeyPressed(KeyEvent event) {
        // Gestion de la pause avec Ctrl + Espace
        if (event.getCode() == javafx.scene.input.KeyCode.SPACE && event.isControlDown()) {
            if (isPaused) {
                resumeGame();
            } else {
                pauseGame();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Pause");
                    alert.setHeaderText(null);
                    alert.setContentText(" Le jeu est en pause.\nAppuyez sur Ctrl + Espace ou fermez cette boîte pour reprendre.");
                    alert.showAndWait();
                    resumeGame();
                });
            }
            return;
        }

        if (isPaused || gameOverTriggered) return;

        // Contrôles des joueurs
        switch (event.getCode()) {
            // Joueur 1 (ZQSD)
            case Z -> movePlayer(player, 0, -1, "HAUT");
            case S -> movePlayer(player, 0, 1, "BAS");
            case Q -> movePlayer(player, -1, 0, "GAUCHE");
            case D -> movePlayer(player, 1, 0, "DROITE");
            case SPACE -> placeBomb(player, player.getGridX(), player.getGridY());

            // Joueur 2 (Flèches)
            case UP -> movePlayer(player2, 0, -1, "HAUT");
            case DOWN -> movePlayer(player2, 0, 1, "BAS");
            case LEFT -> movePlayer(player2, -1, 0, "GAUCHE");
            case RIGHT -> movePlayer(player2, 1, 0, "DROITE");
            case M -> placeBomb(player2, player2.getGridX(), player2.getGridY());
        }
    }

    /** ------------------ MÉTHODES DE FIN DE JEU ------------------ */

    /**
     * Termine le jeu suite à une collision avec un ennemi.
     * Affiche les scores finaux et détermine le vainqueur.
     */
    public void gameOver() {
        if (gameOverTriggered) return;
        gameOverTriggered = true;

        gamePane.setOnKeyPressed(null);
        //Main.playDeathMusic();

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);

            String winner = "";
            if (player1Score > player2Score) {
                winner = "Joueur 1 gagne avec " + player1Score + " points!";
            } else if (player2Score > player1Score) {
                winner = "Joueur 2 gagne avec " + player2Score + " points!";
            } else {
                winner = "Match nul avec " + player1Score + " points chacun!";
            }

            alert.setContentText("Un joueur a perdu !\n\nScore final:\n" +
                    "Joueur 1: " + player1Score + " points\n" +
                    "Joueur 2: " + player2Score + " points\n\n" +
                    winner);
            alert.showAndWait();
            Platform.exit();
        });
    }

    /**
     * Termine le jeu suite à la mort d'un joueur par une explosion.
     * Affiche les scores finaux et détermine le vainqueur.
     * @param killedPlayer le joueur qui a été tué
     */
    private void showExplosionKilledMessage(Player killedPlayer) {
        if (gameOverTriggered) return;
        gameOverTriggered = true;

        String joueur = (killedPlayer == player) ? "Joueur 1" : "Joueur 2";
        //Main.playDeathMusic();

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);

            String winner = "";
            if (player1Score > player2Score) {
                winner = "Joueur 1 gagne avec " + player1Score + " points!";
            } else if (player2Score > player1Score) {
                winner = "Joueur 2 gagne avec " + player2Score + " points!";
            } else {
                winner = "Match nul avec " + player1Score + " points chacun!";
            }

            alert.setContentText(joueur + " a été tué par une EXPLOSION !\n\nScore final:\n" +
                    "Joueur 1: " + player1Score + " points\n" +
                    "Joueur 2: " + player2Score + " points\n\n" +
                    winner);
            alert.showAndWait();
            Platform.exit();
        });
    }
}