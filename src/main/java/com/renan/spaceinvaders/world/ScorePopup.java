package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;

import java.awt.Color;

public final class ScorePopup {

    private final int x;
    private final int startY;
    private final String text;
    private final Color color;
    private int life;

    public ScorePopup(int x, int y, String text, Color color) {
        this.x = x;
        this.startY = y;
        this.text = text;
        this.color = color;
        this.life = GameConfig.POPUP_LIFETIME;
    }

    public void update() {
        if (life > 0) life--;
    }

    public boolean isAlive() {
        return life > 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        double progress = 1.0 - (life / (double) GameConfig.POPUP_LIFETIME);
        return startY - (int) (progress * GameConfig.POPUP_RISE_PX);
    }

    public String getText() {
        return text;
    }

    public Color getColor() {
        return color;
    }

    public float getAlpha() {
        return Math.max(0f, life / (float) GameConfig.POPUP_LIFETIME);
    }
}
