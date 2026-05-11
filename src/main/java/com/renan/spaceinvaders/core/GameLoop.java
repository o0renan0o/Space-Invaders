package com.renan.spaceinvaders.core;

import com.renan.spaceinvaders.assets.SoundManager;
import com.renan.spaceinvaders.input.InputHandler;
import com.renan.spaceinvaders.render.GamePanel;
import com.renan.spaceinvaders.ui.HighScoreStore;
import com.renan.spaceinvaders.world.World;

public final class GameLoop {

    private final World world;
    private final InputHandler input;
    private final GamePanel panel;
    private final SoundManager sounds;
    private final HighScoreStore highScores;
    private final double tickRate;

    private volatile boolean running;
    private Thread thread;

    public GameLoop(World world,
                    InputHandler input,
                    GamePanel panel,
                    SoundManager sounds,
                    HighScoreStore highScores,
                    double tickRate) {
        this.world = world;
        this.input = input;
        this.panel = panel;
        this.sounds = sounds;
        this.highScores = highScores;
        this.tickRate = tickRate;
    }

    public void start() {
        if (running) return;
        running = true;
        thread = new Thread(this::loop, "game-loop");
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        running = false;
    }

    private void loop() {
        final double nsPerTick = 1_000_000_000.0 / tickRate;
        long last = System.nanoTime();
        double delta = 0;
        int previousHighScore = highScores.load();

        while (running) {
            long now = System.nanoTime();
            delta += (now - last) / nsPerTick;
            last = now;

            boolean updated = false;
            while (delta >= 1) {
                world.update(input, sounds);
                delta--;
                updated = true;
                if (delta > 8) {
                    delta = 0;
                    break;
                }
            }

            if (updated) {
                if (world.getScore() > previousHighScore) {
                    previousHighScore = world.getScore();
                    highScores.save(previousHighScore);
                    world.setHighScore(previousHighScore);
                }
                panel.repaint();
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }
}
