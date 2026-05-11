package com.renan.spaceinvaders.ui;

import com.renan.spaceinvaders.core.GameConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class HallOfFame {

    public record Entry(String initials, int score) implements Comparable<Entry> {
        @Override
        public int compareTo(Entry other) {
            return Integer.compare(other.score, this.score);
        }
    }

    private final Path path;
    private final List<Entry> entries = new ArrayList<>();

    public HallOfFame() {
        this(defaultPath());
    }

    public HallOfFame(Path path) {
        this.path = path;
        load();
    }

    private static Path defaultPath() {
        String home = System.getProperty("user.home", ".");
        Path dir = Paths.get(home, ".spaceinvaders");
        try {
            Files.createDirectories(dir);
        } catch (IOException ignored) {
            return Paths.get(".hof");
        }
        return dir.resolve("hof");
    }

    public void load() {
        entries.clear();
        if (!Files.exists(path)) return;
        try {
            for (String line : Files.readAllLines(path)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;
                int sep = trimmed.indexOf(' ');
                if (sep < 0) continue;
                try {
                    int score = Integer.parseInt(trimmed.substring(0, sep).trim());
                    String initials = trimmed.substring(sep + 1).trim();
                    if (initials.length() > 3) initials = initials.substring(0, 3);
                    entries.add(new Entry(initials, score));
                } catch (NumberFormatException ignored) {
                }
            }
            sortAndTrim();
        } catch (IOException ignored) {
        }
    }

    public void save() {
        try {
            List<String> lines = new ArrayList<>();
            for (Entry e : entries) lines.add(e.score + " " + e.initials);
            Files.write(path, lines);
        } catch (IOException ignored) {
        }
    }

    public boolean qualifies(int score) {
        if (score <= 0) return false;
        if (entries.size() < GameConfig.HALL_OF_FAME_SIZE) return true;
        return score > entries.get(entries.size() - 1).score;
    }

    public void add(String initials, int score) {
        entries.add(new Entry(normalize(initials), score));
        sortAndTrim();
        save();
    }

    private static String normalize(String initials) {
        if (initials == null) return "AAA";
        String upper = initials.toUpperCase();
        if (upper.length() > 3) upper = upper.substring(0, 3);
        StringBuilder sb = new StringBuilder(upper);
        while (sb.length() < 3) sb.append('A');
        return sb.toString();
    }

    private void sortAndTrim() {
        Collections.sort(entries, Comparator.naturalOrder());
        while (entries.size() > GameConfig.HALL_OF_FAME_SIZE) {
            entries.remove(entries.size() - 1);
        }
    }

    public List<Entry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public int topScore() {
        return entries.isEmpty() ? 0 : entries.get(0).score;
    }
}
