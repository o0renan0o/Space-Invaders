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
    void invulnerableShowsGodMode() {
        FaceController f = new FaceController();
        assertEquals(FaceState.GOD, f.compute(GameState.PLAYING, 3, true));
    }

    @Test
    void invulnerableOverridesHurtAndAttack() {
        FaceController f = new FaceController();
        f.onHit();
        f.onShot();
        assertEquals(FaceState.GOD, f.compute(GameState.PLAYING, 3, true));
    }

    @Test
    void deadStillBeatsInvulnerable() {
        FaceController f = new FaceController();
        assertEquals(FaceState.DEAD, f.compute(GameState.PLAYING, 0, true));
    }

    @Test
    void waveClearedBeatsInvulnerable() {
        FaceController f = new FaceController();
        assertEquals(FaceState.WIN, f.compute(GameState.WAVE_CLEARED, 3, true));
    }

    @Test
    void doomColumnsCoverCanonicalLayout() {
        assertEquals(0, FaceState.FORWARD.doomColumn);
        assertEquals(3, FaceState.RIGHT.doomColumn);
        assertEquals(4, FaceState.LEFT.doomColumn);
        assertEquals(5, FaceState.HURT.doomColumn);
        assertEquals(6, FaceState.ATTACK.doomColumn);
        assertEquals(7, FaceState.EVIL_GRIN.doomColumn);
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
