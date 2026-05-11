package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;
import com.renan.spaceinvaders.core.GameState;
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
        assertEquals(GameConfig.ALIEN_ROWS * GameConfig.ALIEN_COLS, world.getAliens().size());
        assertFalse(world.getShields().isEmpty());
    }

    @Test
    void scoreUpdatesHighScore() {
        World world = new World(50);
        world.startNewGame();
        world.addScore(100);
        assertEquals(100, world.getScore());
        assertTrue(world.getHighScore() >= 100);
    }

    @Test
    void scoreDoesNotLowerHighScore() {
        World world = new World(500);
        world.startNewGame();
        world.addScore(10);
        assertEquals(500, world.getHighScore());
    }
}
