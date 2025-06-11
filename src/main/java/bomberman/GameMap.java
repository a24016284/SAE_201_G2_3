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

    @FXML
    public void initialize() {
        drawMap();
        player = new Player(playerX, playerY,choixJoueur1);
        gamePane.getChildren().add(player);
        addEnemies(3);

        gamePane.setFocusTraversable(true);
        Platform.runLater(() -> gamePane.requestFocus());
        gamePane.setOnKeyPressed(this::handleKeyPressed);
    }

    public void drawMap() {
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

    public void addEnemies(int numberOfEnemies) {
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

    public void placeBomb(int x, int y) {
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

    public void destroyNearbyObstacles(int centerX, int centerY) {};

    public void handleKeyPressed(KeyEvent event) {};

    public boolean isPaused() {
        return isPaused;
    }

    public Player getPlayer() {
        return player;
    }

    public int getWaveNumber() { return waveNumber.get(); }

    public void nextWave() {
        this.waveNumber.set(waveNumber.get()+1);
    }

    public IntegerProperty waveNumberProperty() {
        return waveNumber;
    }

}