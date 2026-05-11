package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;

public final class Combo {

    private int multiplier = 1;
    private int ticksLeft;

    public void registerKill() {
        if (ticksLeft <= 0) {
            multiplier = 1;
        } else if (multiplier < GameConfig.COMBO_MAX_MULTIPLIER) {
            multiplier = Math.min(GameConfig.COMBO_MAX_MULTIPLIER, multiplier * 2);
        }
        ticksLeft = GameConfig.COMBO_WINDOW_TICKS;
    }

    public void registerKill(int extraTicks) {
        registerKill();
        ticksLeft += extraTicks;
    }

    public void tick() {
        if (ticksLeft > 0) {
            ticksLeft--;
            if (ticksLeft == 0) multiplier = 1;
        }
    }

    public void breakCombo() {
        multiplier = 1;
        ticksLeft = 0;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public int getTicksLeft() {
        return ticksLeft;
    }

    public float getWindowFraction() {
        return Math.max(0f, ticksLeft / (float) GameConfig.COMBO_WINDOW_TICKS);
    }
}
