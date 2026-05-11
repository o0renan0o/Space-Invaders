package com.renan.spaceinvaders.core;

public final class GameConfig {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int STBAR_HEIGHT = 70;
    public static final int STBAR_TOP = HEIGHT - STBAR_HEIGHT;

    public static final double TICK_RATE = 60.0;

    public static final int PLAYER_LIVES = 3;
    public static final int PLAYER_MAX_LIVES = 5;
    public static final int PLAYER_WIDTH = 60;
    public static final int PLAYER_HEIGHT = 30;
    public static final int PLAYER_Y = STBAR_TOP - 38;
    public static final double PLAYER_SPEED = 4.5;
    public static final int PLAYER_FIRE_COOLDOWN_TICKS = 18;
    public static final int RESPAWN_INVULN_TICKS = 90;

    public static final int BULLET_WIDTH = 4;
    public static final int BULLET_HEIGHT = 14;
    public static final double PLAYER_BULLET_SPEED = 8.5;
    public static final double ALIEN_BULLET_SPEED = 3.5;

    public static final int ALIEN_ROWS = 5;
    public static final int ALIEN_COLS = 10;
    public static final int ALIEN_WIDTH = 40;
    public static final int ALIEN_HEIGHT = 32;
    public static final int ALIEN_H_SPACING = 16;
    public static final int ALIEN_V_SPACING = 12;
    public static final int ALIEN_START_Y = 70;
    public static final double ALIEN_START_SPEED = 0.5;
    public static final int ALIEN_DROP = 14;
    public static final int ALIEN_FIRE_MIN_TICKS = 35;
    public static final int ALIEN_FIRE_MAX_TICKS = 110;
    public static final int ALIEN_FRAME_TICKS = 30;
    public static final int ALIEN_GAME_OVER_Y = PLAYER_Y - 8;

    public static final int DIVER_FIRE_CHANCE_PCT = 1;
    public static final double DIVER_SPEED = 3.2;

    public static final int SHIELD_COUNT = 4;
    public static final int SHIELD_Y = STBAR_TOP - 124;
    public static final int SHIELD_CELL = 4;
    public static final int SHIELD_COLS = 22;
    public static final int SHIELD_ROWS = 14;

    public static final int UFO_Y = 30;
    public static final double UFO_SPEED = 2.0;
    public static final int UFO_WIDTH = 80;
    public static final int UFO_HEIGHT = 36;
    public static final int UFO_MIN_INTERVAL_TICKS = 60 * 12;
    public static final int UFO_MAX_INTERVAL_TICKS = 60 * 25;
    public static final int UFO_SCORE = 150;

    public static final int EXPLOSION_TICKS = 24;
    public static final int EXPLOSION_FRAMES = 16;
    public static final int PARTICLES_PER_EXPLOSION = 14;
    public static final int PARTICLE_LIFE = 28;

    public static final int COMBO_WINDOW_TICKS = 90;
    public static final int COMBO_MAX_MULTIPLIER = 8;
    public static final int COMBO_BREAK_PENALTY = 0;

    public static final int POPUP_LIFETIME = 45;
    public static final int POPUP_RISE_PX = 25;

    public static final double POWERUP_DROP_CHANCE = 0.12;
    public static final double POWERUP_FALL_SPEED = 2.0;
    public static final int POWERUP_SIZE = 26;
    public static final int POWERUP_DURATION_TICKS = 60 * 10;
    public static final int RAPID_FIRE_COOLDOWN = 6;

    public static final int[] SCORE_REWARD_THRESHOLDS = {1000, 2500, 5000, 10000, 25000};

    public static final int CAMERA_SHAKE_HIT = 12;
    public static final int CAMERA_SHAKE_BOSS = 20;
    public static final int CAMERA_SHAKE_PLAYER = 18;

    public static final int STARFIELD_LAYERS = 3;
    public static final int STARS_PER_LAYER = 40;

    public static final int BOSS_HP_BASE = 30;
    public static final int BOSS_SCORE = 1000;
    public static final int BOSS_FIRE_INTERVAL = 35;
    public static final double BOSS_SPEED = 1.4;
    public static final int BOSS_WIDTH = 160;
    public static final int BOSS_HEIGHT = 80;
    public static final int BOSS_Y = 60;

    public static final int HALL_OF_FAME_SIZE = 10;

    public static final int SCORE_PER_ALIEN_ROW_0 = 30;
    public static final int SCORE_PER_ALIEN_ROW_1 = 20;
    public static final int SCORE_PER_ALIEN_ROW_DEFAULT = 10;

    public static final int MAX_PLAYER_BULLETS = 2;

    public static final int CHARGE_THRESHOLD_TICKS = 30;
    public static final int CHARGE_MAX_TICKS = 60;
    public static final int CHARGE_BULLET_WIDTH = 10;
    public static final int CHARGE_BULLET_HEIGHT = 22;
    public static final int CHARGE_BULLET_DAMAGE = 3;

    public static final double ALIEN_FIRE_LEAD_FACTOR = 0.6;
    public static final double ALIEN_FIRE_MAX_HORIZONTAL_SPEED = 2.0;

    public static final int NEAR_MISS_DISTANCE = 12;
    public static final int NEAR_MISS_SCORE = 25;
    public static final int NEAR_MISS_COMBO_BONUS_TICKS = 30;

    public static final int WAVE_SPEED_BONUS_TICK_THRESHOLD = 60 * 30;
    public static final int WAVE_SPEED_BONUS = 500;

    public static final double BOSS_PATTERN_2_HP_FRACTION = 0.66;
    public static final double BOSS_PATTERN_3_HP_FRACTION = 0.33;
    public static final int BOSS_PATTERN_3_FIRE_INTERVAL = 22;

    private GameConfig() {
    }
}
