package com.renan.spaceinvaders.assets;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class FaceSheetTest {

    @Test
    void missingResourceLeavesSheetUnloaded() {
        FaceSheet sheet = new FaceSheet("pics/this_does_not_exist.png");
        assertFalse(sheet.isLoaded());
        assertNull(sheet.get(0, 0));
        assertNull(sheet.god());
        assertNull(sheet.dead());
    }

    @Test
    void healthRowForFiveLivesIsTopRow() {
        assertEquals(0, FaceSheet.healthRowForLives(5));
    }

    @Test
    void healthRowForOneLifeIsBottomRow() {
        assertEquals(FaceSheet.HEALTH_LEVELS - 1, FaceSheet.healthRowForLives(1));
    }

    @Test
    void healthRowForZeroLivesClampsToBottom() {
        assertEquals(FaceSheet.HEALTH_LEVELS - 1, FaceSheet.healthRowForLives(0));
    }

    @Test
    void healthRowMonotonicallyDecreasesWithLives() {
        int prev = -1;
        for (int lives = 5; lives >= 1; lives--) {
            int row = FaceSheet.healthRowForLives(lives);
            org.junit.jupiter.api.Assertions.assertTrue(row >= prev,
                    "row should not decrease as lives drop, lives=" + lives + " row=" + row);
            prev = row;
        }
    }

    @Test
    void healthRowAboveMaxClampsToZero() {
        assertEquals(0, FaceSheet.healthRowForLives(99));
    }
}
