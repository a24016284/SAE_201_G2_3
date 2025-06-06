package bomberman;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Bomb extends ImageView {

    private final int gridX;
    private final int gridY;

    public Bomb(int gridX, int gridY) {
        this.gridX = gridX;
        this.gridY = gridY;

        Image bombImage = new Image(getClass().getResourceAsStream("/bomberman/images/Bombe.png"));
        this.setImage(bombImage);
        this.setFitWidth(Player.TILE_SIZE);
        this.setFitHeight(Player.TILE_SIZE);
        this.setX(gridX * Player.TILE_SIZE);
        this.setY(gridY * Player.TILE_SIZE);
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }
}

