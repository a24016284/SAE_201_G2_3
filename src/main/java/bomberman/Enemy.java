package bomberman;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.TranslateTransition;


public class Enemy extends ImageView {
    private  GameMap gameMap;
    private GameMapMulti gameMapMulti;
    private GameMode gameMode;
    private int x;
    private int y;
    private static final int TILE_SIZE = Player.TILE_SIZE;
    private boolean alive = true;


    private Timeline movementTimeline;



    public Enemy(int gridX, int gridY, Image enemyImage, GameMap gameMap) {
        super(enemyImage);
        this.gameMap = gameMap;
        this.x = gridX;
        this.y = gridY;
        gameMode  = GameMode.SOLO;
        setFitWidth(TILE_SIZE);
        setFitHeight(TILE_SIZE);
        setTranslateX(x * TILE_SIZE);
        setTranslateY(y * TILE_SIZE);
        startMoving();
    }

    public Enemy(int gridX, int gridY, Image enemyImage, GameMapMulti gameMapMulti) {
        super(enemyImage);
        this.gameMapMulti = gameMapMulti;
        this.x = gridX;
        this.y = gridY;
        gameMode  = GameMode.MULTI;
        setFitWidth(TILE_SIZE);
        setFitHeight(TILE_SIZE);
        setTranslateX(x * TILE_SIZE);
        setTranslateY(y * TILE_SIZE);
        startMoving();
    }

    public int getGridX() {
        return x;
    }

    public int getGridY() {
        return y;
    }


    private void startMoving() {
        movementTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> moveRandomly()));
        movementTimeline.setCycleCount(Animation.INDEFINITE);
        movementTimeline.play();
    }

    public void pauseMovement() {
        if (movementTimeline != null) {
            movementTimeline.pause();
        }
    }

    public void resumeMovement() {
        if (movementTimeline != null) {
            movementTimeline.play();
        }
    }

    private void moveRandomly() {

        if (gameMode == GameMode.SOLO){
            if (gameMap.isPaused()) return;
        } else if (gameMode == GameMode.MULTI) {
            if (gameMapMulti.isPaused()) return;
        }




        int[][] directions = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };
        List<int[]> validMoves = new ArrayList<>();

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (newY >= 0 && newY < GameMap.MAP.length &&
                    newX >= 0 && newX < GameMap.MAP[newY].length() &&
                    GameMap.MAP[newY].charAt(newX) == ' ') {
                validMoves.add(dir);
            }

        }

        if (!validMoves.isEmpty()) {
            int[] move = validMoves.get(new Random().nextInt(validMoves.size()));
            int targetX = x + move[0];
            int targetY = y + move[1];

            x = targetX;
            y = targetY;

            TranslateTransition transition = new TranslateTransition(Duration.millis(200), this);
            transition.setToX(x * TILE_SIZE);
            transition.setToY(y * TILE_SIZE);
            transition.play();

        }


        // Fin de moveRandomly
        if (gameMode == GameMode.SOLO){
            if (x == gameMap.getPlayer().getGridX() && y == gameMap.getPlayer().getGridY()) {
                if (gameMap.getPlayer().getShield() != 0) { gameMap.getPlayer().lowerShield();}
                else {gameMap.gameOver();}
            }
        } else if (gameMode == GameMode.MULTI) {
            if (x == gameMapMulti.getPlayer().getGridX() && y == gameMapMulti.getPlayer().getGridY()) {
                if (gameMapMulti.getPlayer().getShield() != 0) { gameMapMulti.getPlayer().lowerShield();}
                else {gameMapMulti.gameOver();}
            }
        }


    }

    public void setKilled() {this.alive = false;}
}