package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;

import java.awt.Rectangle;

public final class Boss implements Entity {

    private double x;
    private double y;
    private int direction = 1;
    private int hp;
    private final int maxHp;
    private boolean alive = true;
    private int fireCooldown;
    private int hitFlashTicks;

    public Boss(int waveNumber) {
        this.hp = GameConfig.BOSS_HP_BASE + waveNumber * 5;
        this.maxHp = this.hp;
        this.x = (GameConfig.WIDTH - GameConfig.BOSS_WIDTH) / 2.0;
        this.y = GameConfig.BOSS_Y;
        this.fireCooldown = GameConfig.BOSS_FIRE_INTERVAL;
    }

    public void update() {
        x += direction * GameConfig.BOSS_SPEED;
        if (x <= 0) {
            x = 0;
            direction = 1;
        } else if (x + GameConfig.BOSS_WIDTH >= GameConfig.WIDTH) {
            x = GameConfig.WIDTH - GameConfig.BOSS_WIDTH;
            direction = -1;
        }
        if (fireCooldown > 0) fireCooldown--;
        if (hitFlashTicks > 0) hitFlashTicks--;
    }

    public boolean readyToFire() {
        if (fireCooldown <= 0) {
            fireCooldown = GameConfig.BOSS_FIRE_INTERVAL;
            return true;
        }
        return false;
    }

    public boolean hit(int amount) {
        hp -= amount;
        hitFlashTicks = 4;
        if (hp <= 0) {
            alive = false;
            return true;
        }
        return false;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean isFlashing() {
        return hitFlashTicks > 0;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, GameConfig.BOSS_WIDTH, GameConfig.BOSS_HEIGHT);
    }

    @Override
    public boolean isAlive() {
        return alive;
    }
}
