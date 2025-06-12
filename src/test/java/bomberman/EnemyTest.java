package bomberman;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javafx.scene.image.Image;
import org.junit.jupiter.api.Test;

/**
 * Classe de test unitaire pour la classe {@link Enemy}.
 * Vérifie le bon fonctionnement de la création, de l'état de vie et de l'image d'un ennemi.
 */
public class EnemyTest {

    /** Image utilisée pour l'ennemi dans les tests. */
    Image enemyImage = new Image(getClass().getResourceAsStream("/bomberman/images/enemy.png"));

    /** Carte du jeu utilisée pour positionner l'ennemi dans les tests. */
    GameMap map = new GameMap();

    /**
     * Vérifie qu'un ennemi nouvellement créé est vivant par défaut.
     */
    @Test
    public void ennemiEstVivantParDefaut() {
        Enemy enemy = new Enemy(0, 0, enemyImage, map);
        assertTrue(enemy.isAlive(), "Un ennemi doit être vivant après sa création.");
    }

    /**
     * Vérifie que les coordonnées X et Y d'un ennemi sont correctement définies lors de sa création.
     */
    @Test
    public void ennemiCreeAvecCoordonneesCorrectes() {
        Enemy enemy = new Enemy(3, 7, enemyImage, map);
        assertEquals(3, enemy.getXGrid(), "La coordonnée X de l'ennemi est incorrecte.");
        assertEquals(7, enemy.getYGrid(), "La coordonnée Y de l'ennemi est incorrecte.");
    }

    /**
     * Vérifie que l'image de l'ennemi est bien chargée et non nulle.
     */
    @Test
    public void imageDeLEnnemiChargee() {
        Enemy enemy = new Enemy(0, 0, enemyImage, map);
        assertNotNull(enemy.getImage(), "L'image de l'ennemi ne doit pas être nulle.");
    }
}
