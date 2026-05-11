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
    private boolean nearMissCredited;
    private int damage = 1;
    private int width = GameConfig.BULLET_WIDTH;
    private int height = GameConfig.BULLET_HEIGHT;

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
        if (y < -height || y > GameConfig.HEIGHT + height
                || x < -width || x > GameConfig.WIDTH) {
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

    public int getDamage() {
        return damage;
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

    public boolean isNearMissCredited() {
        return nearMissCredited;
    }

    public void creditNearMiss() {
        this.nearMissCredited = true;
    }

    public Bullet withDamage(int dmg) {
        this.damage = dmg;
        return this;
    }

    public Bullet withSize(int w, int h) {
        this.width = w;
        this.height = h;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    @Override
    public boolean isAlive() {
        return alive;
    }
}
