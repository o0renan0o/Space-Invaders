package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;

import java.awt.Rectangle;

public final class Player implements Entity {

    private double x;
    private final double y;
    private int lives;
    private int fireCooldown;
    private int invulnTicks;

    public Player() {
        reset();
        this.y = GameConfig.PLAYER_Y;
    }

    public void reset() {
        this.x = (GameConfig.WIDTH - GameConfig.PLAYER_WIDTH) / 2.0;
        this.lives = GameConfig.PLAYER_LIVES;
        this.fireCooldown = 0;
        this.invulnTicks = GameConfig.RESPAWN_INVULN_TICKS;
    }

    public void respawn() {
        this.x = (GameConfig.WIDTH - GameConfig.PLAYER_WIDTH) / 2.0;
        this.fireCooldown = 0;
        this.invulnTicks = GameConfig.RESPAWN_INVULN_TICKS;
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

    public Bullet fire() {
        fireCooldown = GameConfig.PLAYER_FIRE_COOLDOWN_TICKS;
        double bx = x + (GameConfig.PLAYER_WIDTH - GameConfig.BULLET_WIDTH) / 2.0;
        double by = y - GameConfig.BULLET_HEIGHT;
        return new Bullet(bx, by, -GameConfig.PLAYER_BULLET_SPEED, Bullet.Side.PLAYER);
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
