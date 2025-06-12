package bomberman;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

/**
 * Représente un power-up (bonus) sur la grille de jeu.
 * Un power-up peut améliorer la portée des bombes, ajouter un bouclier, ou réduire le temps de recharge.
 * Hérite de {@link ImageView} pour permettre son affichage dans l'interface JavaFX.
 */
public class Powerup extends ImageView {

    /** Position X du power-up sur la grille. */
    private final int gridX;

    /** Position Y du power-up sur la grille. */
    private final int gridY;

    /** Type de power-up : "range", "shield", "cooldown". */
    private final String type;

    /**
     * Constructeur de power-up.
     *
     * @param gridX Position X dans la grille.
     * @param gridY Position Y dans la grille.
     * @param type Type du power-up : "range", "shield" ou "cooldown".
     */
    public Powerup(int gridX, int gridY, String type) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.type = type;

        // Chargement des images pour chaque type de power-up
        Image rangeImage = new Image(getClass().getResourceAsStream("/bomberman/images/powerup_bombe_range.png"));
        Image shieldImage  = new Image(getClass().getResourceAsStream("/bomberman/images/powerup_shield.png"));
        Image cooldownImage = new Image(getClass().getResourceAsStream("/bomberman/images/powerup_cooldown.png"));

        // Définir l'image du power-up en fonction de son type
        if (Objects.equals(this.type, "range")) {
            this.setImage(rangeImage);
        } else if (Objects.equals(this.type, "shield")) {
            this.setImage(shieldImage);
        } else if (Objects.equals(this.type, "cooldown")) {
            this.setImage(cooldownImage);
        }

        // Définir la taille et la position du power-up
        this.setFitWidth(Player.TILE_SIZE);
        this.setFitHeight(Player.TILE_SIZE);
        this.setX(gridX * Player.TILE_SIZE);
        this.setY(gridY * Player.TILE_SIZE);
    }

    /**
     * @return La position X sur la grille.
     */
    public int getGridX() {
        return gridX;
    }

    /**
     * @return La position Y sur la grille.
     */
    public int getGridY() {
        return gridY;
    }
}
