package com.renan.spaceinvaders.core;

public enum Difficulty {
    EASY("EASY", 4, 0.85, 1.30, 0.18),
    NORMAL("NORMAL", 3, 1.0, 1.0, 0.12),
    HARD("HARD", 2, 1.20, 0.75, 0.08);

    public final String label;
    public final int startingLives;
    public final double speedMultiplier;
    public final double fireDelayMultiplier;
    public final double powerUpDropChance;

    Difficulty(String label,
               int startingLives,
               double speedMultiplier,
               double fireDelayMultiplier,
               double powerUpDropChance) {
        this.label = label;
        this.startingLives = startingLives;
        this.speedMultiplier = speedMultiplier;
        this.fireDelayMultiplier = fireDelayMultiplier;
        this.powerUpDropChance = powerUpDropChance;
    }

    public Difficulty next() {
        Difficulty[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}
