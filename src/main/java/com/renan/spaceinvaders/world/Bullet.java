package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;

import java.awt.Rectangle;

public final class Bullet implements Entity {

    public enum Side {PLAYER, ALIEN}

    private double x;
    private double y;
    private double vx;
    private final double vy;
    private final Side side;
    private boolean alive = true;
    private boolean piercing;
    private boolean splittable;

    public Bullet(double x, double y, double vy, Side side) {
        this(x, y, 0, vy, side);
    }

    public Bullet(double x, double y, double vx, double vy, Side side) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.side = side;
    }

    public void update() {
        if (!alive) return;
        x += vx;
        y += vy;
        if (y < -GameConfig.BULLET_HEIGHT || y > GameConfig.HEIGHT + GameConfig.BULLET_HEIGHT
                || x < -GameConfig.BULLET_WIDTH || x > GameConfig.WIDTH) {
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

    public double getVy() {
        return vy;
    }

    public double getVx() {
        return vx;
    }

    public boolean isPiercing() {
        return piercing;
    }

    public Bullet piercing() {
        this.piercing = true;
        return this;
    }

    public boolean isSplittable() {
        return splittable;
    }

    public Bullet splittable() {
        this.splittable = true;
        return this;
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
