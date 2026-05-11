package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.assets.SoundManager;
import com.renan.spaceinvaders.collision.CollisionSystem;
import com.renan.spaceinvaders.core.GameConfig;
import com.renan.spaceinvaders.core.GameState;
import com.renan.spaceinvaders.input.InputHandler;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class World {

    private GameState state = GameState.MENU;
    private final Player player = new Player();
    private final List<Alien> aliens = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private final List<Shield> shields = new ArrayList<>();
    private final List<Explosion> explosions = new ArrayList<>();
    private Ufo ufo;

    private int score;
    private int highScore;
    private int wave = 1;
    private double alienSpeed;
    private int alienDirection = 1;
    private int alienFireTicks;
    private int alienAnimAccum;
    private int ufoTimer;
    private int waveClearedDelay;
    private int gameOverDelay;

    private final Random rng = new Random();

    public World(int highScore) {
        this.highScore = highScore;
        spawnShields();
    }

    public void update(InputHandler input, SoundManager sounds) {
        switch (state) {
            case MENU -> updateMenu(input);
            case PLAYING -> updatePlaying(input, sounds);
            case PAUSED -> updatePaused(input);
            case WAVE_CLEARED -> updateWaveCleared();
            case GAME_OVER -> updateGameOver(input);
        }
    }

    private void updateMenu(InputHandler input) {
        if (input.consumePressed(KeyEvent.VK_ENTER) || input.consumePressed(KeyEvent.VK_SPACE)) {
            startNewGame();
        }
    }

    private void updatePlaying(InputHandler input, SoundManager sounds) {
        if (input.consumePressed(KeyEvent.VK_ESCAPE) || input.consumePressed(KeyEvent.VK_P)) {
            state = GameState.PAUSED;
            return;
        }
        if (input.consumePressed(KeyEvent.VK_M)) {
            sounds.toggleMute();
        }

        if (input.isDown(KeyEvent.VK_LEFT) || input.isDown(KeyEvent.VK_A)) {
            player.moveLeft();
        }
        if (input.isDown(KeyEvent.VK_RIGHT) || input.isDown(KeyEvent.VK_D)) {
            player.moveRight();
        }
        if (input.isDown(KeyEvent.VK_SPACE) && player.canFire()) {
            bullets.add(player.fire());
            sounds.play("shot");
        }
        player.tick();

        moveAliens();
        animateAliens();
        maybeAlienFire(sounds);
        updateUfo();

        for (Bullet b : bullets) b.update();
        bullets.removeIf(b -> !b.isAlive());

        CollisionSystem.handle(this, sounds);

        explosions.removeIf(e -> {
            e.tick();
            return !e.isAlive();
        });

        if (aliens.isEmpty()) {
            state = GameState.WAVE_CLEARED;
            waveClearedDelay = 120;
            return;
        }
        for (Alien a : aliens) {
            if (a.getY() + GameConfig.ALIEN_HEIGHT >= GameConfig.ALIEN_GAME_OVER_Y) {
                triggerGameOver(sounds);
                return;
            }
        }
        if (player.getLives() <= 0) {
            triggerGameOver(sounds);
        }
    }

    private void triggerGameOver(SoundManager sounds) {
        if (state == GameState.GAME_OVER) return;
        state = GameState.GAME_OVER;
        gameOverDelay = 60;
        sounds.play("player_died");
        if (score > highScore) highScore = score;
    }

    private void updatePaused(InputHandler input) {
        if (input.consumePressed(KeyEvent.VK_ESCAPE) || input.consumePressed(KeyEvent.VK_P)) {
            state = GameState.PLAYING;
        }
        if (input.consumePressed(KeyEvent.VK_Q)) {
            state = GameState.MENU;
        }
    }

    private void updateWaveCleared() {
        waveClearedDelay--;
        if (waveClearedDelay <= 0) {
            wave++;
            bullets.clear();
            ufo = null;
            spawnWave();
            state = GameState.PLAYING;
        }
    }

    private void updateGameOver(InputHandler input) {
        if (gameOverDelay > 0) {
            gameOverDelay--;
            return;
        }
        if (input.consumePressed(KeyEvent.VK_ENTER) || input.consumePressed(KeyEvent.VK_SPACE)) {
            state = GameState.MENU;
        }
    }

    public void startNewGame() {
        score = 0;
        wave = 1;
        player.reset();
        bullets.clear();
        explosions.clear();
        ufo = null;
        spawnShields();
        spawnWave();
        state = GameState.PLAYING;
    }

    private void spawnShields() {
        shields.clear();
        int shieldWidthPx = GameConfig.SHIELD_COLS * GameConfig.SHIELD_CELL;
        int totalWidth = shieldWidthPx * GameConfig.SHIELD_COUNT;
        int spacing = (GameConfig.WIDTH - totalWidth) / (GameConfig.SHIELD_COUNT + 1);
        for (int i = 0; i < GameConfig.SHIELD_COUNT; i++) {
            int x = spacing + i * (shieldWidthPx + spacing);
            shields.add(new Shield(x, GameConfig.SHIELD_Y));
        }
    }

    private void spawnWave() {
        aliens.clear();
        int totalWidth = GameConfig.ALIEN_COLS * GameConfig.ALIEN_WIDTH
                + (GameConfig.ALIEN_COLS - 1) * GameConfig.ALIEN_H_SPACING;
        int startX = (GameConfig.WIDTH - totalWidth) / 2;
        int startY = GameConfig.ALIEN_START_Y + Math.min((wave - 1) * 10, 80);
        for (int r = 0; r < GameConfig.ALIEN_ROWS; r++) {
            for (int c = 0; c < GameConfig.ALIEN_COLS; c++) {
                int x = startX + c * (GameConfig.ALIEN_WIDTH + GameConfig.ALIEN_H_SPACING);
                int y = startY + r * (GameConfig.ALIEN_HEIGHT + GameConfig.ALIEN_V_SPACING);
                aliens.add(new Alien(r, c, x, y));
            }
        }
        alienDirection = 1;
        alienSpeed = GameConfig.ALIEN_START_SPEED + (wave - 1) * GameConfig.ALIEN_SPEED_RAMP * 4;
        alienFireTicks = randomFireDelay();
        ufoTimer = randomUfoInterval();
        alienAnimAccum = 0;
    }

    private void moveAliens() {
        if (aliens.isEmpty()) return;
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        for (Alien a : aliens) {
            minX = Math.min(minX, a.getX());
            maxX = Math.max(maxX, a.getX() + GameConfig.ALIEN_WIDTH);
        }
        double total = GameConfig.ALIEN_ROWS * GameConfig.ALIEN_COLS;
        double aliveFrac = aliens.size() / total;
        double speed = alienSpeed * (1.0 + (1.0 - aliveFrac) * 2.5);
        double dx = alienDirection * speed;

        boolean drop = false;
        if (maxX + dx > GameConfig.WIDTH - 4 || minX + dx < 4) {
            drop = true;
            alienDirection *= -1;
        }
        for (Alien a : aliens) {
            if (drop) a.move(0, GameConfig.ALIEN_DROP);
            else a.move(dx, 0);
        }
    }

    private void animateAliens() {
        alienAnimAccum++;
        if (alienAnimAccum >= GameConfig.ALIEN_FRAME_TICKS) {
            alienAnimAccum = 0;
            for (Alien a : aliens) a.toggleFrame();
        }
    }

    private void maybeAlienFire(SoundManager sounds) {
        alienFireTicks--;
        if (alienFireTicks > 0) return;
        Alien shooter = pickBottomAlien();
        if (shooter != null) {
            double bx = shooter.getX() + (GameConfig.ALIEN_WIDTH - GameConfig.BULLET_WIDTH) / 2.0;
            double by = shooter.getY() + GameConfig.ALIEN_HEIGHT;
            bullets.add(new Bullet(bx, by, GameConfig.ALIEN_BULLET_SPEED, Bullet.Side.ALIEN));
            sounds.play("alien_shot");
        }
        alienFireTicks = randomFireDelay();
    }

    private Alien pickBottomAlien() {
        if (aliens.isEmpty()) return null;
        Map<Integer, Alien> byCol = new HashMap<>();
        for (Alien a : aliens) {
            byCol.merge(a.getCol(), a,
                    (existing, incoming) -> existing.getY() > incoming.getY() ? existing : incoming);
        }
        List<Alien> bottoms = new ArrayList<>(byCol.values());
        return bottoms.get(rng.nextInt(bottoms.size()));
    }

    private void updateUfo() {
        if (ufo != null) {
            ufo.update();
            if (!ufo.isAlive()) ufo = null;
            return;
        }
        ufoTimer--;
        if (ufoTimer <= 0) {
            ufo = new Ufo(rng.nextBoolean());
            ufoTimer = randomUfoInterval();
        }
    }

    private int randomUfoInterval() {
        int span = GameConfig.UFO_MAX_INTERVAL_TICKS - GameConfig.UFO_MIN_INTERVAL_TICKS;
        return GameConfig.UFO_MIN_INTERVAL_TICKS + rng.nextInt(span);
    }

    private int randomFireDelay() {
        int span = GameConfig.ALIEN_FIRE_MAX_TICKS - GameConfig.ALIEN_FIRE_MIN_TICKS;
        int delay = GameConfig.ALIEN_FIRE_MIN_TICKS + rng.nextInt(span);
        return Math.max(10, delay - wave * 3);
    }

    public List<Alien> getAliens() {
        return aliens;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public List<Shield> getShields() {
        return shields;
    }

    public List<Explosion> getExplosions() {
        return explosions;
    }

    public Player getPlayer() {
        return player;
    }

    public Ufo getUfo() {
        return ufo;
    }

    public GameState getState() {
        return state;
    }

    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }

    public int getWave() {
        return wave;
    }

    public void addScore(int n) {
        score += n;
        if (score > highScore) highScore = score;
    }

    public void spawnExplosion(int x, int y) {
        explosions.add(new Explosion(x, y));
    }

    public void clearUfo() {
        ufo = null;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public void setState(GameState state) {
        this.state = state;
    }
}
