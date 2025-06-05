package fr.amu.iut.exercice10;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Bomb extends ImageView {

    public Bomb(int gridX, int gridY) {
        Image bombImage = new Image(getClass().getResourceAsStream("/exercice10/images/Bombe.png"));
        this.setImage(bombImage);
        this.setFitWidth(Player.TILE_SIZE);
        this.setFitHeight(Player.TILE_SIZE);
        this.setX(gridX * Player.TILE_SIZE);
        this.setY(gridY * Player.TILE_SIZE);
    }
}
