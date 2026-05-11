package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;

import java.awt.Rectangle;

public final class Alien implements Entity {

    private final int row;
    private final int col;
    private final AlienType type;
    private double x;
    private double y;
    private int hp;
    private boolean alive = true;
    private int animFrame;
    private boolean diving;
    private double diveVx;
    private double diveVy;

    public Alien(int row, int col, double x, double y, AlienType type) {
        this.row = row;
        this.col = col;
        this.x = x;
        this.y = y;
        this.type = type;
        this.hp = type.hp;
    }

    public void move(double dx, double dy) {
        x += dx;
        y += dy;
    }

    public void toggleFrame() {
        animFrame = (animFrame + 1) % 2;
    }

    public boolean hit() {
        hp--;
        if (hp <= 0) {
            alive = false;
            return true;
        }
        return false;
    }

    public void startDive(double vx, double vy) {
        diving = true;
        diveVx = vx;
        diveVy = vy;
    }

    public void diveStep() {
        if (!diving) return;
        x += diveVx;
        y += diveVy;
        if (y > GameConfig.HEIGHT) alive = false;
    }

    public boolean isDiving() {
        return diving;
    }

    public AlienType getType() {
        return type;
    }

    public int scoreValue() {
        return type.score;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getAnimFrame() {
        return animFrame;
    }

    public int getHp() {
        return hp;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, GameConfig.ALIEN_WIDTH, GameConfig.ALIEN_HEIGHT);
    }

    @Override
    public boolean isAlive() {
        return alive;
    }
}
