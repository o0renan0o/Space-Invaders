package com.renan.spaceinvaders.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PhasesTest {

    @Test
    void firstPhaseIsPatrulha() {
        Phase p = Phases.forWave(1);
        assertEquals(1, p.number());
        assertEquals("PATRULHA", p.name());
    }

    @Test
    void waveBeyondListLoopsAndScalesSpeed() {
        Phase last = Phases.LIST.get(Phases.LIST.size() - 1);
        Phase endless = Phases.forWave(Phases.LIST.size() + 1);
        assertTrue(endless.speedMultiplier() > last.speedMultiplier());
        assertTrue(endless.name().startsWith("ENDLESS"));
    }

    @Test
    void wave5HasBoss() {
        Phase p = Phases.forWave(5);
        assertTrue(p.has(Mechanic.BOSS));
    }

    @Test
    void wave10HasAllToughMechanics() {
        Phase p = Phases.forWave(10);
        assertTrue(p.has(Mechanic.DIVERS));
        assertTrue(p.has(Mechanic.SPLITTING_BULLETS));
        assertTrue(p.has(Mechanic.ARMORED_FRONT_ROWS));
        assertTrue(p.has(Mechanic.BOSS));
    }

    @Test
    void clearBonusIncreasesWithPhase() {
        for (int i = 1; i < Phases.LIST.size(); i++) {
            assertTrue(Phases.LIST.get(i).clearBonus()
                    >= Phases.LIST.get(i - 1).clearBonus());
        }
    }

    @Test
    void mechanicsContainsCheck() {
        Phase p = Phases.forWave(7);
        assertNotNull(p.mechanics());
        assertTrue(p.has(Mechanic.ARMORED_FRONT_ROWS));
    }
}
