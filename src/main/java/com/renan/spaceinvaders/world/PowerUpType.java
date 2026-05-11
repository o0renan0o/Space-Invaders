package com.renan.spaceinvaders.world;

import java.awt.Color;

public enum PowerUpType {
    EXTRA_LIFE("pu_extra_life", new Color(0xFF6688), "1UP", true),
    RAPID_FIRE("pu_rapid", new Color(0xFFCC33), "RAPID", false),
    DOUBLE_SHOT("pu_double", new Color(0x66FFFF), "DOUBLE", false),
    PIERCING("pu_pierce", new Color(0xFFFFFF), "PIERCE", false),
    SLOW_MO("pu_slow", new Color(0xCCCCFF), "SLOW", false),
    SHIELD_REPAIR("pu_shield", new Color(0x66FF77), "SHIELD", true);

    public final String spriteKey;
    public final Color color;
    public final String label;
    public final boolean instant;

    PowerUpType(String spriteKey, Color color, String label, boolean instant) {
        this.spriteKey = spriteKey;
        this.color = color;
        this.label = label;
        this.instant = instant;
    }

    public static PowerUpType randomDrop(java.util.Random rng) {
        PowerUpType[] values = values();
        return values[rng.nextInt(values.length)];
    }
}
