package com.renan.spaceinvaders.core;

public final class GameConfig {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public static final double TICK_RATE = 60.0;

    public static final int PLAYER_LIVES = 3;
    public static final int PLAYER_WIDTH = 46;
    public static final int PLAYER_HEIGHT = 28;
    public static final int PLAYER_Y = HEIGHT - 60;
    public static final double PLAYER_SPEED = 4.0;
    public static final int PLAYER_FIRE_COOLDOWN_TICKS = 18;
    public static final int RESPAWN_INVULN_TICKS = 90;

    public static final int BULLET_WIDTH = 3;
    public static final int BULLET_HEIGHT = 12;
    public static final double PLAYER_BULLET_SPEED = 8.0;
    public static final double ALIEN_BULLET_SPEED = 3.5;

    public static final int ALIEN_ROWS = 5;
    public static final int ALIEN_COLS = 10;
    public static final int ALIEN_WIDTH = 40;
    public static final int ALIEN_HEIGHT = 28;
    public static final int ALIEN_H_SPACING = 18;
    public static final int ALIEN_V_SPACING = 16;
    public static final int ALIEN_START_Y = 70;
    public static final double ALIEN_START_SPEED = 0.6;
    public static final double ALIEN_SPEED_RAMP = 0.06;
    public static final int ALIEN_DROP = 14;
    public static final int ALIEN_FIRE_MIN_TICKS = 40;
    public static final int ALIEN_FIRE_MAX_TICKS = 110;
    public static final int ALIEN_FRAME_TICKS = 30;
    public static final int ALIEN_GAME_OVER_Y = PLAYER_Y - 8;

    public static final int SHIELD_COUNT = 4;
    public static final int SHIELD_Y = HEIGHT - 140;
    public static final int SHIELD_CELL = 4;
    public static final int SHIELD_COLS = 22;
    public static final int SHIELD_ROWS = 12;

    public static final int UFO_Y = 30;
    public static final double UFO_SPEED = 2.0;
    public static final int UFO_WIDTH = 56;
    public static final int UFO_HEIGHT = 22;
    public static final int UFO_MIN_INTERVAL_TICKS = 60 * 15;
    public static final int UFO_MAX_INTERVAL_TICKS = 60 * 30;
    public static final int UFO_SCORE = 150;

    public static final int SCORE_PER_ALIEN_ROW_0 = 30;
    public static final int SCORE_PER_ALIEN_ROW_1 = 20;
    public static final int SCORE_PER_ALIEN_ROW_DEFAULT = 10;

    public static final int EXPLOSION_TICKS = 18;

    private GameConfig() {
    }
}
