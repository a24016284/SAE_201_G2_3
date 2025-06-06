package bomberman;

import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Player extends ImageView {
    private int x;
    private int y;
    public static final int TILE_SIZE = 40;

    public Player(int gridX, int gridY) {
        super(new javafx.scene.image.Image(Player.class.getResourceAsStream("/bomberman/images/player2.png")));
        this.x = gridX;
        this.y = gridY;
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
}
