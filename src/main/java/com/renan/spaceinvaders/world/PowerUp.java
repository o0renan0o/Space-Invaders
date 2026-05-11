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
        update(0, 0, false);
    }

    public void update(double playerCx, double playerCy, boolean magnetActive) {
        spinTick++;
        if (magnetActive) {
            double cx = x + GameConfig.POWERUP_SIZE / 2.0;
            double cy = y + GameConfig.POWERUP_SIZE / 2.0;
            double dx = playerCx - cx;
            double dy = playerCy - cy;
            double dist = Math.hypot(dx, dy);
            if (dist > 1) {
                double pull = GameConfig.MAGNET_PULL_SPEED;
                x += dx / dist * pull;
                y += dy / dist * pull;
            }
        } else {
            y += GameConfig.POWERUP_FALL_SPEED;
        }
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
