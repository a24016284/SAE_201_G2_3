package bomberman;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class Powerup extends ImageView {

    private final int gridX;
    private final int gridY;
    private final String type;

    public Powerup(int gridX, int gridY, String type) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.type = type;

        Image rangeImage = new Image(getClass().getResourceAsStream("/bomberman/images/powerup_bombe_range.png"));
        Image shieldImage  = new Image(getClass().getResourceAsStream("/bomberman/images/powerup_shield.png"));
        Image cooldownImage = new Image(getClass().getResourceAsStream("/bomberman/images/powerup_cooldown.png"));

        if (Objects.equals(this.type, "range")){ this.setImage(rangeImage); }
        else if (Objects.equals(this.type, "shield")){ this.setImage(shieldImage); }
        else if (Objects.equals(this.type, "cooldown")){ this.setImage(cooldownImage); }

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
