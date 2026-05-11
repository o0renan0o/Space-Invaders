package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerTest {

    @Test
    void clampsToLeftEdge() {
        Player p = new Player(3);
        for (int i = 0; i < 5000; i++) p.moveLeft();
        assertEquals(0.0, p.getX());
    }

    @Test
    void clampsToRightEdge() {
        Player p = new Player(3);
        for (int i = 0; i < 5000; i++) p.moveRight();
        assertEquals(GameConfig.WIDTH - GameConfig.PLAYER_WIDTH, p.getX());
    }

    @Test
    void fireRespectsCooldown() {
        Player p = new Player(3);
        ActivePowerUps active = new ActivePowerUps();
        assertTrue(p.canFire());
        p.fire(active);
        assertFalse(p.canFire());
        for (int i = 0; i < GameConfig.PLAYER_FIRE_COOLDOWN_TICKS; i++) p.tick();
        assertTrue(p.canFire());
    }

    @Test
    void rapidFireShortensCooldown() {
        Player p = new Player(3);
        ActivePowerUps active = new ActivePowerUps();
        active.activate(PowerUpType.RAPID_FIRE);
        p.fire(active);
        for (int i = 0; i < GameConfig.RAPID_FIRE_COOLDOWN; i++) p.tick();
        assertTrue(p.canFire());
    }

    @Test
    void doubleShotProducesTwoBullets() {
        Player p = new Player(3);
        ActivePowerUps active = new ActivePowerUps();
        active.activate(PowerUpType.DOUBLE_SHOT);
        List<Bullet> bullets = p.fire(active);
        assertEquals(2, bullets.size());
    }

    @Test
    void piercingFlagPropagates() {
        Player p = new Player(3);
        ActivePowerUps active = new ActivePowerUps();
        active.activate(PowerUpType.PIERCING);
        Bullet b = p.fire(active).get(0);
        assertTrue(b.isPiercing());
    }

    @Test
    void hitDecrementsLivesOnceWhenVulnerable() {
        Player p = new Player(3);
        for (int i = 0; i < GameConfig.RESPAWN_INVULN_TICKS; i++) p.tick();
        assertFalse(p.isInvulnerable());
        int lives = p.getLives();
        assertTrue(p.hit());
        assertEquals(lives - 1, p.getLives());
        assertFalse(p.hit(), "consecutive hit while invulnerable must be ignored");
        assertEquals(lives - 1, p.getLives());
    }

    @Test
    void addLifeRespectsMax() {
        Player p = new Player(GameConfig.PLAYER_MAX_LIVES);
        p.addLife();
        assertEquals(GameConfig.PLAYER_MAX_LIVES, p.getLives());
    }
}
