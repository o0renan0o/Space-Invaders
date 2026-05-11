package com.renan.spaceinvaders.world;

import java.util.Random;

public final class CameraShake {

    private int ticks;
    private int magnitude;
    private final Random rng = new Random();

    public void shake(int durationTicks, int magnitude) {
        if (durationTicks > this.ticks) {
            this.ticks = durationTicks;
            this.magnitude = magnitude;
        }
    }

    public void tick() {
        if (ticks > 0) ticks--;
    }

    public int offsetX() {
        if (ticks <= 0) return 0;
        return rng.nextInt(magnitude * 2 + 1) - magnitude;
    }

    public int offsetY() {
        if (ticks <= 0) return 0;
        return rng.nextInt(magnitude * 2 + 1) - magnitude;
    }

    public boolean isActive() {
        return ticks > 0;
    }
}
