package com.renan.spaceinvaders.core;

import com.renan.spaceinvaders.assets.SoundManager;
import com.renan.spaceinvaders.input.InputHandler;
import com.renan.spaceinvaders.render.GamePanel;
import com.renan.spaceinvaders.world.World;

public final class GameLoop {

    private final World world;
    private final InputHandler input;
    private final GamePanel panel;
    private final SoundManager sounds;
    private final double tickRate;

    private volatile boolean running;
    private Thread thread;

    public GameLoop(World world,
                    InputHandler input,
                    GamePanel panel,
                    SoundManager sounds,
                    double tickRate) {
        this.world = world;
        this.input = input;
        this.panel = panel;
        this.sounds = sounds;
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

            if (updated) panel.repaint();

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }
}
