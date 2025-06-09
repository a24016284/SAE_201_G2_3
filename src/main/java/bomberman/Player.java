package bomberman;

import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Player extends ImageView {
    private int x;
    private int y;
    public static final int TILE_SIZE = 40;

    private final Image[] directionImages; // 0 = bas, 1 = haut, 2 = gauche, 3 = droite

    public Player(int gridX, int gridY) {
        directionImages = new Image[] {
                new Image(getClass().getResourceAsStream("/bomberman/images/player1.png")),
                new Image(getClass().getResourceAsStream("/bomberman/images/player1_dos.png")),
                new Image(getClass().getResourceAsStream("/bomberman/images/player1_left.png")),
                new Image(getClass().getResourceAsStream("/bomberman/images/player1_right.png"))
        };

        this.x = gridX;
        this.y = gridY;

        setImage(directionImages[0]); // par défaut bas
        setFitWidth(TILE_SIZE);
        setFitHeight(TILE_SIZE);
        setTranslateX(x * TILE_SIZE);
        setTranslateY(y * TILE_SIZE);
    }

    public int getGridX() {
        return x;
    }

    public int getGridY() {
        return y;
    }

    public void moveToAnimated(int newX, int newY) {
        this.x = newX;
        this.y = newY;

        TranslateTransition transition = new TranslateTransition(Duration.millis(200), this);
        transition.setToX(newX * TILE_SIZE);
        transition.setToY(newY * TILE_SIZE);
        transition.play();
    }

    public void setDirection(String direction) {
        switch (direction) {
            case "BAS" -> setImage(directionImages[0]);
            case "HAUT" -> setImage(directionImages[1]);
            case "GAUCHE" -> setImage(directionImages[2]);
            case "DROITE" -> setImage(directionImages[3]);
        }
    }
}



