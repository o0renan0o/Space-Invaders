package com.renan.spaceinvaders.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class HighScoreStore {

    private final Path path;

    public HighScoreStore() {
        this(defaultPath());
    }

    public HighScoreStore(Path path) {
        this.path = path;
    }

    private static Path defaultPath() {
        String home = System.getProperty("user.home", ".");
        Path dir = Paths.get(home, ".spaceinvaders");
        try {
            Files.createDirectories(dir);
        } catch (IOException ignored) {
            return Paths.get(".highscore");
        }
        return dir.resolve("highscore");
    }

    public int load() {
        if (!Files.exists(path)) return 0;
        try {
            String raw = Files.readString(path).trim();
            if (raw.isEmpty()) return 0;
            return Math.max(0, Integer.parseInt(raw));
        } catch (IOException | NumberFormatException e) {
            return 0;
        }
    }

    public void save(int score) {
        try {
            Files.writeString(path, Integer.toString(score));
        } catch (IOException ignored) {
            // best effort - ignore failures
        }
    }
}
