package com.renan.spaceinvaders.ui;

import com.renan.spaceinvaders.core.GameState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FaceControllerTest {

    @Test
    void defaultIsForward() {
        FaceController f = new FaceController();
        assertEquals(FaceState.FORWARD, f.compute(GameState.PLAYING, 3));
    }

    @Test
    void movementSetsLookDirection() {
        FaceController f = new FaceController();
        f.onMoveLeft();
        assertEquals(FaceState.LEFT, f.compute(GameState.PLAYING, 3));
        f.onMoveRight();
        assertEquals(FaceState.RIGHT, f.compute(GameState.PLAYING, 3));
    }

    @Test
    void attackOverridesLook() {
        FaceController f = new FaceController();
        f.onMoveLeft();
        f.onShot();
        assertEquals(FaceState.ATTACK, f.compute(GameState.PLAYING, 3));
    }

    @Test
    void hurtOverridesAttack() {
        FaceController f = new FaceController();
        f.onShot();
        f.onHit();
        assertEquals(FaceState.HURT, f.compute(GameState.PLAYING, 3));
    }

    @Test
    void hitCancelsEvilGrin() {
        FaceController f = new FaceController();
        f.onSpecialKill();
        assertEquals(FaceState.EVIL_GRIN, f.compute(GameState.PLAYING, 3));
        f.onHit();
        assertEquals(FaceState.HURT, f.compute(GameState.PLAYING, 3));
    }

    @Test
    void deadStateOverridesEverything() {
        FaceController f = new FaceController();
        f.onShot();
        f.onSpecialKill();
        f.onMoveLeft();
        assertEquals(FaceState.DEAD, f.compute(GameState.GAME_OVER, 0));
    }

    @Test
    void waveClearedShowsWin() {
        FaceController f = new FaceController();
        f.onShot();
        assertEquals(FaceState.WIN, f.compute(GameState.WAVE_CLEARED, 3));
    }

    @Test
    void evilGrinExpiresAfterDuration() {
        FaceController f = new FaceController();
        f.onSpecialKill();
        for (int i = 0; i < 50; i++) f.tick();
        assertEquals(FaceState.FORWARD, f.compute(GameState.PLAYING, 3));
    }

    @Test
    void zeroLivesShowsDeadEvenInPlayingState() {
        FaceController f = new FaceController();
        assertEquals(FaceState.DEAD, f.compute(GameState.PLAYING, 0));
    }

    @Test
    void resetClearsAllTickers() {
        FaceController f = new FaceController();
        f.onShot();
        f.onHit();
        f.onSpecialKill();
        f.onMoveLeft();
        f.reset();
        assertEquals(FaceState.FORWARD, f.compute(GameState.PLAYING, 3));
    }
}
