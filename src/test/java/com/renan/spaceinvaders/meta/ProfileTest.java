package com.renan.spaceinvaders.meta;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfileTest {

    @Test
    void newProfileStartsEmpty() {
        Profile p = new Profile();
        assertEquals(0, p.getCoins());
        assertEquals(0, p.getTotalCoinsEarned());
        for (Upgrade u : Upgrade.values()) {
            assertEquals(0, p.getLevel(u));
        }
    }

    @Test
    void earnAccumulatesBoth() {
        Profile p = new Profile();
        p.earn(20);
        p.earn(30);
        assertEquals(50, p.getCoins());
        assertEquals(50, p.getTotalCoinsEarned());
    }

    @Test
    void buyDeductsAndIncrementsLevel() {
        Profile p = new Profile();
        p.earn(100);
        assertTrue(p.buy(Upgrade.EXTRA_LIFE));
        assertEquals(1, p.getLevel(Upgrade.EXTRA_LIFE));
        assertEquals(100 - Upgrade.EXTRA_LIFE.costPerLevel, p.getCoins());
    }

    @Test
    void buyFailsWhenNotEnoughCoins() {
        Profile p = new Profile();
        p.earn(10);
        assertFalse(p.buy(Upgrade.EXTRA_LIFE));
        assertEquals(0, p.getLevel(Upgrade.EXTRA_LIFE));
        assertEquals(10, p.getCoins());
    }

    @Test
    void costScalesWithLevel() {
        Profile p = new Profile();
        p.earn(10000);
        assertTrue(p.buy(Upgrade.EXTRA_LIFE));
        int afterFirst = p.getCoins();
        assertTrue(p.buy(Upgrade.EXTRA_LIFE));
        int afterSecond = p.getCoins();
        int firstCost = 10000 - afterFirst;
        int secondCost = afterFirst - afterSecond;
        assertTrue(secondCost > firstCost,
                "second buy should cost more, was " + firstCost + " then " + secondCost);
    }

    @Test
    void cannotExceedMaxLevel() {
        Profile p = new Profile();
        p.earn(100000);
        Upgrade u = Upgrade.EXTRA_LIFE;
        for (int i = 0; i < u.maxLevel; i++) {
            assertTrue(p.buy(u), "buy " + i + " should succeed");
        }
        assertEquals(u.maxLevel, p.getLevel(u));
        assertFalse(p.buy(u), "buy beyond max should fail");
    }

    @Test
    void bonusComputationsScaleWithLevels() {
        Profile p = new Profile();
        p.earn(100000);
        p.buy(Upgrade.EXTRA_LIFE);
        p.buy(Upgrade.DROP_RATE);
        p.buy(Upgrade.COOLDOWN);
        assertEquals(1, p.bonusLives());
        assertEquals(0.05, p.dropChanceBonus(), 1e-6);
        assertEquals(0.92, p.cooldownMultiplier(), 1e-6);
    }
}
