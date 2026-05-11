package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActivePowerUpsTest {

    @Test
    void activateMakesActive() {
        ActivePowerUps a = new ActivePowerUps();
        a.activate(PowerUpType.RAPID_FIRE);
        assertTrue(a.isActive(PowerUpType.RAPID_FIRE));
    }

    @Test
    void expiresAfterDuration() {
        ActivePowerUps a = new ActivePowerUps();
        a.activate(PowerUpType.DOUBLE_SHOT);
        for (int i = 0; i < GameConfig.POWERUP_DURATION_TICKS; i++) a.tick();
        assertFalse(a.isActive(PowerUpType.DOUBLE_SHOT));
    }

    @Test
    void fractionDecreasesOverTime() {
        ActivePowerUps a = new ActivePowerUps();
        a.activate(PowerUpType.PIERCING);
        float startFrac = a.fractionLeft(PowerUpType.PIERCING);
        for (int i = 0; i < 60; i++) a.tick();
        assertTrue(a.fractionLeft(PowerUpType.PIERCING) < startFrac);
    }

    @Test
    void clearRemovesAll() {
        ActivePowerUps a = new ActivePowerUps();
        a.activate(PowerUpType.SLOW_MO);
        a.activate(PowerUpType.RAPID_FIRE);
        a.clear();
        assertEquals(0, a.snapshot().size());
    }
}
