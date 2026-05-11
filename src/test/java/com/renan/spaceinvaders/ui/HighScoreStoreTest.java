package com.renan.spaceinvaders.ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HighScoreStoreTest {

    @Test
    void loadReturnsZeroWhenAbsent(@TempDir Path dir) {
        HighScoreStore store = new HighScoreStore(dir.resolve("missing"));
        assertEquals(0, store.load());
    }

    @Test
    void saveRoundTrips(@TempDir Path dir) {
        HighScoreStore store = new HighScoreStore(dir.resolve("hs"));
        store.save(1234);
        assertEquals(1234, store.load());
    }

    @Test
    void loadHandlesCorruptFile(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("hs");
        java.nio.file.Files.writeString(file, "not-a-number");
        HighScoreStore store = new HighScoreStore(file);
        assertEquals(0, store.load());
    }

    @Test
    void negativeValuesClampedToZero(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("hs");
        java.nio.file.Files.writeString(file, "-999");
        HighScoreStore store = new HighScoreStore(file);
        assertEquals(0, store.load());
    }
}
