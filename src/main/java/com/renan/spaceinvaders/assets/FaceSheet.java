package com.renan.spaceinvaders.assets;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public final class FaceSheet {

    public static final int HEALTH_LEVELS = 5;
    public static final int COLS = 9;

    public static final int COL_IDLE = 0;
    public static final int COL_BLINK1 = 1;
    public static final int COL_BLINK2 = 2;
    public static final int COL_LOOK_R = 3;
    public static final int COL_LOOK_L = 4;
    public static final int COL_OUCH = 5;
    public static final int COL_RAMPAGE = 6;
    public static final int COL_EVIL = 7;
    public static final int COL_EVIL2 = 8;

    private final BufferedImage[][] grid;
    private final BufferedImage god;
    private final BufferedImage dead;
    private final boolean loaded;

    public FaceSheet(String classpathResource) {
        BufferedImage sheet = loadImage(classpathResource);
        if (sheet == null) {
            this.grid = null;
            this.god = null;
            this.dead = null;
            this.loaded = false;
            return;
        }
        int cellW = sheet.getWidth() / COLS;
        int cellH = sheet.getHeight() / HEALTH_LEVELS;
        BufferedImage[][] g = new BufferedImage[HEALTH_LEVELS][COLS];
        for (int r = 0; r < HEALTH_LEVELS; r++) {
            for (int c = 0; c < COLS; c++) {
                BufferedImage cell = sheet.getSubimage(c * cellW, r * cellH, cellW, cellH);
                g[r][c] = stripChromaKey(cell);
            }
        }
        this.grid = g;
        this.god = g[0][COL_EVIL2];
        this.dead = g[HEALTH_LEVELS - 1][COL_EVIL2];
        this.loaded = true;
    }

    private BufferedImage loadImage(String path) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(path)) {
            if (in == null) return null;
            return ImageIO.read(in);
        } catch (Exception e) {
            return null;
        }
    }

    private BufferedImage stripChromaKey(BufferedImage src) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int p = src.getRGB(x, y);
                int r = (p >> 16) & 0xFF;
                int gg = (p >> 8) & 0xFF;
                int b = p & 0xFF;
                boolean cyan = r < 24 && gg > 220 && b > 220;
                boolean magenta = r > 220 && gg < 24 && b > 220;
                if (cyan || magenta) out.setRGB(x, y, 0);
                else out.setRGB(x, y, p | 0xFF000000);
            }
        }
        return out;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public BufferedImage get(int healthRow, int col) {
        if (!loaded) return null;
        int r = clamp(healthRow, 0, HEALTH_LEVELS - 1);
        int c = clamp(col, 0, COLS - 1);
        return grid[r][c];
    }

    public BufferedImage god() {
        return god;
    }

    public BufferedImage dead() {
        return dead;
    }

    public static int healthRowForLives(int lives) {
        if (lives >= HEALTH_LEVELS) return 0;
        if (lives <= 1) return HEALTH_LEVELS - 1;
        return HEALTH_LEVELS - lives;
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}
