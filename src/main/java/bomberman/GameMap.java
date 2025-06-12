package bomberman;

import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static bomberman.AccueilController.choixJoueur1;

/**
 * Classe principale du plateau de jeu de Bomberman.
 * Elle gère l'affichage de la carte, les vagues d'ennemis,
 * la logique des bombes, des collisions et des power-ups.
 */
public class GameMap {

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

    private boolean isPaused = false;
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Bomb> bombs = new ArrayList<>();

    private Player player;
    private int playerX = 1;
    private int playerY = 1;
    private boolean gameOverTriggered = false;
    private long lastBombTime = 0;

    private Timeline gameTimer;

    private IntegerProperty waveNumber = new SimpleIntegerProperty(1);
    private IntegerProperty score = new SimpleIntegerProperty(0);
    private static final int POINTS_PER_WAVE = 1;

    /**
     * Initialise la carte du jeu, le joueur et les ennemis.
     * Méthode appelée automatiquement lors du chargement FXML.
     */
    @FXML
    public void initialize() {
        drawMap();
        player = new Player(playerX, playerY, choixJoueur1);
        gamePane.getChildren().add(player);
        addEnemies(3);

        gamePane.setFocusTraversable(true);
        Platform.runLater(() -> gamePane.requestFocus());
        gamePane.setOnKeyPressed(this::handleKeyPressed);
    }

    /** ------------------ MÉTHODES DE GESTION DE VAGUE ------------------ */

    /**
     * Démarre une nouvelle vague de jeu.
     * @param i le numéro de la nouvelle vague.
     */
    public void newWave(int i) {
        if (i > 1) {
            addScore(POINTS_PER_WAVE);
        }
        clearGameState();
        restoreOriginalMap();
        resetGameVariables();
        drawMap();
        recreatePlayer();
        addEnemies(1 + 2 * i);

        Platform.runLater(() -> {
            gamePane.requestFocus();
            gamePane.setOnKeyPressed(this::handleKeyPressed);
        });
    }

    /**
     * Incrémente le numéro de vague.
     */
    public void nextWave() {
        this.waveNumber.set(waveNumber.get() + 1);
    }

    public int getWaveNumber() {
        return waveNumber.get();
    }

    public IntegerProperty waveNumberProperty() {
        return waveNumber;
    }

    /** ------------------ MÉTHODES LIÉES AU SCORE ------------------ */

    /**
     * Ajoute un certain nombre de points au score.
     * @param points points à ajouter
     */
    public void addScore(int points) {
        score.set(score.get() + points);
        System.out.println("Score: " + score.get() + " (+" + points + " points)");
    }

    /**
     * @return le score actuel
     */
    public int getScore() {
        return score.get();
    }

    /**
     * @return la propriété de score pour le binding
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /** ------------------ MÉTHODES DE DESSIN ET RÉINITIALISATION ------------------ */

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
     * Vide les objets présents dans le jeu.
     */
    private void clearGameState() {
        gamePane.getChildren().clear();
        enemies.clear();
        bombs.clear();
        player = null;
    }

    /**
     * Restaure la carte à son état initial.
     */
    private void restoreOriginalMap() {
        String[] originalMap = {
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
        System.arraycopy(originalMap, 0, MAP, 0, MAP.length);
    }

    /**
     * Réinitialise les variables liées à l'état du jeu.
     */
    private void resetGameVariables() {
        playerX = 1;
        playerY = 1;
        gameOverTriggered = false;
        isPaused = false;
        lastBombTime = 0;
    }

    /**
     * Recrée le joueur à sa position initiale.
     */
    private void recreatePlayer() {
        player = new Player(playerX, playerY, choixJoueur1);
        gamePane.getChildren().add(player);
    }

    /** ------------------ MÉTHODES DE GESTION DES ENNEMIS ------------------ */

    /**
     * Ajoute un certain nombre d'ennemis à la carte, placés aléatoirement.
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

    /** ------------------ MÉTHODES DE JEU ------------------ */

    /**
     * Gère l'appui des touches clavier pour le déplacement ou la pose de bombe.
     * @param event événement de touche
     */
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE && event.isControlDown()) {
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

        int newX = player.getGridX();
        int newY = player.getGridY();

        switch (event.getCode()) {
            case Z, UP -> { newY--; player.setDirection("HAUT"); }
            case S, DOWN -> { newY++; player.setDirection("BAS"); }
            case Q, LEFT -> { newX--; player.setDirection("GAUCHE"); }
            case D, RIGHT -> { newX++; player.setDirection("DROITE"); }
            case SPACE, M -> { placeBomb(player, player.getGridX(), player.getGridY()); return; }
            default -> { return; }
        }

        if (newX >= 0 && newX < MAP_WIDTH && newY >= 0 && newY < MAP_HEIGHT) {
            char destination = MAP[newY].charAt(newX);
            if (destination != '#' && destination != '*' && destination != 'B') {
                for (Enemy enemy : enemies) {
                    if (enemy.getGridX() == newX && enemy.getGridY() == newY) {
                        if (player.getShield() > 0) { player.lowerShield();}
                        else {gameOver();}
                        return;
                    }
                }
                player.moveToAnimated(newX, newY);
                if (destination == 'C' || destination == 'S') {
                    if (destination == 'C') player.powerupCooldown();
                    else player.powerupShield();

                    // Supprimer l’image du power-up
                    int finalNewX = newX;
                    int finalNewY = newY;
                    gamePane.getChildren().removeIf(node ->
                            node instanceof ImageView &&
                                    ((ImageView) node).getX() == finalNewX * TILE_SIZE &&
                                    ((ImageView) node).getY() == finalNewY * TILE_SIZE);

                    // Ajouter l’image du sol **en arrière-plan**
                    Image floorImage = new Image(getClass().getResourceAsStream("/bomberman/images/floor.png"));
                    ImageView floorView = new ImageView(floorImage);
                    floorView.setFitWidth(TILE_SIZE);
                    floorView.setFitHeight(TILE_SIZE);
                    floorView.setX(newX * TILE_SIZE);
                    floorView.setY(newY * TILE_SIZE);
                    gamePane.getChildren().add(0, floorView);

                    // Mettre à jour la MAP
                    StringBuilder row = new StringBuilder(MAP[newY]);
                    row.setCharAt(newX, ' ');
                    MAP[newY] = row.toString();
                }


            }
        }
    }

    /**
     * Place une bombe à l'endroit du joueur si le cooldown est respecté.
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
            destroyNearbyObstacles(x, y);
        });
        explosionDelay.play();
    }

    /**
     * Détruit les obstacles proches de la bombe et gère les effets secondaires (ennemis, power-ups, joueur).
     */
    private void destroyNearbyObstacles(int centerX, int centerY) {
        //Main.playExplosionSound();

        Image floorImage = new Image(getClass().getResourceAsStream("/bomberman/images/floor.png"));
        Image explosionImage = new Image(getClass().getResourceAsStream("/bomberman/images/explosion.png"));
        List<ImageView> explosionEffects = new ArrayList<>();

        int[][] directions = {
                {0, 0}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };

        for (int[] dir : directions) {
            int x = centerX + dir[0];
            int y = centerY + dir[1];

            if (x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT) {
                char tile = MAP[y].charAt(x);

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

                    char currentTile = MAP[y].charAt(x);
                    if (currentTile != 'C' && currentTile != 'S') {
                        gamePane.getChildren().removeIf(node ->
                                node instanceof ImageView &&
                                        ((ImageView) node).getX() == x * TILE_SIZE &&
                                        ((ImageView) node).getY() == y * TILE_SIZE);
                    }


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

                ImageView explosion = new ImageView(explosionImage);
                explosion.setFitWidth(TILE_SIZE);
                explosion.setFitHeight(TILE_SIZE);
                explosion.setX(x * TILE_SIZE);
                explosion.setY(y * TILE_SIZE);
                gamePane.getChildren().add(explosion);
                explosionEffects.add(explosion);

                List<Enemy> enemiesToRemove = new ArrayList<>();
                for (Enemy enemy : enemies) {
                    if (enemy.getGridX() == x && enemy.getGridY() == y) {
                        gamePane.getChildren().remove(enemy);
                        enemiesToRemove.add(enemy);
                        enemy.setKilled();
                        enemy.pauseMovement();
                    }
                }
                enemies.removeAll(enemiesToRemove);
                if (enemies.isEmpty()) {
                    nextWave();
                    newWave(getWaveNumber());
                }

                if (player.getGridX() == x && player.getGridY() == y && !gameOverTriggered) {
                    if (player.getShield() > 0) { player.lowerShield(); }
                    else { showExplosionKilledMessage(); }
                }
            }
        }

        PauseTransition cleanup = new PauseTransition(Duration.millis(300));
        cleanup.setOnFinished(e -> gamePane.getChildren().removeAll(explosionEffects));
        cleanup.play();
    }

    /** ------------------ MÉTHODES UTILITAIRES ------------------ */

    public boolean isPaused() {
        return isPaused;
    }

    public Player getPlayer() {
        return player;
    }

    public void setGameTimer(Timeline gameTimer) {
        this.gameTimer = gameTimer;
    }

    public void pauseGame() {
        isPaused = true;
        for (Enemy enemy : enemies) {
            enemy.pauseMovement();
        }
        if (gameTimer != null) {
            gameTimer.pause();
        }
    }

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
     * Fin du jeu après contact avec un ennemi.
     */
    public void gameOver() {
        if (gameOverTriggered) return;
        gameOverTriggered = true;
        System.out.println("Game Over!");
        gamePane.setOnKeyPressed(null);

        gamePane.getChildren().remove(player);

        //Main.playDeathMusic();

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText("Vous avez été touché par un ennemi !");
            alert.showAndWait();
            Platform.exit();
        });
    }

    /**
     * Fin du jeu suite à une explosion.
     */
    private void showExplosionKilledMessage() {
        if (gameOverTriggered) return;
        gameOverTriggered = true;

        gamePane.getChildren().remove(player);

        //Main.playDeathMusic();

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText("Vous avez été tué par une EXPLOSION !");
            alert.showAndWait();
            Platform.exit();
        });
    }

}
