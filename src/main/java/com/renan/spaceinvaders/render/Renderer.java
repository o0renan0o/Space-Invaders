package com.renan.spaceinvaders.render;

import com.renan.spaceinvaders.assets.AssetManager;
import com.renan.spaceinvaders.core.Difficulty;
import com.renan.spaceinvaders.core.GameConfig;
import com.renan.spaceinvaders.core.GameState;
import com.renan.spaceinvaders.world.Alien;
import com.renan.spaceinvaders.world.Boss;
import com.renan.spaceinvaders.world.Bullet;
import com.renan.spaceinvaders.world.Explosion;
import com.renan.spaceinvaders.world.Particle;
import com.renan.spaceinvaders.world.Player;
import com.renan.spaceinvaders.world.PowerUp;
import com.renan.spaceinvaders.world.PowerUpType;
import com.renan.spaceinvaders.world.ScorePopup;
import com.renan.spaceinvaders.world.Shield;
import com.renan.spaceinvaders.world.StarField;
import com.renan.spaceinvaders.world.Ufo;
import com.renan.spaceinvaders.world.World;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public final class Renderer {

    private static final Color WHITE = Color.WHITE;
    private static final Color GREEN = new Color(0x33FF33);
    private static final Color CYAN = new Color(0x33CCFF);
    private static final Color YELLOW = new Color(0xFFCC33);
    private static final Color PINK = new Color(0xFF66AA);
    private static final Color SHIELD = new Color(0x66FF77);

    private final World world;
    private final AssetManager assets;
    private final Font hudFont = new Font(Font.MONOSPACED, Font.BOLD, 16);
    private final Font smallFont = new Font(Font.MONOSPACED, Font.BOLD, 12);
    private final Font bigFont = new Font(Font.MONOSPACED, Font.BOLD, 36);
    private final Font mediumFont = new Font(Font.MONOSPACED, Font.BOLD, 22);
    private final Font hugeFont = new Font(Font.MONOSPACED, Font.BOLD, 56);

    public Renderer(World world, AssetManager assets) {
        this.world = world;
        this.assets = assets;
    }

    public void render(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        drawStars(g);

        int sx = world.getCameraShake().offsetX();
        int sy = world.getCameraShake().offsetY();
        g.translate(sx, sy);

        drawShields(g);
        drawAliens(g);
        drawBoss(g);
        drawUfo(g);
        drawPowerUps(g);
        drawBullets(g);
        drawExplosions(g);
        drawParticles(g);
        drawPlayer(g);
        drawPopups(g);

        g.translate(-sx, -sy);

        drawHud(g);
        drawOverlays(g);
    }

    private void drawStars(Graphics2D g) {
        StarField sf = world.getStarField();
        for (StarField.Star s : sf.getStars()) {
            int b = s.brightness;
            g.setColor(new Color(b, b, Math.min(255, b + 30)));
            g.fillRect((int) s.x, (int) s.y, s.size, s.size);
        }
    }

    private void drawShields(Graphics2D g) {
        for (Shield s : world.getShields()) {
            boolean[][] cells = s.getCells();
            int cell = GameConfig.SHIELD_CELL;
            g.setColor(SHIELD);
            for (int r = 0; r < cells.length; r++) {
                for (int c = 0; c < cells[0].length; c++) {
                    if (!cells[r][c]) continue;
                    g.fillRect(s.getX() + c * cell, s.getY() + r * cell, cell, cell);
                }
            }
        }
    }

    private void drawAliens(Graphics2D g) {
        for (Alien a : world.getAliens()) {
            BufferedImage img = assets.get(a.getType().spriteKey);
            int x = (int) a.getX();
            int y = (int) a.getY() + (a.getAnimFrame() == 0 ? 0 : 2);
            if (img != null) {
                g.drawImage(img, x, y, GameConfig.ALIEN_WIDTH, GameConfig.ALIEN_HEIGHT, null);
            } else {
                g.setColor(rowColor(a.getRow()));
                g.fillRect(x, y, GameConfig.ALIEN_WIDTH, GameConfig.ALIEN_HEIGHT);
            }
            if (a.getType().hp > 1 && a.getHp() < a.getType().hp) {
                g.setColor(new Color(0xFF3333));
                g.drawRect(x - 1, y - 1, GameConfig.ALIEN_WIDTH + 2, GameConfig.ALIEN_HEIGHT + 2);
            }
        }
    }

    private void drawBoss(Graphics2D g) {
        Boss boss = world.getBoss();
        if (boss == null) return;
        BufferedImage img = assets.get("boss");
        if (img == null) img = assets.get("alien_armored");
        int x = (int) boss.getX();
        int y = (int) boss.getY();
        if (img != null) {
            g.drawImage(img, x, y, GameConfig.BOSS_WIDTH, GameConfig.BOSS_HEIGHT, null);
        } else {
            g.setColor(new Color(0xFF3333));
            g.fillRect(x, y, GameConfig.BOSS_WIDTH, GameConfig.BOSS_HEIGHT);
        }
        if (boss.isFlashing()) {
            g.setColor(new Color(255, 255, 255, 110));
            g.fillRect(x, y, GameConfig.BOSS_WIDTH, GameConfig.BOSS_HEIGHT);
        }
    }

    private void drawBossHud(Graphics2D g) {
        Boss boss = world.getBoss();
        if (boss == null) return;
        int barY = 40;
        int barX = 100;
        int barW = GameConfig.WIDTH - 200;
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(barX - 2, barY - 2, barW + 4, 14);
        Color color = switch (boss.pattern()) {
            case 1 -> new Color(0x33FF66);
            case 2 -> new Color(0xFFCC33);
            default -> new Color(0xFF3344);
        };
        g.setColor(color);
        int fill = (int) (barW * boss.hpFraction());
        g.fillRect(barX, barY, Math.max(0, fill), 10);
        g.setColor(WHITE);
        g.drawRect(barX, barY, barW, 10);
        g.setFont(smallFont);
        g.drawString("BOSS HP   PHASE " + boss.pattern(), barX, barY - 4);
    }

    private void drawUfo(Graphics2D g) {
        Ufo ufo = world.getUfo();
        if (ufo == null) return;
        BufferedImage img = assets.get("ufo");
        if (img != null) {
            g.drawImage(img, (int) ufo.getX(), (int) ufo.getY(),
                    GameConfig.UFO_WIDTH, GameConfig.UFO_HEIGHT, null);
        } else {
            g.setColor(PINK);
            g.fillRect((int) ufo.getX(), (int) ufo.getY(),
                    GameConfig.UFO_WIDTH, GameConfig.UFO_HEIGHT);
        }
    }

    private void drawPowerUps(Graphics2D g) {
        for (PowerUp pu : world.getPowerUps()) {
            BufferedImage img = assets.get(pu.getType().spriteKey);
            int x = (int) pu.getX();
            int y = (int) pu.getY();
            int wobble = (int) (Math.sin(pu.getSpinTick() * 0.2) * 2);
            int size = GameConfig.POWERUP_SIZE;
            g.setColor(new Color(pu.getType().color.getRed(),
                    pu.getType().color.getGreen(),
                    pu.getType().color.getBlue(), 80));
            g.fillOval(x - 2, y - 2 + wobble, size + 4, size + 4);
            if (img != null) {
                g.drawImage(img, x, y + wobble, size, size, null);
            } else {
                g.setColor(pu.getType().color);
                g.fillRect(x, y + wobble, size, size);
            }
        }
    }

    private void drawBullets(Graphics2D g) {
        for (Bullet b : world.getBullets()) {
            Color c = b.getSide() == Bullet.Side.PLAYER
                    ? (b.isPiercing() ? CYAN : WHITE)
                    : (b.isSplittable() ? new Color(0xFFAA33) : YELLOW);
            int w = b.getWidth();
            int h = b.getHeight();
            if (b.isPiercing()) {
                g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 80));
                g.fillRect((int) b.getX() - 3, (int) b.getY() - 3, w + 6, h + 6);
            }
            g.setColor(c);
            g.fillRect((int) b.getX(), (int) b.getY(), w, h);
        }
    }

    private void drawExplosions(Graphics2D g) {
        for (Explosion ex : world.getExplosions()) {
            int t = ex.getTicks();
            int frameIndex = (int) ((1 - t / (double) GameConfig.EXPLOSION_TICKS)
                    * GameConfig.EXPLOSION_FRAMES);
            BufferedImage frame = assets.explosionFrame(frameIndex);
            int size = 64;
            int cx = ex.getX() + GameConfig.ALIEN_WIDTH / 2;
            int cy = ex.getY() + GameConfig.ALIEN_HEIGHT / 2;
            if (frame != null) {
                g.drawImage(frame, cx - size / 2, cy - size / 2, size, size, null);
            } else {
                int s = (GameConfig.EXPLOSION_TICKS - t) + 8;
                g.setColor(new Color(255, 200, 50, Math.min(255, t * 12)));
                g.fillRect(cx - s / 2, cy - s / 2, s, s);
            }
        }
    }

    private void drawParticles(Graphics2D g) {
        for (Particle p : world.getParticles()) {
            float frac = p.getLife() / (float) p.getMaxLife();
            int a = (int) (frac * 255);
            Color c = p.getColor();
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, a))));
            int sz = 2 + (int) (frac * 2);
            g.fillRect((int) p.getX(), (int) p.getY(), sz, sz);
        }
    }

    private void drawPlayer(Graphics2D g) {
        Player p = world.getPlayer();
        if (p.getLives() <= 0) return;
        if (p.isInvulnerable() && ((System.nanoTime() / 100_000_000L) & 1) == 0) return;
        drawShip(g, (int) p.getX(), (int) p.getY(),
                GameConfig.PLAYER_WIDTH, GameConfig.PLAYER_HEIGHT);
        drawChargeIndicator(g, p);
    }

    private void drawChargeIndicator(Graphics2D g, Player p) {
        int hold = world.getChargeHoldTicks();
        if (hold <= 0) return;
        int barW = GameConfig.PLAYER_WIDTH;
        int barX = (int) p.getX();
        int barY = (int) p.getY() + GameConfig.PLAYER_HEIGHT + 4;
        float frac = Math.min(1f, hold / (float) GameConfig.CHARGE_THRESHOLD_TICKS);
        g.setColor(new Color(40, 40, 60));
        g.fillRect(barX, barY, barW, 3);
        Color fill = world.isCharging() ? CYAN : new Color(200, 200, 100);
        g.setColor(fill);
        g.fillRect(barX, barY, (int) (barW * frac), 3);
        if (world.isCharging()) {
            int glow = (int) (4 + Math.sin(System.nanoTime() / 80_000_000.0) * 2);
            g.setColor(new Color(CYAN.getRed(), CYAN.getGreen(), CYAN.getBlue(), 90));
            g.fillRect((int) p.getX() - glow, (int) p.getY() - glow,
                    GameConfig.PLAYER_WIDTH + glow * 2,
                    GameConfig.PLAYER_HEIGHT + glow * 2);
        }
    }

    private void drawShip(Graphics2D g, int x, int y, int w, int h) {
        BufferedImage img = assets.get("ship");
        if (img != null) {
            g.drawImage(img, x, y, w, h, null);
            return;
        }
        g.setColor(GREEN);
        Polygon ship = new Polygon();
        ship.addPoint(x + w / 2, y);
        ship.addPoint(x, y + h);
        ship.addPoint(x + w, y + h);
        g.fillPolygon(ship);
    }

    private void drawPopups(Graphics2D g) {
        g.setFont(hudFont);
        for (ScorePopup pu : world.getPopups()) {
            float alpha = pu.getAlpha();
            Color c = pu.getColor();
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(),
                    Math.max(0, Math.min(255, (int) (alpha * 255)))));
            g.drawString(pu.getText(), pu.getX(), pu.getY());
        }
    }

    private void drawHud(Graphics2D g) {
        g.setFont(hudFont);
        g.setColor(WHITE);
        g.drawString("SCORE " + pad(world.getScore(), 6), 16, 22);
        String hi = "HI " + pad(world.getHighScore(), 6);
        int hiWidth = g.getFontMetrics().stringWidth(hi);
        g.drawString(hi, GameConfig.WIDTH / 2 - hiWidth / 2, 22);
        String wave = "WAVE " + world.getWave() + "  " + world.getCurrentPhase().name();
        int waveWidth = g.getFontMetrics().stringWidth(wave);
        g.drawString(wave, GameConfig.WIDTH - 16 - waveWidth, 22);

        Player p = world.getPlayer();
        for (int i = 0; i < p.getLives() - 1; i++) {
            drawShip(g, 16 + i * (GameConfig.PLAYER_WIDTH / 2 + 4),
                    GameConfig.HEIGHT - GameConfig.PLAYER_HEIGHT / 2 - 8,
                    GameConfig.PLAYER_WIDTH / 2, GameConfig.PLAYER_HEIGHT / 2);
        }
        g.setColor(new Color(0, 200, 80));
        g.fillRect(0, GameConfig.HEIGHT - 4, GameConfig.WIDTH, 2);

        drawCombo(g);
        drawActivePowerUps(g);
        drawBossHud(g);
    }

    private void drawCombo(Graphics2D g) {
        int mult = world.getCombo().getMultiplier();
        if (mult <= 1) return;
        g.setFont(mediumFont);
        Color color = mult >= 4 ? YELLOW : WHITE;
        g.setColor(color);
        String text = "COMBO x" + mult;
        int tw = g.getFontMetrics().stringWidth(text);
        int x = GameConfig.WIDTH - tw - 16;
        int y = 50;
        g.drawString(text, x, y);
        int barW = 100;
        int barX = GameConfig.WIDTH - barW - 16;
        int barY = y + 6;
        g.setColor(new Color(60, 60, 60));
        g.fillRect(barX, barY, barW, 4);
        g.setColor(color);
        g.fillRect(barX, barY, (int) (barW * world.getCombo().getWindowFraction()), 4);
    }

    private void drawActivePowerUps(Graphics2D g) {
        Map<PowerUpType, Integer> active = world.getActivePowerUps().snapshot();
        if (active.isEmpty()) return;
        int x = GameConfig.WIDTH - 80;
        int y = GameConfig.HEIGHT - 90;
        g.setFont(smallFont);
        for (Map.Entry<PowerUpType, Integer> e : active.entrySet()) {
            PowerUpType t = e.getKey();
            BufferedImage img = assets.get(t.spriteKey);
            if (img != null) {
                g.drawImage(img, x, y, 20, 20, null);
            } else {
                g.setColor(t.color);
                g.fillRect(x, y, 20, 20);
            }
            g.setColor(WHITE);
            g.drawString(t.label, x + 24, y + 10);
            float frac = world.getActivePowerUps().fractionLeft(t);
            g.setColor(new Color(60, 60, 60));
            g.fillRect(x + 24, y + 14, 50, 3);
            g.setColor(t.color);
            g.fillRect(x + 24, y + 14, (int) (50 * frac), 3);
            y += 24;
        }
    }

    private Color rowColor(int row) {
        return switch (row) {
            case 0 -> new Color(0xFF3344);
            case 1 -> YELLOW;
            case 2 -> GREEN;
            default -> CYAN;
        };
    }

    private void drawOverlays(Graphics2D g) {
        GameState state = world.getState();
        switch (state) {
            case MENU -> drawMenu(g);
            case HALL_OF_FAME -> drawHallOfFame(g);
            case PAUSED -> drawCenter(g, "PAUSED", "Press P or ESC to resume   Q to quit");
            case WAVE_CLEARED -> drawCenter(g, "WAVE CLEARED!",
                    "+" + world.getCurrentPhase().clearBonus() + " bonus");
            case GAME_OVER -> drawCenter(g, "GAME OVER",
                    "SCORE " + world.getScore() + "    Press ENTER");
            case ENTERING_INITIALS -> drawInitials(g);
            default -> {
            }
        }
    }

    private void drawMenu(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        g.setFont(hugeFont);
        g.setColor(GREEN);
        String title = "SPACE INVADERS";
        int tw = g.getFontMetrics().stringWidth(title);
        g.drawString(title, GameConfig.WIDTH / 2 - tw / 2, 130);

        g.setFont(mediumFont);
        g.setColor(WHITE);
        String prompt = "Press ENTER to start";
        int pw = g.getFontMetrics().stringWidth(prompt);
        g.drawString(prompt, GameConfig.WIDTH / 2 - pw / 2, 200);

        g.setFont(hudFont);
        g.setColor(YELLOW);
        String diff = "DIFFICULTY: " + world.getDifficulty().label + "   [D]";
        int dw = g.getFontMetrics().stringWidth(diff);
        g.drawString(diff, GameConfig.WIDTH / 2 - dw / 2, 240);

        g.setColor(CYAN);
        String[] lines = {
                "MOVE      LEFT / RIGHT  (A / D)",
                "FIRE      SPACE",
                "PAUSE     P or ESC",
                "MUTE      M",
                "HALL OF FAME    H",
                "",
                "SCORING",
                "Squid 30   Crab 20   Octopus 10",
                "Armored 50   UFO 150   Boss 1000+",
                "",
                "POWER-UPS: 1UP, RAPID, DOUBLE,",
                "PIERCE, SLOW-MO, SHIELD REPAIR"
        };
        int y = 290;
        for (String line : lines) {
            int w = g.getFontMetrics().stringWidth(line);
            g.drawString(line, GameConfig.WIDTH / 2 - w / 2, y);
            y += 20;
        }
        g.setColor(YELLOW);
        String hi = "HIGH SCORE   " + pad(world.getHighScore(), 6);
        int hw = g.getFontMetrics().stringWidth(hi);
        g.drawString(hi, GameConfig.WIDTH / 2 - hw / 2, y + 14);
    }

    private void drawHallOfFame(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        g.setFont(bigFont);
        g.setColor(YELLOW);
        String title = "HALL OF FAME";
        int tw = g.getFontMetrics().stringWidth(title);
        g.drawString(title, GameConfig.WIDTH / 2 - tw / 2, 100);

        g.setFont(mediumFont);
        g.setColor(WHITE);
        List<com.renan.spaceinvaders.ui.HallOfFame.Entry> entries =
                world.getHallOfFame().getEntries();
        int y = 160;
        if (entries.isEmpty()) {
            String none = "no scores yet";
            int nw = g.getFontMetrics().stringWidth(none);
            g.drawString(none, GameConfig.WIDTH / 2 - nw / 2, y);
        } else {
            for (int i = 0; i < entries.size(); i++) {
                var e = entries.get(i);
                String line = String.format("%2d.  %s   %s", i + 1, e.initials(), pad(e.score(), 6));
                int w = g.getFontMetrics().stringWidth(line);
                g.drawString(line, GameConfig.WIDTH / 2 - w / 2, y);
                y += 30;
            }
        }
        g.setFont(hudFont);
        g.setColor(CYAN);
        String hint = "Press ENTER to return";
        int hw = g.getFontMetrics().stringWidth(hint);
        g.drawString(hint, GameConfig.WIDTH / 2 - hw / 2, GameConfig.HEIGHT - 40);
    }

    private void drawInitials(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        g.setFont(bigFont);
        g.setColor(YELLOW);
        String title = "NEW HIGH SCORE";
        int tw = g.getFontMetrics().stringWidth(title);
        g.drawString(title, GameConfig.WIDTH / 2 - tw / 2, 150);

        g.setFont(mediumFont);
        g.setColor(WHITE);
        String score = "SCORE  " + pad(world.getScore(), 6);
        int sw = g.getFontMetrics().stringWidth(score);
        g.drawString(score, GameConfig.WIDTH / 2 - sw / 2, 200);

        g.setFont(hugeFont);
        char[] letters = world.getInitialsEntry().getLetters();
        int cursor = world.getInitialsEntry().getCursor();
        int letterSpacing = 60;
        int totalW = letterSpacing * letters.length;
        int startX = GameConfig.WIDTH / 2 - totalW / 2;
        int baselineY = 300;
        for (int i = 0; i < letters.length; i++) {
            int x = startX + i * letterSpacing;
            if (i == cursor) {
                g.setColor(YELLOW);
                g.drawRect(x - 4, baselineY - 50, 50, 70);
            }
            g.setColor(i == cursor ? YELLOW : WHITE);
            g.drawString(String.valueOf(letters[i]), x, baselineY);
        }

        g.setFont(hudFont);
        g.setColor(CYAN);
        String[] hint = {
                "UP / DOWN  change letter",
                "LEFT / RIGHT  move cursor",
                "ENTER  confirm letter"
        };
        int hy = 380;
        for (String line : hint) {
            int w = g.getFontMetrics().stringWidth(line);
            g.drawString(line, GameConfig.WIDTH / 2 - w / 2, hy);
            hy += 24;
        }
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

    @SuppressWarnings("unused")
    private static Composite alpha(Graphics2D g, float a) {
        Composite prev = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a));
        return prev;
    }

    @SuppressWarnings("unused")
    private static void enableSmoothing(Graphics2D g, boolean smooth) {
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                smooth ? RenderingHints.VALUE_INTERPOLATION_BILINEAR
                        : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }
}
