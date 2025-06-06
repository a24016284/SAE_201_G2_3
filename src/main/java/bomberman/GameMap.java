package bomberman;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameMap {

    static final int TILE_SIZE = Player.TILE_SIZE;
    static final String[] MAP = {
            "#############################",
            "#   *   #   *   #   *   #   #",
            "# ### ### ### ### ### ### # #",
            "# *   *   *   *   *   *   * #",
            "# ### ### ### ### ### ### # #",
            "# *   *   *   *   *   *   * #",
            "# ### ### ### ### ### ### # #",
            "# *   *   *   *   *   *   * #",
            "# ### ### ### ### ### ### # #",
            "#   *   #   *   #   *   #   #",
            "#############################"
    };

    private static final int MAP_HEIGHT = MAP.length;
    private static final int MAP_WIDTH = MAP[0].length();

    @FXML
    private Pane gamePane;

//    Liste Ennemies et Bombe
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Bomb> bombs = new ArrayList<>();

//    Création joueur
    private Player player;
    private int playerX = 1;
    private int playerY = 1;
    private boolean gameOverTriggered = false;

    private int activeBombs = 0;
    private int remainingBombs = 5;


    @FXML
    public void initialize() {
        drawMap();
        player = new Player(playerX, playerY);
        gamePane.getChildren().add(player);
        addEnemies(7);

        gamePane.setFocusTraversable(true);
        Platform.runLater(() -> gamePane.requestFocus());
        gamePane.setOnKeyPressed(this::handleKeyPressed);
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

            // On ne veut pas de mur, ni d'obstacle, ni de joueur à cet endroit
            boolean isPlayerPos = (x == playerX && y == playerY);
            boolean isTileEmpty = (tile == ' ');

            boolean alreadyEnemy = enemies.stream().anyMatch(e -> e.getGridX() == x && e.getGridY() == y);

            if (isTileEmpty && !isPlayerPos && !alreadyEnemy) {
                Enemy enemy = new Enemy(x, y, enemyImage, this);
                enemies.add(enemy);
                gamePane.getChildren().add(enemy);
                added++;
            }
        }
    }


    private void placeBomb(int x, int y) {
        if (remainingBombs <= 0) {
            System.out.println("⚠️ Plus de bombes disponibles !");
            return;
        }

        remainingBombs--;
        System.out.println("- Nombre restant de Bombes : " + remainingBombs);

        Bomb bomb = new Bomb(x, y);
        gamePane.getChildren().add(bomb);

        PauseTransition explosionDelay = new PauseTransition(Duration.seconds(2));
        explosionDelay.setOnFinished(e -> {
            gamePane.getChildren().remove(bomb);
            destroyNearbyObstacles(x, y);
        });
        explosionDelay.play();
    }




    private void destroyNearbyObstacles(int centerX, int centerY) {
        Image floorImage = new Image(getClass().getResourceAsStream("/bomberman/images/floor.png"));
        Image explosionImage = new Image(getClass().getResourceAsStream("/bomberman/images/explosion.png"));
        List<ImageView> explosionEffects = new ArrayList<>();

        // Rayon d'explosion dans les 4 directions + centre
        int[][] directions = {
                {0, 0}, // centre
                {1, 0}, {-1, 0}, // droite / gauche
                {0, 1}, {0, -1}  // bas / haut
        };

        for (int[] dir : directions) {
            int x = centerX + dir[0];
            int y = centerY + dir[1];

            if (x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT) {
                char tile = MAP[y].charAt(x);

                // Détruit les obstacles
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

                // 🔥 Ajoute l'effet d'explosion
                ImageView explosion = new ImageView(explosionImage);
                explosion.setFitWidth(TILE_SIZE);
                explosion.setFitHeight(TILE_SIZE);
                explosion.setX(x * TILE_SIZE);
                explosion.setY(y * TILE_SIZE);
                gamePane.getChildren().add(explosion);
                explosionEffects.add(explosion);

                // 💀 Tue les ennemis touchés
                List<Enemy> enemiesToRemove = new ArrayList<>();
                for (Enemy enemy : enemies) {
                    if (enemy.getGridX() == x && enemy.getGridY() == y) {
                        gamePane.getChildren().remove(enemy);
                        enemiesToRemove.add(enemy);
                    }
                }
                enemies.removeAll(enemiesToRemove);

                // 💀 Vérifie si le player est dans le rayon
                if (player.getGridX() == x && player.getGridY() == y && !gameOverTriggered) {
                    showExplosionKilledMessage();
                }
            }
        }

        // 🔄 Retire l'effet d'explosion après 300 ms
        PauseTransition cleanup = new PauseTransition(Duration.millis(300));
        cleanup.setOnFinished(e -> gamePane.getChildren().removeAll(explosionEffects));
        cleanup.play();
    }


    private void handleKeyPressed(KeyEvent event) {
        int newX = player.getGridX();
        int newY = player.getGridY();

        switch (event.getCode()) {
            case Z:
            case UP:
                newY--;
                break;
            case S:
            case DOWN:
                newY++;
                break;
            case Q:
            case LEFT:
                newX--;
                break;
            case D:
            case RIGHT:
                newX++;
                break;
            case SPACE:
                placeBomb(player.getGridX(), player.getGridY());
                return;
            default:
                return;
        }

        if (newX >= 0 && newX < MAP_WIDTH && newY >= 0 && newY < MAP_HEIGHT) {
            char destination = MAP[newY].charAt(newX);
            if (destination != '#' && destination != '*') {

                // 🔥 Vérifie d'abord si une bombe est sur cette case
                for (Bomb bomb : bombs) {
                    if (bomb.getGridX() == newX && bomb.getGridY() == newY) {
                        showBombKilledMessage(); // mort immédiate
                        return;
                    }
                }

                // Ensuite seulement, on déplace le joueur
                player.moveTo(newX, newY);
            }
        }
    }


    public Player getPlayer() {
        return player;
    }

    public void gameOver() {
        if (gameOverTriggered) {
            return; // déjà appelé, on ne fait rien
        }
        gameOverTriggered = true;

        System.out.println("Game Over!");
        gamePane.setOnKeyPressed(null);

        Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText("Vous avez été touché par un ennemi !");
            alert.showAndWait();

            Platform.exit();
        });
    }



    public boolean isGameOverTriggered() {
        return gameOverTriggered;
    }

    public void setGameOverTriggered(boolean gameOverTriggered) {
        this.gameOverTriggered = gameOverTriggered;
    }
    private void showBombKilledMessage() {
        if (gameOverTriggered) return;

        gameOverTriggered = true;

        Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText("Vous avez été tué par une BOMBE !");
            alert.showAndWait();

            Platform.exit();
        });

    }
    private void showExplosionKilledMessage() {
        if (gameOverTriggered) return;

        gameOverTriggered = true;

        Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText("Vous avez été tué par une EXPLOSION !");
            alert.showAndWait();

            Platform.exit();
        });
    }


}