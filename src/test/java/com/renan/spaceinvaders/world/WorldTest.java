package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;
import com.renan.spaceinvaders.core.GameState;
import com.renan.spaceinvaders.core.Phases;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorldTest {

    @Test
    void newWorldStartsInMenu() {
        World world = new World(0);
        assertEquals(GameState.MENU, world.getState());
    }

    @Test
    void startNewGamePopulatesAliensAndShields() {
        World world = new World(0);
        world.startNewGame();
        assertEquals(GameState.PLAYING, world.getState());
        var phase = Phases.LIST.get(0);
        assertEquals(phase.rows() * phase.cols(), world.getAliens().size());
        assertFalse(world.getShields().isEmpty());
    }

    @Test
    void scoreThresholdsAwardLives() {
        World world = new World(0);
        world.startNewGame();
        int beforeLives = world.getPlayer().getLives();
        world.addScore(GameConfig.SCORE_REWARD_THRESHOLDS[0]);
        assertEquals(Math.min(GameConfig.PLAYER_MAX_LIVES, beforeLives + 1),
                world.getPlayer().getLives());
    }

    @Test
    void comboMultiplierTracksScore() {
        World world = new World(0);
        world.startNewGame();
        assertEquals(1, world.getCombo().getMultiplier());
        world.getCombo().registerKill();
        assertEquals(1, world.getCombo().getMultiplier(),
                "first kill keeps multiplier at 1");
        world.getCombo().registerKill();
        assertEquals(2, world.getCombo().getMultiplier());
    }

    @Test
    void waveAdvancesPhase() {
        World world = new World(0);
        world.startNewGame();
        assertEquals(1, world.getWave());
        assertEquals(Phases.LIST.get(0).name(), world.getCurrentPhase().name());
    }

    @Test
    void cameraShakeCanBeTriggered() {
        World world = new World(0);
        world.startNewGame();
        world.getCameraShake().shake(10, 8);
        assertTrue(world.getCameraShake().isActive());
    }
}
