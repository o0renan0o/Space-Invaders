package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComboTest {

    @Test
    void firstKillStartsAtOne() {
        Combo c = new Combo();
        c.registerKill();
        assertEquals(1, c.getMultiplier());
    }

    @Test
    void secondKillDoublesMultiplier() {
        Combo c = new Combo();
        c.registerKill();
        c.registerKill();
        assertEquals(2, c.getMultiplier());
    }

    @Test
    void doesNotExceedMax() {
        Combo c = new Combo();
        for (int i = 0; i < 20; i++) c.registerKill();
        assertEquals(GameConfig.COMBO_MAX_MULTIPLIER, c.getMultiplier());
    }

    @Test
    void timeoutResetsCombo() {
        Combo c = new Combo();
        c.registerKill();
        c.registerKill();
        for (int i = 0; i < GameConfig.COMBO_WINDOW_TICKS + 1; i++) c.tick();
        assertEquals(1, c.getMultiplier());
    }

    @Test
    void manualBreakResets() {
        Combo c = new Combo();
        c.registerKill();
        c.registerKill();
        c.breakCombo();
        assertEquals(1, c.getMultiplier());
    }

    @Test
    void windowFractionStartsAtOne() {
        Combo c = new Combo();
        c.registerKill();
        assertTrue(c.getWindowFraction() > 0.99f);
    }
}
