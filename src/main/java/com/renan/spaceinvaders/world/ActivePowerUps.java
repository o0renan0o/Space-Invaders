package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;

import java.util.EnumMap;
import java.util.Map;

public final class ActivePowerUps {

    private final Map<PowerUpType, Integer> active = new EnumMap<>(PowerUpType.class);

    public void activate(PowerUpType type) {
        active.put(type, GameConfig.POWERUP_DURATION_TICKS);
    }

    public void tick() {
        active.entrySet().removeIf(e -> {
            int t = e.getValue() - 1;
            if (t <= 0) return true;
            e.setValue(t);
            return false;
        });
    }

    public boolean isActive(PowerUpType type) {
        return active.containsKey(type);
    }

    public int ticksLeft(PowerUpType type) {
        return active.getOrDefault(type, 0);
    }

    public float fractionLeft(PowerUpType type) {
        return active.getOrDefault(type, 0) / (float) GameConfig.POWERUP_DURATION_TICKS;
    }

    public Map<PowerUpType, Integer> snapshot() {
        return new EnumMap<>(active);
    }

    public void clear() {
        active.clear();
    }
}
