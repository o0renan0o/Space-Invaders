package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerTest {

    @Test
    void clampsToLeftEdge() {
        Player p = new Player();
        for (int i = 0; i < 5000; i++) p.moveLeft();
        assertEquals(0.0, p.getX());
    }

    @Test
    void clampsToRightEdge() {
        Player p = new Player();
        for (int i = 0; i < 5000; i++) p.moveRight();
        assertEquals(GameConfig.WIDTH - GameConfig.PLAYER_WIDTH, p.getX());
    }

    @Test
    void fireRespectsCooldown() {
        Player p = new Player();
        assertTrue(p.canFire());
        p.fire();
        assertFalse(p.canFire());
        for (int i = 0; i < GameConfig.PLAYER_FIRE_COOLDOWN_TICKS; i++) p.tick();
        assertTrue(p.canFire());
    }

    @Test
    void hitDecrementsLivesOnceWhenVulnerable() {
        Player p = new Player();
        for (int i = 0; i < GameConfig.RESPAWN_INVULN_TICKS; i++) p.tick();
        assertFalse(p.isInvulnerable());
        int lives = p.getLives();
        assertTrue(p.hit());
        assertEquals(lives - 1, p.getLives());
        assertFalse(p.hit(), "consecutive hit while invulnerable must be ignored");
        assertEquals(lives - 1, p.getLives());
    }

    @Test
    void firedBulletStartsAbovePlayer() {
        Player p = new Player();
        Bullet b = p.fire();
        assertEquals(Bullet.Side.PLAYER, b.getSide());
        assertTrue(b.getY() < p.getY());
        assertNotEquals(0, GameConfig.PLAYER_BULLET_SPEED);
    }
}
