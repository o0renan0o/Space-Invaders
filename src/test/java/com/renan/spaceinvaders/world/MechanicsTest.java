package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MechanicsTest {

    @Test
    void chargedBulletIsBiggerPiercingAndStronger() {
        Player p = new Player(3);
        Bullet b = p.fireCharged();
        assertTrue(b.isPiercing(), "charge shot must pierce");
        assertEquals(GameConfig.CHARGE_BULLET_DAMAGE, b.getDamage());
        assertEquals(GameConfig.CHARGE_BULLET_WIDTH, b.getWidth());
        assertEquals(GameConfig.CHARGE_BULLET_HEIGHT, b.getHeight());
    }

    @Test
    void chargedBulletKillsArmoredInOneHit() {
        Alien armored = new Alien(0, 0, 100, 100, AlienType.ARMORED);
        Bullet b = new Bullet(105, 105, -1, Bullet.Side.PLAYER)
                .withDamage(GameConfig.CHARGE_BULLET_DAMAGE);
        assertTrue(armored.getBounds().intersects(b.getBounds()));
        boolean killed = armored.hit(b.getDamage());
        assertTrue(killed, "charge shot must kill armored in one hit");
    }

    @Test
    void regularBulletDoesNotKillArmoredInOneHit() {
        Alien armored = new Alien(0, 0, 100, 100, AlienType.ARMORED);
        Bullet b = new Bullet(105, 105, -1, Bullet.Side.PLAYER);
        assertFalse(armored.hit(b.getDamage()));
        assertTrue(armored.isAlive());
    }

    @Test
    void nearMissCreditTracking() {
        Bullet b = new Bullet(100, 100, 1, Bullet.Side.ALIEN);
        assertFalse(b.isNearMissCredited());
        b.creditNearMiss();
        assertTrue(b.isNearMissCredited());
    }

    @Test
    void bossPatternChangesWithHp() {
        Boss boss = new Boss(1);
        assertEquals(1, boss.pattern());
        int maxHp = boss.getMaxHp();
        for (int i = 0; i < maxHp / 2; i++) boss.hit(1);
        assertTrue(boss.pattern() >= 2);
        while (boss.getHp() > maxHp * GameConfig.BOSS_PATTERN_3_HP_FRACTION) {
            boss.hit(1);
            if (!boss.isAlive()) break;
        }
        if (boss.isAlive()) assertEquals(3, boss.pattern());
    }

    @Test
    void bossFireIntervalShrinksInFinalPhase() {
        Boss boss = new Boss(1);
        int p1Interval = boss.fireInterval();
        int maxHp = boss.getMaxHp();
        for (int i = 0; i < maxHp * 0.7; i++) boss.hit(1);
        if (boss.isAlive() && boss.pattern() == 3) {
            assertTrue(boss.fireInterval() < p1Interval,
                    "phase 3 must fire faster");
        }
    }
}
