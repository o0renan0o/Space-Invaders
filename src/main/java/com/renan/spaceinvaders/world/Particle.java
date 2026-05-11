package com.renan.spaceinvaders.world;

import java.awt.Color;

public final class Particle {

    private double x;
    private double y;
    private double vx;
    private double vy;
    private int life;
    private final int maxLife;
    private final Color color;

    public Particle(double x, double y, double vx, double vy, int life, Color color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.life = life;
        this.maxLife = life;
        this.color = color;
    }

    public void update() {
        x += vx;
        y += vy;
        vy += 0.08;
        vx *= 0.98;
        life--;
    }

    public boolean isAlive() {
        return life > 0;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getLife() {
        return life;
    }

    public int getMaxLife() {
        return maxLife;
    }

    public Color getColor() {
        return color;
    }
}
