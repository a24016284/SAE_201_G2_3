package bomberman;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Représente un power-up (bonus) sur la grille de jeu.
 * Un power-up peut améliorer la portée des bombes, ajouter un bouclier, ou réduire le temps de recharge.
 */
public class Powerup extends ImageView {

    private static final Image SHIELD_IMAGE = loadImage("/bomberman/images/powerup_shield.png");
    private static final Image COOLDOWN_IMAGE = loadImage("/bomberman/images/powerup_cooldown.png");

    private final int gridX;
    private final int gridY;
    private final String type;

    public Powerup(int gridX, int gridY, String type) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.type = type;

        setFitWidth(Player.TILE_SIZE);
        setFitHeight(Player.TILE_SIZE);
        setX(gridX * Player.TILE_SIZE);
        setY(gridY * Player.TILE_SIZE);

        switch (type) {
            case "shield" -> setImage(SHIELD_IMAGE);
            case "cooldown" -> setImage(COOLDOWN_IMAGE);
            default -> throw new IllegalArgumentException("Type de power-up inconnu : " + type);
        }
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public String getType() {
        return type;
    }

    private static Image loadImage(String path) {
        try {
            Image img = new Image(Powerup.class.getResourceAsStream(path));
            if (img.isError()) {
                System.err.println("Erreur lors du chargement de l'image : " + path);
                return null;
            }
            return img;
        } catch (Exception e) {
            System.err.println("Exception lors du chargement de l'image " + path + " : " + e.getMessage());
            return null;
        }
    }
}
