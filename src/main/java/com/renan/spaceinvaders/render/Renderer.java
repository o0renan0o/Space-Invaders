package com.renan.spaceinvaders.render;

import com.renan.spaceinvaders.assets.AssetManager;
import com.renan.spaceinvaders.core.GameConfig;
import com.renan.spaceinvaders.core.GameState;
import com.renan.spaceinvaders.ui.FaceState;
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
        int barW = GameConfig.WIDTH - 260;
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
        drawStatusBar(g);
        drawBossHud(g);
    }

    private void drawStatusBar(Graphics2D g) {
        int top = GameConfig.STBAR_TOP;
        int height = GameConfig.STBAR_HEIGHT;

        g.setColor(new Color(14, 14, 30));
        g.fillRect(0, top, GameConfig.WIDTH, height);

        g.setColor(new Color(80, 200, 100));
        g.fillRect(0, top - 1, GameConfig.WIDTH, 2);
        g.setColor(new Color(40, 100, 60));
        g.fillRect(0, top + 3, GameConfig.WIDTH, 1);
        g.fillRect(0, top + height - 4, GameConfig.WIDTH, 1);

        int[] regionEdges = {170, 340, 460, 620};
        g.setColor(new Color(60, 60, 100));
        for (int x : regionEdges) g.fillRect(x, top + 6, 1, height - 12);

        drawScoreRegion(g, 0, 170, top, height);
        drawWaveRegion(g, 170, 170, top, height);
        drawCockpitRegion(g, 340, 120, top, height);
        drawComboRegion(g, 460, 160, top, height);
        drawPowerUpsRegion(g, 620, 180, top, height);
    }

    private void drawScoreRegion(Graphics2D g, int x, int w, int top, int height) {
        g.setFont(smallFont);
        g.setColor(new Color(120, 200, 255));
        g.drawString("SCORE", x + 12, top + 18);
        g.setFont(hudFont);
        g.setColor(YELLOW);
        g.drawString(pad(world.getScore(), 6), x + 12, top + 38);
        g.setFont(smallFont);
        g.setColor(new Color(120, 200, 255));
        g.drawString("HI", x + 12, top + 54);
        g.setColor(new Color(255, 220, 120));
        g.drawString(pad(world.getHighScore(), 6), x + 36, top + 54);
    }

    private void drawWaveRegion(Graphics2D g, int x, int w, int top, int height) {
        g.setFont(smallFont);
        g.setColor(new Color(120, 200, 255));
        g.drawString("WAVE", x + 12, top + 18);
        g.setFont(hudFont);
        g.setColor(CYAN);
        g.drawString(Integer.toString(world.getWave()), x + 60, top + 18);
        g.setFont(smallFont);
        g.setColor(YELLOW);
        String name = world.getCurrentPhase().name();
        if (name.length() > 18) name = name.substring(0, 18);
        g.drawString(name, x + 12, top + 36);

        g.setColor(new Color(120, 200, 255));
        g.drawString("LIVES", x + 12, top + 56);
        g.setFont(hudFont);
        g.setColor(WHITE);
        int lives = Math.max(0, world.getPlayer().getLives());
        g.drawString("x" + lives, x + 52, top + 56);

        int iconY = top + 46;
        int iconSize = 14;
        for (int i = 0; i < Math.min(lives, 5); i++) {
            drawShip(g, x + 86 + i * (iconSize + 2), iconY, iconSize, iconSize / 2 + 1);
        }

        g.setFont(smallFont);
        g.setColor(new Color(255, 204, 51));
        g.drawString("$" + world.getCoinsEarned(), x + w - 42, top + 36);
    }

    private void drawCockpitRegion(Graphics2D g, int x, int w, int top, int height) {
        int cockpitW = 100;
        int cockpitH = height - 8;
        int cockpitX = x + (w - cockpitW) / 2;
        int cockpitY = top + 4;
        BufferedImage cockpit = assets.get("cockpit");
        if (cockpit != null) {
            g.drawImage(cockpit, cockpitX, cockpitY, cockpitW, cockpitH, null);
        } else {
            g.setColor(new Color(40, 30, 60));
            g.fillRect(cockpitX, cockpitY, cockpitW, cockpitH);
            g.setColor(new Color(100, 100, 140));
            g.drawRect(cockpitX, cockpitY, cockpitW, cockpitH);
        }

        int lives = Math.max(0, world.getPlayer().getLives());
        boolean invuln = world.getPlayer().isInvulnerable();
        com.renan.spaceinvaders.ui.FaceState state = world.getFaceController().compute(
                world.getState(), lives, invuln);

        int faceSize = Math.min(cockpitW - 24, cockpitH - 10);
        int faceX = cockpitX + (cockpitW - faceSize) / 2;
        int faceY = cockpitY + (cockpitH - faceSize) / 2;

        BufferedImage face = resolveFace(state, lives);
        if (face != null) {
            g.drawImage(face, faceX, faceY, faceSize, faceSize, null);
        } else {
            g.setColor(new Color(200, 150, 100));
            g.fillRect(faceX, faceY, faceSize, faceSize);
        }

        com.renan.spaceinvaders.assets.FaceSheet sheet = assets.getFaceSheet();
        if (sheet == null || !sheet.isLoaded()) {
            int startingLives = Math.max(1, world.getDifficulty().startingLives);
            double damage = 1.0 - (lives / (double) startingLives);
            if (damage > 0 && state != com.renan.spaceinvaders.ui.FaceState.DEAD) {
                int alpha = (int) Math.min(140, damage * 130);
                g.setColor(new Color(255, 40, 40, alpha));
                g.fillRect(faceX, faceY, faceSize, faceSize);
            }
        }
    }

    private BufferedImage resolveFace(com.renan.spaceinvaders.ui.FaceState state, int lives) {
        com.renan.spaceinvaders.assets.FaceSheet sheet = assets.getFaceSheet();
        if (sheet != null && sheet.isLoaded()) {
            if (state == com.renan.spaceinvaders.ui.FaceState.DEAD) return sheet.dead();
            if (state == com.renan.spaceinvaders.ui.FaceState.GOD) return sheet.god();
            int row = state == com.renan.spaceinvaders.ui.FaceState.WIN
                    ? 0
                    : com.renan.spaceinvaders.assets.FaceSheet.healthRowForLives(lives);
            return sheet.get(row, state.doomColumn);
        }
        return assets.get(state.spriteKey);
    }

    private void drawComboRegion(Graphics2D g, int x, int w, int top, int height) {
        g.setFont(smallFont);
        g.setColor(new Color(120, 200, 255));
        g.drawString("COMBO", x + 12, top + 18);

        int mult = world.getCombo().getMultiplier();
        Color comboColor = mult >= 4 ? YELLOW : (mult > 1 ? WHITE : new Color(120, 120, 140));
        g.setFont(hudFont);
        g.setColor(comboColor);
        g.drawString("x" + mult, x + 60, top + 22);

        int barX = x + 12;
        int barY = top + 32;
        int barW = w - 24;
        g.setColor(new Color(40, 40, 60));
        g.fillRect(barX, barY, barW, 6);
        g.setColor(comboColor);
        g.fillRect(barX, barY, (int) (barW * world.getCombo().getWindowFraction()), 6);

        g.setFont(smallFont);
        g.setColor(new Color(120, 200, 255));
        g.drawString("DIFFICULTY", x + 12, top + 52);
        g.setColor(YELLOW);
        g.drawString(world.getDifficulty().label, x + 86, top + 52);
    }

    private void drawPowerUpsRegion(Graphics2D g, int x, int w, int top, int height) {
        g.setFont(smallFont);
        g.setColor(new Color(120, 200, 255));
        g.drawString("POWER-UPS", x + 12, top + 18);

        var active = world.getActivePowerUps().snapshot();
        if (active.isEmpty()) {
            g.setColor(new Color(80, 80, 100));
            g.drawString("(none)", x + 12, top + 42);
            return;
        }

        int slotW = 30;
        int slotX = x + 12;
        int slotY = top + 24;
        for (var e : active.entrySet()) {
            com.renan.spaceinvaders.world.PowerUpType t = e.getKey();
            BufferedImage img = assets.get(t.spriteKey);
            if (img != null) {
                g.drawImage(img, slotX, slotY, 24, 24, null);
            } else {
                g.setColor(t.color);
                g.fillRect(slotX, slotY, 24, 24);
            }
            float frac = world.getActivePowerUps().fractionLeft(t);
            g.setColor(new Color(40, 40, 60));
            g.fillRect(slotX, slotY + 26, 24, 3);
            g.setColor(t.color);
            g.fillRect(slotX, slotY + 26, (int) (24 * frac), 3);
            g.setFont(smallFont);
            g.setColor(new Color(200, 200, 220));
            String label = t.label.length() > 6 ? t.label.substring(0, 6) : t.label;
            int lw = g.getFontMetrics().stringWidth(label);
            g.drawString(label, slotX + 12 - lw / 2, slotY + 40);
            slotX += slotW + 6;
            if (slotX + slotW > x + w) break;
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
            case SHOP -> drawShop(g);
            case PAUSED -> drawCenter(g, "PAUSED", "Press P or ESC to resume   Q to quit");
            case WAVE_CLEARED -> drawCenter(g, "WAVE CLEARED!",
                    "+" + world.getCurrentPhase().clearBonus() + " bonus");
            case GAME_OVER -> drawCenter(g, "GAME OVER",
                    "SCORE " + world.getScore() + "    +" + world.getCoinsEarned() + " coins    Press ENTER");
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
                "SHOP            S",
                "",
                "SCORING",
                "Squid 30   Crab 20   Octopus 10",
                "Armored 50   UFO 150   Boss 1000+",
                "",
                "POWER-UPS: 1UP, RAPID, DOUBLE, PIERCE,",
                "SLOW, SHIELD, DAMAGE, MAGNET, NUKE,",
                "REFLECT, GHOST"
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

    private void drawShop(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 210));
        g.fillRect(0, 0, GameConfig.WIDTH, GameConfig.HEIGHT);

        g.setFont(bigFont);
        g.setColor(YELLOW);
        String title = "ARMOURY";
        int tw = g.getFontMetrics().stringWidth(title);
        g.drawString(title, GameConfig.WIDTH / 2 - tw / 2, 80);

        com.renan.spaceinvaders.meta.Profile profile = world.getProfile();
        g.setFont(mediumFont);
        g.setColor(new Color(0xFFCC33));
        String coinLine = profile.getCoins() + " coins";
        int cw = g.getFontMetrics().stringWidth(coinLine);
        g.drawString(coinLine, GameConfig.WIDTH / 2 - cw / 2, 115);

        g.setFont(hudFont);
        g.setColor(new Color(160, 160, 200));
        String earnedLine = "Total earned: " + profile.getTotalCoinsEarned();
        int ew = g.getFontMetrics().stringWidth(earnedLine);
        g.drawString(earnedLine, GameConfig.WIDTH / 2 - ew / 2, 138);

        com.renan.spaceinvaders.meta.Upgrade[] upgrades = com.renan.spaceinvaders.meta.Upgrade.values();
        int selected = world.getShopIndex();
        int rowY = 180;
        int rowH = 64;
        int boxW = 560;
        int boxX = (GameConfig.WIDTH - boxW) / 2;
        for (int i = 0; i < upgrades.length; i++) {
            com.renan.spaceinvaders.meta.Upgrade u = upgrades[i];
            int level = profile.getLevel(u);
            boolean maxed = level >= u.maxLevel;
            int cost = maxed ? 0 : u.costAtLevel(level);
            boolean canBuy = !maxed && profile.getCoins() >= cost;
            boolean active = i == selected;

            g.setColor(active ? new Color(50, 70, 120, 220) : new Color(20, 25, 50, 200));
            g.fillRect(boxX, rowY, boxW, rowH - 8);
            g.setColor(active ? YELLOW : new Color(60, 60, 90));
            g.drawRect(boxX, rowY, boxW, rowH - 8);

            g.setFont(hudFont);
            g.setColor(active ? YELLOW : WHITE);
            g.drawString(u.label, boxX + 14, rowY + 22);

            g.setFont(smallFont);
            g.setColor(new Color(180, 200, 230));
            g.drawString(u.description, boxX + 14, rowY + 40);

            g.setFont(hudFont);
            String levelStr = "Lvl " + level + "/" + u.maxLevel;
            int lw = g.getFontMetrics().stringWidth(levelStr);
            g.setColor(WHITE);
            g.drawString(levelStr, boxX + boxW - lw - 110, rowY + 22);

            String costStr = maxed ? "MAX" : (cost + "c");
            int xw = g.getFontMetrics().stringWidth(costStr);
            g.setColor(maxed ? new Color(120, 200, 120) : canBuy ? new Color(0xFFCC33) : new Color(200, 80, 80));
            g.drawString(costStr, boxX + boxW - xw - 14, rowY + 22);

            rowY += rowH;
        }

        g.setFont(hudFont);
        g.setColor(CYAN);
        String hint = "UP/DOWN select   ENTER buy   ESC/S back";
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
