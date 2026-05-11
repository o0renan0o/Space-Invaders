package com.renan.spaceinvaders.render;

import com.renan.spaceinvaders.assets.AssetManager;
import com.renan.spaceinvaders.core.GameConfig;
import com.renan.spaceinvaders.core.GameState;
import com.renan.spaceinvaders.world.Alien;
import com.renan.spaceinvaders.world.Bullet;
import com.renan.spaceinvaders.world.Explosion;
import com.renan.spaceinvaders.world.Player;
import com.renan.spaceinvaders.world.Shield;
import com.renan.spaceinvaders.world.Ufo;
import com.renan.spaceinvaders.world.World;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

public final class Renderer {

    private static final Color BG = Color.BLACK;
    private static final Color WHITE = Color.WHITE;
    private static final Color GREEN = new Color(0x33FF33);
    private static final Color CYAN = new Color(0x33CCFF);
    private static final Color RED = new Color(0xFF3344);
    private static final Color YELLOW = new Color(0xFFCC33);
    private static final Color SHIELD = new Color(0x66FF77);

    private final World world;
    private final AssetManager assets;
    private final Font hudFont = new Font(Font.MONOSPACED, Font.BOLD, 16);
    private final Font bigFont = new Font(Font.MONOSPACED, Font.BOLD, 36);
    private final Font mediumFont = new Font(Font.MONOSPACED, Font.BOLD, 22);

    public Renderer(World world, AssetManager assets) {
        this.world = world;
        this.assets = assets;
    }

    public void render(Graphics2D g) {
        g.setColor(BG);
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        drawStars(g);
        drawShields(g);
        drawAliens(g);
        drawUfo(g);
        drawBullets(g);
        drawExplosions(g);
        drawPlayer(g);
        drawHud(g);
        drawOverlays(g);
    }

    private void drawStars(Graphics2D g) {
        g.setColor(new Color(40, 40, 60));
        long seed = 12345;
        for (int i = 0; i < 60; i++) {
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            int x = (int) ((seed >>> 33) % GameConfig.WIDTH);
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            int y = (int) ((seed >>> 33) % (GameConfig.HEIGHT - 60));
            g.fillRect(x, y, 2, 2);
        }
    }

    private void drawShields(Graphics2D g) {
        for (Shield s : world.getShields()) {
            boolean[][] cells = s.getCells();
            int cell = GameConfig.SHIELD_CELL;
            for (int r = 0; r < cells.length; r++) {
                for (int c = 0; c < cells[0].length; c++) {
                    if (!cells[r][c]) continue;
                    g.setColor(SHIELD);
                    g.fillRect(s.getX() + c * cell, s.getY() + r * cell, cell, cell);
                }
            }
        }
    }

    private void drawAliens(Graphics2D g) {
        BufferedImage img = assets.get("alien");
        for (Alien a : world.getAliens()) {
            int x = (int) a.getX();
            int y = (int) a.getY();
            if (img != null) {
                int offsetY = a.getAnimFrame() == 0 ? 0 : 2;
                g.drawImage(img, x, y + offsetY,
                        GameConfig.ALIEN_WIDTH, GameConfig.ALIEN_HEIGHT, null);
            } else {
                g.setColor(rowColor(a.getRow()));
                g.fillRect(x, y, GameConfig.ALIEN_WIDTH, GameConfig.ALIEN_HEIGHT);
                if (a.getAnimFrame() == 0) {
                    g.fillRect(x - 4, y + 8, 4, 6);
                    g.fillRect(x + GameConfig.ALIEN_WIDTH, y + 8, 4, 6);
                } else {
                    g.fillRect(x - 4, y + 16, 4, 6);
                    g.fillRect(x + GameConfig.ALIEN_WIDTH, y + 16, 4, 6);
                }
            }
        }
    }

    private Color rowColor(int row) {
        return switch (row) {
            case 0 -> RED;
            case 1 -> YELLOW;
            case 2 -> GREEN;
            default -> CYAN;
        };
    }

    private void drawUfo(Graphics2D g) {
        Ufo ufo = world.getUfo();
        if (ufo == null) return;
        BufferedImage img = assets.get("ufo");
        if (img != null) {
            g.drawImage(img, (int) ufo.getX(), (int) ufo.getY(),
                    GameConfig.UFO_WIDTH, GameConfig.UFO_HEIGHT, null);
        } else {
            g.setColor(RED);
            g.fillRect((int) ufo.getX(), (int) ufo.getY(),
                    GameConfig.UFO_WIDTH, GameConfig.UFO_HEIGHT);
        }
    }

    private void drawBullets(Graphics2D g) {
        for (Bullet b : world.getBullets()) {
            g.setColor(b.getSide() == Bullet.Side.PLAYER ? WHITE : YELLOW);
            g.fillRect((int) b.getX(), (int) b.getY(),
                    GameConfig.BULLET_WIDTH, GameConfig.BULLET_HEIGHT);
        }
    }

    private void drawExplosions(Graphics2D g) {
        for (Explosion ex : world.getExplosions()) {
            int t = ex.getTicks();
            int size = (GameConfig.EXPLOSION_TICKS - t) + 8;
            int alpha = Math.min(255, t * 12);
            g.setColor(new Color(255, 200, 50, alpha));
            g.fillRect(ex.getX() + (GameConfig.ALIEN_WIDTH - size) / 2,
                    ex.getY() + (GameConfig.ALIEN_HEIGHT - size) / 2,
                    size, size);
        }
    }

    private void drawPlayer(Graphics2D g) {
        Player p = world.getPlayer();
        if (p.getLives() <= 0) return;
        if (p.isInvulnerable() && ((p.getLives() + (int) (System.nanoTime() / 100_000_000L)) & 1) == 0) {
            return;
        }
        drawShip(g, (int) p.getX(), (int) p.getY());
    }

    private void drawShip(Graphics2D g, int x, int y) {
        BufferedImage img = assets.get("ship");
        if (img != null) {
            g.drawImage(img, x, y, GameConfig.PLAYER_WIDTH, GameConfig.PLAYER_HEIGHT, null);
            return;
        }
        g.setColor(GREEN);
        Polygon ship = new Polygon();
        int w = GameConfig.PLAYER_WIDTH;
        int h = GameConfig.PLAYER_HEIGHT;
        ship.addPoint(x + w / 2, y);
        ship.addPoint(x, y + h);
        ship.addPoint(x + w, y + h);
        g.fillPolygon(ship);
    }

    private void drawHud(Graphics2D g) {
        g.setFont(hudFont);
        g.setColor(WHITE);
        g.drawString("SCORE " + pad(world.getScore(), 5), 16, 22);
        String hi = "HI " + pad(world.getHighScore(), 5);
        int hiWidth = g.getFontMetrics().stringWidth(hi);
        g.drawString(hi, GameConfig.WIDTH / 2 - hiWidth / 2, 22);
        String wave = "WAVE " + world.getWave();
        int waveWidth = g.getFontMetrics().stringWidth(wave);
        g.drawString(wave, GameConfig.WIDTH - 16 - waveWidth, 22);

        g.setColor(GREEN);
        for (int i = 0; i < world.getPlayer().getLives() - 1; i++) {
            drawShip(g, 16 + i * (GameConfig.PLAYER_WIDTH + 8), GameConfig.HEIGHT - GameConfig.PLAYER_HEIGHT - 8);
        }
        g.setColor(GREEN);
        g.fillRect(0, GameConfig.HEIGHT - 4, GameConfig.WIDTH, 2);
    }

    private void drawOverlays(Graphics2D g) {
        GameState state = world.getState();
        switch (state) {
            case MENU -> drawMenu(g);
            case PAUSED -> drawCenter(g, "PAUSED", "Press P or ESC to resume   Q to quit");
            case WAVE_CLEARED -> drawCenter(g, "WAVE " + world.getWave() + " CLEARED", "Get ready...");
            case GAME_OVER -> drawCenter(g, "GAME OVER", "SCORE " + world.getScore()
                    + "   HI " + world.getHighScore()
                    + "    Press ENTER");
            case PLAYING -> {
            }
        }
    }

    private void drawMenu(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        g.setFont(bigFont);
        g.setColor(GREEN);
        String title = "SPACE INVADERS";
        int tw = g.getFontMetrics().stringWidth(title);
        g.drawString(title, GameConfig.WIDTH / 2 - tw / 2, 200);

        g.setFont(mediumFont);
        g.setColor(WHITE);
        String prompt = "Press ENTER to start";
        int pw = g.getFontMetrics().stringWidth(prompt);
        g.drawString(prompt, GameConfig.WIDTH / 2 - pw / 2, 280);

        g.setFont(hudFont);
        g.setColor(CYAN);
        String[] lines = {
                "Move:    LEFT / RIGHT  (or A / D)",
                "Fire:    SPACE",
                "Pause:   P or ESC",
                "Mute:    M",
                "",
                "Top row: 30   Middle: 20   Bottom: 10   UFO: 150"
        };
        int y = 330;
        for (String line : lines) {
            int w = g.getFontMetrics().stringWidth(line);
            g.drawString(line, GameConfig.WIDTH / 2 - w / 2, y);
            y += 22;
        }
        g.setColor(YELLOW);
        String hi = "HIGH SCORE   " + pad(world.getHighScore(), 5);
        int hw = g.getFontMetrics().stringWidth(hi);
        g.drawString(hi, GameConfig.WIDTH / 2 - hw / 2, y + 16);
    }

    private void drawCenter(Graphics2D g, String top, String bottom) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, GameConfig.HEIGHT / 2 - 80, GameConfig.WIDTH, 160);

        g.setFont(bigFont);
        g.setColor(WHITE);
        int tw = g.getFontMetrics().stringWidth(top);
        g.drawString(top, GameConfig.WIDTH / 2 - tw / 2, GameConfig.HEIGHT / 2 - 10);

        g.setFont(hudFont);
        g.setColor(CYAN);
        int bw = g.getFontMetrics().stringWidth(bottom);
        g.drawString(bottom, GameConfig.WIDTH / 2 - bw / 2, GameConfig.HEIGHT / 2 + 30);
    }

    private static String pad(int n, int width) {
        String s = Integer.toString(n);
        StringBuilder b = new StringBuilder();
        for (int i = s.length(); i < width; i++) b.append('0');
        b.append(s);
        return b.toString();
    }
}
