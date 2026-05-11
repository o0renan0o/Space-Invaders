package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.assets.SoundManager;
import com.renan.spaceinvaders.core.GameState;
import com.renan.spaceinvaders.input.InputHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class WorldSmokeTest {

    @Test
    void runsManyTicksWithoutThrowing() {
        World world = new World(0);
        world.startNewGame();
        SoundManager sounds = new SoundManager();
        InputHandler input = new InputHandler();
        for (int i = 0; i < 600; i++) {
            world.update(input, sounds);
            if (world.getState() == GameState.GAME_OVER) break;
        }
        assertNotNull(world.getState());
    }

    @Test
    void menuToGameToPauseTransitions() {
        World world = new World(0);
        SoundManager sounds = new SoundManager();
        InputHandler input = new InputHandler();

        assertNotNull(world.getState());

        world.startNewGame();
        for (int i = 0; i < 10; i++) world.update(input, sounds);

        world.setState(GameState.PAUSED);
        for (int i = 0; i < 5; i++) world.update(input, sounds);
    }
}
