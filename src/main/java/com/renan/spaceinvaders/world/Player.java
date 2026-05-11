package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public final class Player implements Entity {

    private double x;
    private final double y;
    private int lives;
    private int fireCooldown;
    private int invulnTicks;
    private int initialLives;

    public Player() {
        this(GameConfig.PLAYER_LIVES);
    }

    public Player(int startingLives) {
        this.initialLives = startingLives;
        this.y = GameConfig.PLAYER_Y;
        reset();
    }

    public void reset() {
        this.x = (GameConfig.WIDTH - GameConfig.PLAYER_WIDTH) / 2.0;
        this.lives = initialLives;
        this.fireCooldown = 0;
        this.invulnTicks = GameConfig.RESPAWN_INVULN_TICKS;
    }

    public void setStartingLives(int lives) {
        this.initialLives = lives;
    }

    public void moveLeft() {
        x -= GameConfig.PLAYER_SPEED;
        if (x < 0) x = 0;
    }

    public void moveRight() {
        x += GameConfig.PLAYER_SPEED;
        if (x > GameConfig.WIDTH - GameConfig.PLAYER_WIDTH) {
            x = GameConfig.WIDTH - GameConfig.PLAYER_WIDTH;
        }
    }

    public boolean canFire() {
        return fireCooldown <= 0;
    }

    public List<Bullet> fire(ActivePowerUps active) {
        fireCooldown = active.isActive(PowerUpType.RAPID_FIRE)
                ? GameConfig.RAPID_FIRE_COOLDOWN
                : GameConfig.PLAYER_FIRE_COOLDOWN_TICKS;
        List<Bullet> out = new ArrayList<>();
        double by = y - GameConfig.BULLET_HEIGHT;
        boolean piercing = active.isActive(PowerUpType.PIERCING);
        if (active.isActive(PowerUpType.DOUBLE_SHOT)) {
            double left = x + 8;
            double right = x + GameConfig.PLAYER_WIDTH - 8 - GameConfig.BULLET_WIDTH;
            Bullet b1 = new Bullet(left, by, -GameConfig.PLAYER_BULLET_SPEED, Bullet.Side.PLAYER);
            Bullet b2 = new Bullet(right, by, -GameConfig.PLAYER_BULLET_SPEED, Bullet.Side.PLAYER);
            if (piercing) {
                b1.piercing();
                b2.piercing();
            }
            out.add(b1);
            out.add(b2);
        } else {
            double bx = x + (GameConfig.PLAYER_WIDTH - GameConfig.BULLET_WIDTH) / 2.0;
            Bullet b = new Bullet(bx, by, -GameConfig.PLAYER_BULLET_SPEED, Bullet.Side.PLAYER);
            if (piercing) b.piercing();
            out.add(b);
        }
        return out;
    }

    public void tick() {
        if (fireCooldown > 0) fireCooldown--;
        if (invulnTicks > 0) invulnTicks--;
    }

    public boolean hit() {
        if (invulnTicks > 0) return false;
        lives--;
        invulnTicks = GameConfig.RESPAWN_INVULN_TICKS;
        return true;
    }

    public boolean isInvulnerable() {
        return invulnTicks > 0;
    }

    public int getLives() {
        return lives;
    }

    public void addLife() {
        if (lives < GameConfig.PLAYER_MAX_LIVES) lives++;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, GameConfig.PLAYER_WIDTH, GameConfig.PLAYER_HEIGHT);
    }

    @Override
    public boolean isAlive() {
        return lives > 0;
    }
}
