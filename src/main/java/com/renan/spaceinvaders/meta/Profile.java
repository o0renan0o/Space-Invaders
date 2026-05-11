package com.renan.spaceinvaders.meta;

import java.util.EnumMap;
import java.util.Map;

public final class Profile {

    private int coins;
    private int totalCoinsEarned;
    private final Map<Upgrade, Integer> levels = new EnumMap<>(Upgrade.class);

    public int getCoins() { return coins; }
    public int getTotalCoinsEarned() { return totalCoinsEarned; }

    public int getLevel(Upgrade u) {
        return levels.getOrDefault(u, 0);
    }

    public boolean canAfford(Upgrade u) {
        int level = getLevel(u);
        if (level >= u.maxLevel) return false;
        return coins >= u.costAtLevel(level);
    }

    public boolean buy(Upgrade u) {
        if (!canAfford(u)) return false;
        int level = getLevel(u);
        coins -= u.costAtLevel(level);
        levels.put(u, level + 1);
        return true;
    }

    public void earn(int amount) {
        if (amount <= 0) return;
        coins += amount;
        totalCoinsEarned += amount;
    }

    public int bonusLives() {
        return getLevel(Upgrade.EXTRA_LIFE);
    }

    public double dropChanceBonus() {
        return getLevel(Upgrade.DROP_RATE) * 0.05;
    }

    public double cooldownMultiplier() {
        return Math.max(0.6, 1.0 - getLevel(Upgrade.COOLDOWN) * 0.08);
    }

    public int bonusShields() {
        return getLevel(Upgrade.SHIELD_REINFORCE);
    }

    void setCoins(int c) { this.coins = Math.max(0, c); }
    void setTotalCoinsEarned(int c) { this.totalCoinsEarned = Math.max(0, c); }
    void setLevel(Upgrade u, int level) {
        levels.put(u, Math.max(0, Math.min(u.maxLevel, level)));
    }
}
