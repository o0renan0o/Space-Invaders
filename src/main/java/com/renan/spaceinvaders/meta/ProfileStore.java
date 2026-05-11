package com.renan.spaceinvaders.meta;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class ProfileStore {

    private final Path path;

    public ProfileStore() {
        this(defaultPath());
    }

    public ProfileStore(Path path) {
        this.path = path;
    }

    private static Path defaultPath() {
        String home = System.getProperty("user.home", ".");
        Path dir = Paths.get(home, ".spaceinvaders");
        try {
            Files.createDirectories(dir);
        } catch (IOException ignored) {
            return Paths.get(".profile");
        }
        return dir.resolve("profile");
    }

    public Profile load() {
        Profile p = new Profile();
        if (!Files.exists(path)) return p;
        Properties props = new Properties();
        try {
            props.load(Files.newBufferedReader(path));
        } catch (IOException ignored) {
            return p;
        }
        p.setCoins(parseInt(props.getProperty("coins"), 0));
        p.setTotalCoinsEarned(parseInt(props.getProperty("totalCoinsEarned"), 0));
        for (Upgrade u : Upgrade.values()) {
            p.setLevel(u, parseInt(props.getProperty("upgrade." + u.name()), 0));
        }
        return p;
    }

    public void save(Profile p) {
        List<String> lines = new ArrayList<>();
        lines.add("coins=" + p.getCoins());
        lines.add("totalCoinsEarned=" + p.getTotalCoinsEarned());
        for (Upgrade u : Upgrade.values()) {
            lines.add("upgrade." + u.name() + "=" + p.getLevel(u));
        }
        try {
            Files.write(path, lines);
        } catch (IOException ignored) {
        }
    }

    private static int parseInt(String s, int fallback) {
        if (s == null) return fallback;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
