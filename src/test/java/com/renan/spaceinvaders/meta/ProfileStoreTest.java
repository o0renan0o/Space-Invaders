package com.renan.spaceinvaders.meta;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProfileStoreTest {

    @Test
    void roundTripPersistsCoinsAndLevels(@TempDir Path tmp) {
        Path file = tmp.resolve("profile");
        ProfileStore store = new ProfileStore(file);

        Profile p = store.load();
        p.earn(120);
        p.buy(Upgrade.EXTRA_LIFE);
        p.buy(Upgrade.DROP_RATE);
        store.save(p);

        Profile loaded = store.load();
        assertEquals(p.getCoins(), loaded.getCoins());
        assertEquals(p.getTotalCoinsEarned(), loaded.getTotalCoinsEarned());
        for (Upgrade u : Upgrade.values()) {
            assertEquals(p.getLevel(u), loaded.getLevel(u), u.name());
        }
    }

    @Test
    void missingFileReturnsBlankProfile(@TempDir Path tmp) {
        ProfileStore store = new ProfileStore(tmp.resolve("nope"));
        Profile p = store.load();
        assertEquals(0, p.getCoins());
        assertEquals(0, p.getTotalCoinsEarned());
    }
}
