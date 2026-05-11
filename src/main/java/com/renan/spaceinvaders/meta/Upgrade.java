package com.renan.spaceinvaders.meta;

public enum Upgrade {
    EXTRA_LIFE("Extra life", "+1 starting life", 50, 3),
    DROP_RATE("Drop hunter", "+5% power-up drop rate", 30, 4),
    COOLDOWN("Trigger discipline", "-8% fire cooldown", 40, 4),
    SHIELD_REINFORCE("Shield engineer", "+1 shield on every wave start", 80, 1);

    public final String label;
    public final String description;
    public final int costPerLevel;
    public final int maxLevel;

    Upgrade(String label, String description, int costPerLevel, int maxLevel) {
        this.label = label;
        this.description = description;
        this.costPerLevel = costPerLevel;
        this.maxLevel = maxLevel;
    }

    public int costAtLevel(int currentLevel) {
        return costPerLevel * (currentLevel + 1);
    }
}
