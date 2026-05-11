package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;

import java.awt.Rectangle;

public final class Ufo implements Entity {

    private double x;
    private final double y;
    private final double vx;
    private boolean alive = true;

    public Ufo(boolean fromLeft) {
        this.y = GameConfig.UFO_Y;
        if (fromLeft) {
            this.x = -GameConfig.UFO_WIDTH;
            this.vx = GameConfig.UFO_SPEED;
        } else {
            this.x = GameConfig.WIDTH;
            this.vx = -GameConfig.UFO_SPEED;
        }
    }

    public void update() {
        x += vx;
        if (vx > 0 && x > GameConfig.WIDTH) alive = false;
        if (vx < 0 && x + GameConfig.UFO_WIDTH < 0) alive = false;
    }

    public void kill() {
        alive = false;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, GameConfig.UFO_WIDTH, GameConfig.UFO_HEIGHT);
    }

    @Override
    public boolean isAlive() {
        return alive;
    }
}
