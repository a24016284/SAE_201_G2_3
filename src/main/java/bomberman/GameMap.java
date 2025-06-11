//GameMap
package bomberman;

import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

public class GameMap {

    static final int TILE_SIZE = Player.TILE_SIZE;
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

    // Cooldown pour les bombes
    private long lastBombTime = 0;
    private static final long BOMB_COOLDOWN = 1000;

    private Timeline gameTimer;
    private IntegerProperty waveNumber = new SimpleIntegerProperty(1);

    // Ajout du compteur de points
    private IntegerProperty score = new SimpleIntegerProperty(0);
    private static final int POINTS_PER_WAVE = 1; // Points gagnés par vague

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

    /**
     * Démarre une nouvelle vague
     */
    public void newWave(int i) {

        if (i > 1) {
            addScore(POINTS_PER_WAVE);
        }

        // Nettoyer l'état du jeu
        clearGameState();

        // Restaurer la carte originale
        restoreOriginalMap();

        // Réinitialiser les variables de jeu
        resetGameVariables();

        // Réinitialise la carte
        drawMap();

        // Recréer le joueur
        recreatePlayer();

        // Rajouter des ennemis
        addEnemies(1 + 2*i);

        // Remettre le focus
        Platform.runLater(() -> {
            gamePane.requestFocus();
            gamePane.setOnKeyPressed(this::handleKeyPressed);
        });
    }

    public void addScore(int points) {
        score.set(score.get() + points);
        System.out.println("Score: " + score.get() + " (+" + points + " points)");
    }

    /**
     * Retourne le score actuel
     */
    public int getScore() {
        return score.get();
    }

    /**
     * Retourne la propriété score pour le binding
     */
    public IntegerProperty scoreProperty() {
        return score;
    }




    /**
     * Nettoie l'état actuel du jeu
     */
    private void clearGameState() {
        // Vider le pane de jeu
        gamePane.getChildren().clear();

        // Vider les listes
        enemies.clear();
        bombs.clear();

        // Supprimer les références
        if (player != null) {
            player = null;
        }
    }

    /**
     * Restaure la carte originale (sans les modifications des bombes)
     */
    private void restoreOriginalMap() {
        // Carte originale (vous pouvez la stocker comme constante)
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

        // Copier la carte originale
        System.arraycopy(originalMap, 0, MAP, 0, MAP.length);
    }

    /**
     * Remet les variables de jeu à leurs valeurs initiales
     */
    private void resetGameVariables() {
        playerX = 1;
        playerY = 1;
        gameOverTriggered = false;
        isPaused = false;
        lastBombTime = 0;
    }

    /**
     * Recrée le joueur à sa position initiale
     */
    private void recreatePlayer() {
        player = new Player(playerX, playerY, choixJoueur1);
        gamePane.getChildren().add(player);
    }

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

    private void addEnemies(int numberOfEnemies) {
        Image enemyImage = new Image(getClass().getResourceAsStream("/bomberman/images/enemy.png"));
        Random random = new Random();
        int added = 0;

        while (added < numberOfEnemies) {
            int y = random.nextInt(MAP_HEIGHT);
            int x = random.nextInt(MAP[0].length());

            char tile = MAP[y].charAt(x);
            boolean isPlayerClose = (x-2 <= playerX && playerX <= x+2 && y-2<= playerY && playerY <= y+2);
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

    public int getWaveNumber() { return waveNumber.get(); }

    public void nextWave() {
        this.waveNumber.set(waveNumber.get()+1);
    }

    public IntegerProperty waveNumberProperty() {
        return waveNumber;
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

    private void placeBomb(int x, int y) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBombTime < BOMB_COOLDOWN) {
            System.out.println(" Attendez encore " + ((BOMB_COOLDOWN - (currentTime - lastBombTime)) / 1000.0) + " secondes !");
            return;
        }

        lastBombTime = currentTime;

        Bomb bomb = new Bomb(x, y);
        bombs.add(bomb);
        gamePane.getChildren().add(bomb);
        StringBuilder row = new StringBuilder(MAP[y]);
        row.setCharAt(x, '*');
        MAP[y] = row.toString();

        PauseTransition explosionDelay = new PauseTransition(Duration.seconds(2));
        explosionDelay.setOnFinished(e -> {
            gamePane.getChildren().remove(bomb);
            bombs.remove(bomb);
            destroyNearbyObstacles(x, y);
        });
        explosionDelay.play();
    }


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
                    row.setCharAt(x, ' ');
                    MAP[y] = row.toString();

                    gamePane.getChildren().removeIf(node ->
                            node instanceof ImageView &&
                                    ((ImageView) node).getX() == x * TILE_SIZE &&
                                    ((ImageView) node).getY() == y * TILE_SIZE);

                    ImageView floor = new ImageView(floorImage);
                    floor.setFitWidth(TILE_SIZE);
                    floor.setFitHeight(TILE_SIZE);
                    floor.setX(x * TILE_SIZE);
                    floor.setY(y * TILE_SIZE);
                    gamePane.getChildren().add(0, floor);
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
                if (enemies.isEmpty()){
                    nextWave();
                    newWave(getWaveNumber());
                }

                if (player.getGridX() == x && player.getGridY() == y && !gameOverTriggered) {
                    showExplosionKilledMessage();
                }
            }
        }

        PauseTransition cleanup = new PauseTransition(Duration.millis(300));
        cleanup.setOnFinished(e -> gamePane.getChildren().removeAll(explosionEffects));
        cleanup.play();
    }


    public boolean isPaused() {
        return isPaused;
    }

    private void handleKeyPressed(KeyEvent event) {

        // 🎮 Pause avec Ctrl + Espace
        if (event.getCode() == KeyCode.SPACE && event.isControlDown()) {
            if (isPaused) {
                resumeGame();
            } else {
                pauseGame();
                Platform.runLater(() -> {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                    alert.setTitle("Pause");
                    alert.setHeaderText(null);
                    alert.setContentText(" Le jeu est en pause.\nAppuyez sur Ctrl + Espace ou fermez cette boîte pour reprendre.");
                    alert.showAndWait();  // Bloque jusqu'à fermeture
                    resumeGame();         //  Reprend le jeu automatiquement après fermeture
                });
            }
            return;
        }


        if (isPaused || gameOverTriggered) {
            return; // Bloque les actions si en pause ou terminé
        }

        int newX = player.getGridX();
        int newY = player.getGridY();

        switch (event.getCode()) {
            case Z, UP -> {
                newY--;
                player.setDirection("HAUT");
            }
            case S, DOWN -> {
                newY++;
                player.setDirection("BAS");
            }
            case Q, LEFT -> {
                newX--;
                player.setDirection("GAUCHE");
            }
            case D, RIGHT -> {
                newX++;
                player.setDirection("DROITE");
            }
            case SPACE -> {
                placeBomb(player.getGridX(), player.getGridY());
                return;
            }
            default -> {
                return;
            }

        }

        if (newX >= 0 && newX < MAP_WIDTH && newY >= 0 && newY < MAP_HEIGHT) {
            char destination = MAP[newY].charAt(newX);
            if (destination != '#' && destination != '*') {
                for (Enemy enemy : enemies) {
                    if (enemy.getGridX() == newX && enemy.getGridY() == newY) {
                        gameOver();
                        return;
                    }
                }
                player.moveToAnimated(newX, newY);
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void gameOver() {
        if (gameOverTriggered) return;
        gameOverTriggered = true;

        System.out.println("Game Over!");
        gamePane.setOnKeyPressed(null);

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

    private void showExplosionKilledMessage() {
        if (gameOverTriggered) return;
        gameOverTriggered = true;

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