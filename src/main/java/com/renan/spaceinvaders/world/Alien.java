package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;

import java.awt.Rectangle;

public final class Alien implements Entity {

    private final int row;
    private final int col;
    private double x;
    private double y;
    private boolean alive = true;
    private int animFrame;

    public Alien(int row, int col, double x, double y) {
        this.row = row;
        this.col = col;
        this.x = x;
        this.y = y;
    }

    public void move(double dx, double dy) {
        x += dx;
        y += dy;
    }

    public void toggleFrame() {
        animFrame = (animFrame + 1) % 2;
    }

    public void kill() {
        alive = false;
    }

    public int scoreValue() {
        if (row == 0) return GameConfig.SCORE_PER_ALIEN_ROW_0;
        if (row == 1) return GameConfig.SCORE_PER_ALIEN_ROW_1;
        return GameConfig.SCORE_PER_ALIEN_ROW_DEFAULT;
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

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, GameConfig.ALIEN_WIDTH, GameConfig.ALIEN_HEIGHT);
    }

    @Override
    public boolean isAlive() {
        return alive;
    }
}
