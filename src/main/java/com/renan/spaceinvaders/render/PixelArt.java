package com.renan.spaceinvaders.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public final class PixelArt {

    private PixelArt() {
    }

    public static BufferedImage render(String[] rows, int scale, Color color) {
        int h = rows.length;
        int w = rows[0].length();
        BufferedImage img = new BufferedImage(w * scale, h * scale, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setColor(color);
            for (int y = 0; y < h; y++) {
                String row = rows[y];
                for (int x = 0; x < row.length(); x++) {
                    if (row.charAt(x) == '#') {
                        g.fillRect(x * scale, y * scale, scale, scale);
                    }
                }
            }
        } finally {
            g.dispose();
        }
        return img;
    }

    public static BufferedImage outlined(String[] rows, int scale, Color fill, Color outline) {
        BufferedImage base = render(rows, scale, fill);
        int h = rows.length;
        int w = rows[0].length();
        Graphics2D g = base.createGraphics();
        try {
            g.setColor(outline);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (rows[y].charAt(x) != '#') continue;
                    for (int[] d : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
                        int nx = x + d[0];
                        int ny = y + d[1];
                        boolean off = nx < 0 || ny < 0 || ny >= h || nx >= rows[ny].length();
                        if (off || rows[ny].charAt(nx) != '#') {
                            g.fillRect((x + d[0]) * scale, (y + d[1]) * scale, scale, scale);
                        }
                    }
                }
            }
        } finally {
            g.dispose();
        }
        return base;
    }
}
