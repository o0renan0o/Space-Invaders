package com.renan.spaceinvaders.ui;

import com.renan.spaceinvaders.core.GameConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HallOfFameTest {

    @Test
    void emptyHallReportsZero(@TempDir Path dir) {
        HallOfFame hof = new HallOfFame(dir.resolve("hof"));
        assertEquals(0, hof.topScore());
        assertTrue(hof.getEntries().isEmpty());
    }

    @Test
    void anyScoreQualifiesWhenHallIsEmpty(@TempDir Path dir) {
        HallOfFame hof = new HallOfFame(dir.resolve("hof"));
        assertTrue(hof.qualifies(1));
        assertFalse(hof.qualifies(0));
    }

    @Test
    void addPersistsAndSorts(@TempDir Path dir) {
        Path path = dir.resolve("hof");
        HallOfFame hof = new HallOfFame(path);
        hof.add("AAA", 100);
        hof.add("BBB", 500);
        hof.add("CCC", 300);
        assertEquals(500, hof.topScore());
        assertEquals("BBB", hof.getEntries().get(0).initials());
        assertEquals("CCC", hof.getEntries().get(1).initials());
        assertEquals("AAA", hof.getEntries().get(2).initials());

        HallOfFame loaded = new HallOfFame(path);
        assertEquals(3, loaded.getEntries().size());
        assertEquals(500, loaded.topScore());
    }

    @Test
    void capsAtConfiguredSize(@TempDir Path dir) {
        HallOfFame hof = new HallOfFame(dir.resolve("hof"));
        for (int i = 0; i < GameConfig.HALL_OF_FAME_SIZE + 5; i++) {
            hof.add("AAA", i + 1);
        }
        assertEquals(GameConfig.HALL_OF_FAME_SIZE, hof.getEntries().size());
    }

    @Test
    void newEntryDoesNotQualifyBelowTenth(@TempDir Path dir) {
        HallOfFame hof = new HallOfFame(dir.resolve("hof"));
        for (int i = 0; i < GameConfig.HALL_OF_FAME_SIZE; i++) {
            hof.add("AAA", 1000 - i);
        }
        assertFalse(hof.qualifies(1));
        assertTrue(hof.qualifies(2000));
    }

    @Test
    void initialsNormalizedToThreeUppercase(@TempDir Path dir) {
        HallOfFame hof = new HallOfFame(dir.resolve("hof"));
        hof.add("xy", 100);
        assertEquals("XYA", hof.getEntries().get(0).initials());
    }
}
