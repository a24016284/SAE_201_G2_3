package bomberman;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Représente un ennemi dans le jeu Bomberman.
 * Il peut se déplacer de manière aléatoire et interagit avec le joueur.
 */
public class Enemy extends ImageView {

    private GameMap gameMap;
    private GameMapMulti gameMapMulti;
    private GameMode gameMode;

    private int x; // Position X dans la grille
    private int y; // Position Y dans la grille

    private static final int TILE_SIZE = Player.TILE_SIZE;

    private boolean alive = true;

    private Timeline movementTimeline;

    /**
     * Constructeur pour le mode solo.
     *
     * @param gridX      Position X dans la grille.
     * @param gridY      Position Y dans la grille.
     * @param enemyImage Image représentant l’ennemi.
     * @param gameMap    Référence à la carte du jeu solo.
     */
    public Enemy(int gridX, int gridY, Image enemyImage, GameMap gameMap) {
        super(enemyImage);
        this.gameMap = gameMap;
        this.x = gridX;
        this.y = gridY;
        this.gameMode = GameMode.SOLO;
        initialize();
    }

    /**
     * Constructeur pour le mode multijoueur.
     *
     * @param gridX         Position X dans la grille.
     * @param gridY         Position Y dans la grille.
     * @param enemyImage    Image représentant l’ennemi.
     * @param gameMapMulti  Référence à la carte du jeu multijoueur.
     */
    public Enemy(int gridX, int gridY, Image enemyImage, GameMapMulti gameMapMulti) {
        super(enemyImage);
        this.gameMapMulti = gameMapMulti;
        this.x = gridX;
        this.y = gridY;
        this.gameMode = GameMode.MULTI;
        initialize();
    }

    /**
     * Initialise les propriétés de l’ennemi (taille, position, déplacement).
     */
    private void initialize() {
        setFitWidth(TILE_SIZE);
        setFitHeight(TILE_SIZE);
        setTranslateX(x * TILE_SIZE);
        setTranslateY(y * TILE_SIZE);
        startMoving();
    }

    /**
     * @return Position X de l’ennemi sur la grille.
     */
    public int getGridX() {
        return x;
    }

    /**
     * @return Position Y de l’ennemi sur la grille.
     */
    public int getGridY() {
        return y;
    }

    /**
     * Démarre le déplacement aléatoire de l’ennemi à intervalles réguliers.
     */
    private void startMoving() {
        movementTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> moveRandomly()));
        movementTimeline.setCycleCount(Animation.INDEFINITE);
        movementTimeline.play();
    }

    /**
     * Met en pause le déplacement de l’ennemi.
     */
    public void pauseMovement() {
        if (movementTimeline != null) {
            movementTimeline.pause();
        }
    }

    /**
     * Reprend le déplacement de l’ennemi après une pause.
     */
    public void resumeMovement() {
        if (movementTimeline != null) {
            movementTimeline.play();
        }
    }

    /**
     * Effectue un déplacement aléatoire dans une direction valide.
     * Gère aussi la collision avec le joueur.
     */
    private void moveRandomly() {
        // Si le jeu est en pause, l’ennemi ne bouge pas
        if ((gameMode == GameMode.SOLO && gameMap.isPaused()) ||
                (gameMode == GameMode.MULTI && gameMapMulti.isPaused())) return;

        // Directions possibles : droite, gauche, bas, haut
        int[][] directions = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };
        List<int[]> validMoves = new ArrayList<>();

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            // Vérifie que la case est dans les limites et vide
            if (newY >= 0 && newY < GameMap.MAP.length &&
                    newX >= 0 && newX < GameMap.MAP[newY].length() &&
                    GameMap.MAP[newY].charAt(newX) == ' ') {
                validMoves.add(dir);
            }
        }

        if (!validMoves.isEmpty()) {
            // Choisit un mouvement valide au hasard
            int[] move = validMoves.get(new Random().nextInt(validMoves.size()));
            int targetX = x + move[0];
            int targetY = y + move[1];

            x = targetX;
            y = targetY;

            // Anime le déplacement de l’ennemi
            TranslateTransition transition = new TranslateTransition(Duration.millis(200), this);
            transition.setToX(x * TILE_SIZE);
            transition.setToY(y * TILE_SIZE);
            transition.play();
        }

        // Vérifie la collision avec le joueur
        if (gameMode == GameMode.SOLO) {
            if (x == gameMap.getPlayer().getGridX() && y == gameMap.getPlayer().getGridY()) {
                if (gameMap.getPlayer().getShield() > 0) {
                    gameMap.getPlayer().lowerShield();
                } else {
                    gameMap.gameOver();
                }
            }
        } else if (gameMode == GameMode.MULTI) {
            if (x == gameMapMulti.getPlayer().getGridX() && y == gameMapMulti.getPlayer().getGridY()) {
                if (gameMapMulti.getPlayer().getShield() > 0) {
                    gameMapMulti.getPlayer().lowerShield();
                } else {
                    gameMapMulti.gameOver();
                }
            }
        }
    }

    /**
     * Marque l’ennemi comme tué (utile pour le suivi de l’état).
     */
    public void setKilled() {
        this.alive = false;
    }

    public int getXGrid() {
        return x;
    }

    public int getYGrid() {
        return y;
    }

    public boolean isAlive() {
        return alive;
    }
}
