package bomberman;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Player extends ImageView {

    public static final int TILE_SIZE = 40;

    private int gridX;
    private int gridY;

    public Player(int gridX, int gridY) {
        this.gridX = gridX;
        this.gridY = gridY;

        // Charge l'image du joueur
        Image playerImage = new Image(getClass().getResourceAsStream("/bomberman/images/player.png"));
        setImage(playerImage);
        setFitWidth(TILE_SIZE);
        setFitHeight(TILE_SIZE);

        updatePosition();
    }

    public void moveTo(int x, int y) {
        this.gridX = x;
        this.gridY = y;
        updatePosition();
    }

    private void updatePosition() {
        setX(gridX * TILE_SIZE);
        setY(gridY * TILE_SIZE);
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }
}
