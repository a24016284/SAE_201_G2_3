package bomberman;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameMapMulti {

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

    private long lastBombTime = 0;
    private static final long BOMB_COOLDOWN = 1500;

    private Timeline gameTimer;

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

    public void setPlayerImages(String img1, String img2) {
        this.choixJoueur1 = img1;
        this.choixJoueur2 = img2;
    }

    public Player getPlayer2() {
        return player2;
    }

    // Méthodes pour gérer les scores
    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    private void addPointsToPlayer1(int points) {
        player1Score += points;
        updateScoreDisplay();
    }

    private void addPointsToPlayer2(int points) {
        player2Score += points;
        updateScoreDisplay();
    }

    private void updateScoreDisplay() {
        if (player1ScoreLabel != null) {
            player1ScoreLabel.setText(String.valueOf(player1Score));
        }
        if (player2ScoreLabel != null) {
            player2ScoreLabel.setText(String.valueOf(player2Score));
        }
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
            boolean isPlayerClose = (x - 1 <= playerX && playerX <= x + 1 && y - 1 <= playerY && playerY <= y + 1);
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

    private void placeBomb(Player bombPlacer, int x, int y) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBombTime < BOMB_COOLDOWN) {
            System.out.println(" Attendez encore " + ((BOMB_COOLDOWN - (currentTime - lastBombTime)) / 1000.0) + " secondes !");
            return;
        }

        lastBombTime = currentTime;

        Bomb bomb = new Bomb(x, y);
        bombs.add(bomb);
        gamePane.getChildren().add(bomb);

        PauseTransition explosionDelay = new PauseTransition(Duration.seconds(2));
        explosionDelay.setOnFinished(e -> {
            gamePane.getChildren().remove(bomb);
            bombs.remove(bomb);
            destroyNearbyObstacles(bombPlacer, x, y);
        });
        explosionDelay.play();
    }

    private void destroyNearbyObstacles(Player bombOwner, int centerX, int centerY) {
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

                // Vérifier les ennemis tués et attribuer des points
                List<Enemy> enemiesToRemove = new ArrayList<>();
                for (Enemy enemy : enemies) {
                    if (enemy.getGridX() == x && enemy.getGridY() == y) {
                        gamePane.getChildren().remove(enemy);
                        enemiesToRemove.add(enemy);

                        // Attribuer des points au joueur qui a posé la bombe
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

                // Vérifier si un joueur est tué et attribuer des points à l'adversaire
                if (player.getGridX() == x && player.getGridY() == y) {
                    if (bombOwner == player2) {
                        addPointsToPlayer2(PLAYER_KILL_POINTS);
                        System.out.println("Joueur 2 gagne " + PLAYER_KILL_POINTS + " points pour avoir tué le Joueur 1!");
                    }
                    showExplosionKilledMessage(player);
                } else if (player2.getGridX() == x && player2.getGridY() == y) {
                    if (bombOwner == player) {
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

    public boolean isPaused() {
        return isPaused;
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
                if ((player != player2) && player2.getGridX() == newX && player2.getGridY() == newY) return;

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

    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == javafx.scene.input.KeyCode.SPACE && event.isControlDown()) {
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
            case Z -> movePlayer(player, 0, -1, "HAUT");
            case S -> movePlayer(player, 0, 1, "BAS");
            case Q -> movePlayer(player, -1, 0, "GAUCHE");
            case D -> movePlayer(player, 1, 0, "DROITE");
            case SPACE -> placeBomb(player, player.getGridX(), player.getGridY());

            case UP -> movePlayer(player2, 0, -1, "HAUT");
            case DOWN -> movePlayer(player2, 0, 1, "BAS");
            case LEFT -> movePlayer(player2, -1, 0, "GAUCHE");
            case RIGHT -> movePlayer(player2, 1, 0, "DROITE");
            case M -> placeBomb(player2, player2.getGridX(), player2.getGridY());
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void gameOver() {
        if (gameOverTriggered) return;
        gameOverTriggered = true;

        gamePane.setOnKeyPressed(null);
        //Main.playDeathMusic();

        Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
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

            String winner = "";
            if (player1Score > player2Score) {
                winner = "Joueur 1 gagne avec " + player1Score + " points!";
            } else if (player2Score > player1Score) {
                winner = "Joueur 2 gagne avec " + player2Score + " points!";
            } else {
                winner = "Match nul avec " + player1Score + " points chacun!";
            }

            alert.setContentText("Vous avez été tué par une BOMBE !\n\nScore final:\n" +
                    "Joueur 1: " + player1Score + " points\n" +
                    "Joueur 2: " + player2Score + " points\n\n" +
                    winner);
            alert.showAndWait();
            Platform.exit();
        });
    }

    private void showExplosionKilledMessage(Player killedPlayer) {
        if (gameOverTriggered) return;
        gameOverTriggered = true;

        String joueur = (killedPlayer == player) ? "Joueur 1" : "Joueur 2";
        //Main.playDeathMusic();

        Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
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

    public void setScoreLabels(Label player1ScoreLabel, Label player2ScoreLabel) {
        this.player1ScoreLabel = player1ScoreLabel;
        this.player2ScoreLabel = player2ScoreLabel;
        // Mettre à jour l'affichage initial
        updateScoreDisplay();
    }
}