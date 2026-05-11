package com.renan.spaceinvaders.ui;

import com.renan.spaceinvaders.core.GameState;

public final class FaceController {

    private static final int ATTACK_DURATION = 8;
    private static final int HURT_DURATION = 35;
    private static final int EVIL_DURATION = 32;
    private static final int LOOK_DURATION = 8;

    private int attackTicks;
    private int hurtTicks;
    private int evilTicks;
    private int leftTicks;
    private int rightTicks;

    public void onShot() {
        attackTicks = ATTACK_DURATION;
    }

    public void onHit() {
        hurtTicks = HURT_DURATION;
        evilTicks = 0;
    }

    public void onSpecialKill() {
        evilTicks = EVIL_DURATION;
    }

    public void onMoveLeft() {
        leftTicks = LOOK_DURATION;
        rightTicks = 0;
    }

    public void onMoveRight() {
        rightTicks = LOOK_DURATION;
        leftTicks = 0;
    }

    public void tick() {
        if (attackTicks > 0) attackTicks--;
        if (hurtTicks > 0) hurtTicks--;
        if (evilTicks > 0) evilTicks--;
        if (leftTicks > 0) leftTicks--;
        if (rightTicks > 0) rightTicks--;
    }

    public void reset() {
        attackTicks = 0;
        hurtTicks = 0;
        evilTicks = 0;
        leftTicks = 0;
        rightTicks = 0;
    }

    public FaceState compute(GameState gameState, int lives) {
        if (gameState == GameState.GAME_OVER || lives <= 0) return FaceState.DEAD;
        if (gameState == GameState.WAVE_CLEARED) return FaceState.WIN;
        if (hurtTicks > 0) return FaceState.HURT;
        if (evilTicks > 0) return FaceState.EVIL_GRIN;
        if (attackTicks > 0) return FaceState.ATTACK;
        if (leftTicks > 0 && leftTicks >= rightTicks) return FaceState.LEFT;
        if (rightTicks > 0) return FaceState.RIGHT;
        return FaceState.FORWARD;
    }

    int getAttackTicks() { return attackTicks; }
    int getHurtTicks() { return hurtTicks; }
    int getEvilTicks() { return evilTicks; }
    int getLeftTicks() { return leftTicks; }
    int getRightTicks() { return rightTicks; }
}
