package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;

import java.awt.Rectangle;

public final class Bullet implements Entity {

    public enum Side {PLAYER, ALIEN}

    private double x;
    private double y;
    private final double vy;
    private final Side side;
    private boolean alive = true;

    public Bullet(double x, double y, double vy, Side side) {
        this.x = x;
        this.y = y;
        this.vy = vy;
        this.side = side;
    }

    public void update() {
        if (!alive) return;
        y += vy;
        if (y < -GameConfig.BULLET_HEIGHT || y > GameConfig.HEIGHT + GameConfig.BULLET_HEIGHT) {
            alive = false;
        }
    }

    public void kill() {
        alive = false;
    }

    public Side getSide() {
        return side;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, GameConfig.BULLET_WIDTH, GameConfig.BULLET_HEIGHT);
    }

    @Override
    public boolean isAlive() {
        return alive;
    }
}
