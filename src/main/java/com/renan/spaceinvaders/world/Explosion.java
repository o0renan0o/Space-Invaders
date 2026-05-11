package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;

public final class Explosion {

    private final int x;
    private final int y;
    private int ticks;

    public Explosion(int x, int y) {
        this.x = x;
        this.y = y;
        this.ticks = GameConfig.EXPLOSION_TICKS;
    }

    public void tick() {
        if (ticks > 0) ticks--;
    }

    public boolean isAlive() {
        return ticks > 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTicks() {
        return ticks;
    }
}
