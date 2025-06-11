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

public class GameMapSolo extends GameMap {

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

    @Override
    public void initialize() {
        drawMap();
        player = new Player(playerX, playerY,choixJoueur1);
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

    @Override
    public void destroyNearbyObstacles(int centerX, int centerY) {

        Main.playExplosionSound();

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

    private void movePlayer(Player player, int dx, int dy, String direction) {
        int newX = player.getGridX() + dx;
        int newY = player.getGridY() + dy;
        player.setDirection(direction);

        if (newX >= 0 && newX < MAP_WIDTH && newY >= 0 && newY < MAP_HEIGHT) {
            char destination = MAP[newY].charAt(newX);
            if (destination != '#' && destination != '*') {
                for (Bomb bomb : bombs) {
                    if (bomb.getGridX() == newX && bomb.getGridY() == newY) return;
                }

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

    public void handleKeyPressed(KeyEvent event) {
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
                    alert.showAndWait();
                    resumeGame();
                });
            }
            return;
        }

        if (isPaused || gameOverTriggered) return;

        switch (event.getCode()) {
            case Z, UP -> movePlayer(player, 0, -1, "HAUT");
            case S, DOWN -> movePlayer(player, 0, 1, "BAS");
            case Q, LEFT -> movePlayer(player, -1, 0, "GAUCHE");
            case D, RIGHT -> movePlayer(player, 1, 0, "DROITE");
            case SPACE, M -> placeBomb(player.getGridX(), player.getGridY());
        }
    }

    public void gameOver() {
        if (gameOverTriggered) return;
        gameOverTriggered = true;

        gamePane.setOnKeyPressed(null);

        Main.playDeathMusic();

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

        Main.playDeathMusic();

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