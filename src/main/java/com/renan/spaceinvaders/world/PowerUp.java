package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;

import java.awt.Rectangle;

public final class PowerUp implements Entity {

    private double x;
    private double y;
    private final PowerUpType type;
    private boolean alive = true;
    private int spinTick;

    public PowerUp(double x, double y, PowerUpType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void update() {
        y += GameConfig.POWERUP_FALL_SPEED;
        spinTick++;
        if (y > GameConfig.HEIGHT) alive = false;
    }

    public void collect() {
        alive = false;
    }

    public PowerUpType getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getSpinTick() {
        return spinTick;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, GameConfig.POWERUP_SIZE, GameConfig.POWERUP_SIZE);
    }

    @Override
    public boolean isAlive() {
        return alive;
    }
}
