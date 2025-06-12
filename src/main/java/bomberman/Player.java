package bomberman;

import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Représente un joueur dans le jeu Bomberman.
 * Hérite de {@link ImageView} pour afficher une image correspondant au joueur.
 */
public class Player extends ImageView {

    /** Position X du joueur dans la grille. */
    private int x;

    /** Position Y du joueur dans la grille. */
    private int y;

    /** Taille d'une case de la grille en pixels. */
    public static final int TILE_SIZE = 40;

    /** Délai entre deux poses de bombes (en millisecondes). */
    private long bombCooldown = 1000;

    /** Portée d'explosion de la bombe. */
    private int range = 1;

    /** Nombre de boucliers du joueur. */
    private int shield = 0;

    /** Images du joueur dans différentes directions : bas, haut, gauche, droite. */
    private final Image[] directionImages;

    /**
     * Constructeur du joueur.
     *
     * @param gridX Position X initiale sur la grille.
     * @param gridY Position Y initiale sur la grille.
     * @param skinChoice Choix de l'apparence (nom du skin).
     */
    public Player(int gridX, int gridY, String skinChoice) {
        directionImages = loadImagesForSkin(skinChoice);
        this.x = gridX;
        this.y = gridY;

        setImage(directionImages[0]); // Image par défaut : face au bas
        setFitWidth(TILE_SIZE);
        setFitHeight(TILE_SIZE);
        setTranslateX(x * TILE_SIZE);
        setTranslateY(y * TILE_SIZE);
    }

    /**
     * Charge les images du skin sélectionné.
     *
     * @param skinChoice Nom du skin choisi par le joueur.
     * @return Tableau d'images dans l'ordre : bas, haut, gauche, droite.
     */
    private Image[] loadImagesForSkin(String skinChoice) {
        String baseName = switch (skinChoice) {
            case "Personnage 1" -> "player1";
            case "Personnage 2" -> "player2";
            case "Personnage 3" -> "player3";
            case "Personnage 4" -> "player4";
            default -> "player1"; // Skin par défaut
        };

        return new Image[] {
                new Image(getClass().getResourceAsStream("/bomberman/images/" + baseName + ".png")),
                new Image(getClass().getResourceAsStream("/bomberman/images/" + baseName + "_dos.png")),
                new Image(getClass().getResourceAsStream("/bomberman/images/" + baseName + "_left.png")),
                new Image(getClass().getResourceAsStream("/bomberman/images/" + baseName + "_right.png")),
        };
    }

    /** @return La position X du joueur sur la grille. */
    public int getGridX() {
        return x;
    }

    /** @return La position Y du joueur sur la grille. */
    public int getGridY() {
        return y;
    }

    /** @return Le délai actuel entre les poses de bombes. */
    public long getBombCooldown() {
        return bombCooldown;
    }

    /** @return La portée d'explosion actuelle. */
    public int getRange() {
        return range;
    }

    /** @return Le nombre de boucliers actuels du joueur. */
    public int getShield() {
        return shield;
    }

    /**
     * Diminue le nombre de boucliers du joueur de 1.
     * Affiche la valeur restante dans la console.
     */
    public void lowerShield() {
        shield -= 1;
        System.out.println("Shield: " + shield);
    }

    /**
     * Anime le déplacement du joueur vers une nouvelle position.
     *
     * @param newX Nouvelle position X sur la grille.
     * @param newY Nouvelle position Y sur la grille.
     */
    public void moveToAnimated(int newX, int newY) {
        this.x = newX;
        this.y = newY;

        TranslateTransition transition = new TranslateTransition(Duration.millis(200), this);
        transition.setToX(newX * TILE_SIZE);
        transition.setToY(newY * TILE_SIZE);
        transition.play();
    }

    /**
     * Met à jour l'image du joueur en fonction de la direction donnée.
     *
     * @param direction Direction parmi : "BAS", "HAUT", "GAUCHE", "DROITE".
     */
    public void setDirection(String direction) {
        switch (direction) {
            case "BAS" -> setImage(directionImages[0]);
            case "HAUT" -> setImage(directionImages[1]);
            case "GAUCHE" -> setImage(directionImages[2]);
            case "DROITE" -> setImage(directionImages[3]);
        }
    }

    /**
     * Réduit de moitié le délai de pose de bombes (power-up).
     * Affiche le nouveau cooldown dans la console.
     */
    public void powerupCooldown() {
        bombCooldown = bombCooldown / 2;
        System.out.println("Nouveau cooldown: " + bombCooldown);
    }

    /**
     * Augmente la portée des explosions de bombe (power-up).
     */
    public void powerupRange() {
        range += 1;
    }

    /**
     * Ajoute un bouclier au joueur (power-up).
     * Affiche la valeur actuelle dans la console.
     */
    public void powerupShield() {
        shield += 1;
        System.out.println("Shield: " + shield);
    }

    /**
     * Renvoie l'image actuelle représentant la face du joueur.
     * Utile pour les captures ou les effets visuels.
     *
     * @return Image du joueur selon sa dernière direction.
     */
    public Image getFaceImage() {
        return directionImages[0]; // Par défaut renvoie l'image de face (vers le bas)
    }
}
