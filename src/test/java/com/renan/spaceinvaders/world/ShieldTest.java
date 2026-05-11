package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;
import org.junit.jupiter.api.Test;

import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShieldTest {

    @Test
    void bulletHitErodesCells() {
        Shield shield = new Shield(100, 400);
        int aliveBefore = countAlive(shield);
        Rectangle bullet = new Rectangle(
                shield.getX() + shield.widthPx() / 2,
                shield.getY() + GameConfig.SHIELD_CELL * 2,
                GameConfig.BULLET_WIDTH,
                GameConfig.BULLET_HEIGHT);
        assertTrue(shield.handleHit(bullet, Bullet.Side.PLAYER));
        int aliveAfter = countAlive(shield);
        assertTrue(aliveAfter < aliveBefore);
    }

    @Test
    void missedBulletReturnsFalse() {
        Shield shield = new Shield(100, 400);
        Rectangle bullet = new Rectangle(0, 0, 2, 10);
        assertFalse(shield.handleHit(bullet, Bullet.Side.PLAYER));
    }

    private int countAlive(Shield shield) {
        int n = 0;
        for (boolean[] row : shield.getCells()) {
            for (boolean cell : row) if (cell) n++;
        }
        return n;
    }
}
