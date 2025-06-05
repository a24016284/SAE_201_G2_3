package bomberman;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

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

    private Player player;
    private int playerX = 1;
    private int playerY = 1;

    @FXML
    public void initialize() {
        drawMap();
        player = new Player(playerX, playerY);
        gamePane.getChildren().add(player);
        addEnemies(10);

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
            int x = random.nextInt(MAP[y].length());

            if (MAP[y].charAt(x) == ' ') {
                Enemy enemy = new Enemy(x, y, enemyImage);
                gamePane.getChildren().add(enemy);
                added++;
            }
        }
    }

    private void placeBomb(int x, int y) {
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

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int x = centerX + dx;
                int y = centerY + dy;

                if (x >= 0 && x < MAP_WIDTH && y >= 0 && y < MAP_HEIGHT) {
                    if (MAP[y].charAt(x) == '*') {
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
                }
            }
        }
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
                player.moveTo(newX, newY);
            }
        }
    }

}
