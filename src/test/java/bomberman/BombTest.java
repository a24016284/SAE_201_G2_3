package bomberman;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Classe de test unitaire pour la classe {@link Bomb}.
 * Vérifie la création d'une bombe et le bon chargement de son image.
 */
public class BombTest {

    /**
     * Teste que les coordonnées X et Y de la bombe sont correctement assignées lors de sa création.
     */
    @Test
    public void testCoordonneesCorrectes() {
        Bomb bomb = new Bomb(5, 10);
        assertEquals(5, bomb.getGridX(), "La coordonnée X de la bombe est incorrecte.");
        assertEquals(10, bomb.getGridY(), "La coordonnée Y de la bombe est incorrecte.");
    }

    /**
     * Teste que l'image de la bombe est bien chargée (non nulle).
     */
    @Test
    public void testImageNonNulle() {
        Bomb bomb = new Bomb(0, 0);
        assertNotNull(bomb.getImage(), "L'image de la bombe ne doit pas être nulle.");
    }
}
