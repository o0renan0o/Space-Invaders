package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;

import java.util.Random;

public final class StarField {

    public static final class Star {
        public double x;
        public double y;
        public final double speed;
        public final int size;
        public final int brightness;

        Star(double x, double y, double speed, int size, int brightness) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.size = size;
            this.brightness = brightness;
        }
    }

    private final Star[] stars;

    public StarField() {
        Random rng = new Random(1337);
        int total = GameConfig.STARFIELD_LAYERS * GameConfig.STARS_PER_LAYER;
        stars = new Star[total];
        for (int layer = 0; layer < GameConfig.STARFIELD_LAYERS; layer++) {
            double speed = 0.3 + layer * 0.6;
            int size = layer == 0 ? 1 : (layer == 1 ? 2 : 2);
            int brightness = 80 + layer * 60;
            for (int i = 0; i < GameConfig.STARS_PER_LAYER; i++) {
                int idx = layer * GameConfig.STARS_PER_LAYER + i;
                stars[idx] = new Star(
                        rng.nextDouble() * GameConfig.WIDTH,
                        rng.nextDouble() * GameConfig.HEIGHT,
                        speed, size, Math.min(255, brightness));
            }
        }
    }

    public void update() {
        for (Star s : stars) {
            s.y += s.speed;
            if (s.y > GameConfig.HEIGHT) {
                s.y = 0;
                s.x = (s.x + 137) % GameConfig.WIDTH;
            }
        }
    }

    public Star[] getStars() {
        return stars;
    }
}
