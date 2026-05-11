package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PowerUpEffectsTest {

    @Test
    void damageUpDoublesBulletDamage() {
        Player p = new Player();
        ActivePowerUps active = new ActivePowerUps();
        active.activate(PowerUpType.DAMAGE_UP);
        List<Bullet> bullets = p.fire(active);
        assertFalse(bullets.isEmpty());
        for (Bullet b : bullets) {
            assertEquals(2, b.getDamage(), "DAMAGE_UP should double bullet damage");
        }
    }

    @Test
    void doubleAndDamageStack() {
        Player p = new Player();
        ActivePowerUps active = new ActivePowerUps();
        active.activate(PowerUpType.DOUBLE_SHOT);
        active.activate(PowerUpType.DAMAGE_UP);
        List<Bullet> bullets = p.fire(active);
        assertEquals(2, bullets.size());
        for (Bullet b : bullets) assertEquals(2, b.getDamage());
    }

    @Test
    void cooldownMultiplierShrinksFireDelay() {
        Player slow = new Player();
        slow.setCooldownMultiplier(1.0);
        slow.fire(new ActivePowerUps());
        assertFalse(slow.canFire(), "should be on cooldown after firing");

        Player fast = new Player();
        fast.setCooldownMultiplier(0.6);
        fast.fire(new ActivePowerUps());
        for (int i = 0; i < (int) Math.round(GameConfig.PLAYER_FIRE_COOLDOWN_TICKS * 0.6); i++) {
            fast.tick();
        }
        assertTrue(fast.canFire(), "fast cooldown should expire sooner");
    }

    @Test
    void magnetPullsPowerUpTowardPlayer() {
        PowerUp pu = new PowerUp(100, 100, PowerUpType.RAPID_FIRE);
        double initialDist = Math.hypot(400 - 100, 500 - 100);
        for (int i = 0; i < 5; i++) pu.update(400, 500, true);
        double finalDist = Math.hypot(400 - pu.getX(), 500 - pu.getY());
        assertTrue(finalDist < initialDist, "magnet should reduce distance");
    }

    @Test
    void noMagnetMakesPowerUpFallNormally() {
        PowerUp pu = new PowerUp(100, 100, PowerUpType.RAPID_FIRE);
        pu.update(400, 500, false);
        assertEquals(100, pu.getX(), 0.001, "x should not change without magnet");
        assertTrue(pu.getY() > 100, "should fall vertically");
    }
}
