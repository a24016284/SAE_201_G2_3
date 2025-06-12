package bomberman;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Représente une bombe placée sur la grille du jeu Bomberman.
 * Elle hérite de ImageView pour pouvoir être affichée facilement dans la scène.
 */
public class Bomb extends ImageView {

    /** Coordonnée X sur la grille (colonne). */
    private final int gridX;

    /** Coordonnée Y sur la grille (ligne). */
    private final int gridY;

    /**
     * Crée une bombe aux coordonnées spécifiées sur la grille.
     *
     * @param gridX La position horizontale (colonne) dans la grille.
     * @param gridY La position verticale (ligne) dans la grille.
     */
    public Bomb(int gridX, int gridY) {
        this.gridX = gridX;
        this.gridY = gridY;

        // Charge l’image de la bombe
        Image bombImage = new Image(getClass().getResourceAsStream("/bomberman/images/Bombe.png"));
        this.setImage(bombImage);

        // Ajuste la taille de l’image à la taille d’une case
        this.setFitWidth(Player.TILE_SIZE);
        this.setFitHeight(Player.TILE_SIZE);

        // Positionne visuellement la bombe selon sa position dans la grille
        this.setX(gridX * Player.TILE_SIZE);
        this.setY(gridY * Player.TILE_SIZE);
    }

    /**
     * @return la position X de la bombe sur la grille.
     */
    public int getGridX() {
        return gridX;
    }

    /**
     * @return la position Y de la bombe sur la grille.
     */
    public int getGridY() {
        return gridY;
    }
}

