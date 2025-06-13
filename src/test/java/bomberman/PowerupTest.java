package bomberman;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class PowerupTest {

    @Test
    public void testCoordonneesCorrectes() {
        Powerup powerup = new Powerup(2, 7, "shield");
        assertEquals(2, powerup.getGridX());
        assertEquals(7, powerup.getGridY());
    }



    @Test
    public void testImageNonNullePourShield() {
        Powerup powerup = new Powerup(1, 1, "shield");
        assertNotNull(powerup.getImage(), "L'image du power-up 'shield' ne doit pas être nulle.");
    }

    @Test
    public void testImageNonNullePourCooldown() {
        Powerup powerup = new Powerup(2, 2, "cooldown");
        assertNotNull(powerup.getImage(), "L'image du power-up 'cooldown' ne doit pas être nulle.");
    }
}
