package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.assets.SoundManager;
import com.renan.spaceinvaders.collision.CollisionSystem;
import com.renan.spaceinvaders.core.Difficulty;
import com.renan.spaceinvaders.core.GameConfig;
import com.renan.spaceinvaders.core.GameState;
import com.renan.spaceinvaders.core.Mechanic;
import com.renan.spaceinvaders.core.Phase;
import com.renan.spaceinvaders.core.Phases;
import com.renan.spaceinvaders.input.InputHandler;
import com.renan.spaceinvaders.ui.HallOfFame;
import com.renan.spaceinvaders.ui.InitialsEntry;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class World {

    private GameState state = GameState.MENU;
    private Difficulty difficulty = Difficulty.NORMAL;

    private final Player player = new Player(Difficulty.NORMAL.startingLives);
    private final List<Alien> aliens = new ArrayList<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private final List<Shield> shields = new ArrayList<>();
    private final List<Explosion> explosions = new ArrayList<>();
    private final List<Particle> particles = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
    private final List<ScorePopup> popups = new ArrayList<>();
    private final ActivePowerUps active = new ActivePowerUps();
    private final Combo combo = new Combo();
    private final CameraShake cameraShake = new CameraShake();
    private final StarField starField = new StarField();
    private final HallOfFame hallOfFame;
    private final InitialsEntry initialsEntry = new InitialsEntry();
    private Ufo ufo;
    private Boss boss;

    private int score;
    private int highScore;
    private int wave = 1;
    private Phase currentPhase = Phases.LIST.get(0);
    private int alienDirection = 1;
    private int alienFireTicks;
    private int alienAnimAccum;
    private int ufoTimer;
    private int diveTimer;
    private int waveClearedDelay;
    private int gameOverDelay;
    private int nextRewardIndex;
    private boolean savedThisGame;

    private final Random rng = new Random();

    public World(int highScore) {
        this.highScore = highScore;
        this.hallOfFame = new HallOfFame();
        if (hallOfFame.topScore() > highScore) this.highScore = hallOfFame.topScore();
        spawnShields(GameConfig.SHIELD_COUNT);
    }

    public void update(InputHandler input, SoundManager sounds) {
        starField.update();
        switch (state) {
            case MENU -> updateMenu(input);
            case HALL_OF_FAME -> updateHallOfFame(input);
            case PLAYING -> updatePlaying(input, sounds);
            case PAUSED -> updatePaused(input);
            case WAVE_CLEARED -> updateWaveCleared();
            case GAME_OVER -> updateGameOver(input);
            case ENTERING_INITIALS -> updateInitials(input);
            case BOSS_INTRO, DIFFICULTY_SELECT -> state = GameState.MENU;
        }
    }

    private void updateMenu(InputHandler input) {
        if (input.consumePressed(KeyEvent.VK_ENTER) || input.consumePressed(KeyEvent.VK_SPACE)) {
            startNewGame();
        } else if (input.consumePressed(KeyEvent.VK_D)) {
            difficulty = difficulty.next();
        } else if (input.consumePressed(KeyEvent.VK_H)) {
            state = GameState.HALL_OF_FAME;
        }
    }

    private void updateHallOfFame(InputHandler input) {
        if (input.consumePressed(KeyEvent.VK_ESCAPE)
                || input.consumePressed(KeyEvent.VK_ENTER)
                || input.consumePressed(KeyEvent.VK_SPACE)
                || input.consumePressed(KeyEvent.VK_H)) {
            state = GameState.MENU;
        }
    }

    private void updatePlaying(InputHandler input, SoundManager sounds) {
        if (input.consumePressed(KeyEvent.VK_ESCAPE) || input.consumePressed(KeyEvent.VK_P)) {
            state = GameState.PAUSED;
            return;
        }
        if (input.consumePressed(KeyEvent.VK_M)) sounds.toggleMute();

        if (input.isDown(KeyEvent.VK_LEFT) || input.isDown(KeyEvent.VK_A)) player.moveLeft();
        if (input.isDown(KeyEvent.VK_RIGHT) || input.isDown(KeyEvent.VK_D)) player.moveRight();
        if (input.isDown(KeyEvent.VK_SPACE) && player.canFire()) {
            bullets.addAll(player.fire(active));
            sounds.play("shot");
        }
        player.tick();
        active.tick();
        combo.tick();
        cameraShake.tick();

        moveAliens();
        animateAliens();
        updateDivers();
        maybeAlienFire(sounds);
        updateUfo();
        updateBoss(sounds);

        for (Bullet b : bullets) b.update();
        bullets.removeIf(b -> !b.isAlive());

        for (PowerUp p : powerUps) p.update();
        powerUps.removeIf(p -> !p.isAlive());

        for (Particle p : particles) p.update();
        particles.removeIf(p -> !p.isAlive());

        explosions.removeIf(e -> {
            e.tick();
            return !e.isAlive();
        });

        for (ScorePopup p : popups) p.update();
        popups.removeIf(p -> !p.isAlive());

        CollisionSystem.handle(this, sounds);

        boolean bossPhase = currentPhase.has(Mechanic.BOSS);
        boolean bossDone = !bossPhase || (boss == null);
        if (aliens.isEmpty() && bossDone) {
            grantWaveBonus();
            state = GameState.WAVE_CLEARED;
            waveClearedDelay = 120;
            return;
        }
        for (Alien a : aliens) {
            if (a.isDiving()) continue;
            if (a.getY() + GameConfig.ALIEN_HEIGHT >= GameConfig.ALIEN_GAME_OVER_Y) {
                triggerGameOver(sounds);
                return;
            }
        }
        if (player.getLives() <= 0) triggerGameOver(sounds);
    }

    private void grantWaveBonus() {
        int bonus = currentPhase.clearBonus();
        addScore(bonus);
        addPopup(GameConfig.WIDTH / 2 - 60, GameConfig.HEIGHT / 2,
                "WAVE CLEAR +" + bonus, new Color(0x33FF66));
    }

    private void triggerGameOver(SoundManager sounds) {
        if (state == GameState.GAME_OVER || state == GameState.ENTERING_INITIALS) return;
        state = GameState.GAME_OVER;
        gameOverDelay = 90;
        sounds.play("player_died");
        cameraShake.shake(40, GameConfig.CAMERA_SHAKE_PLAYER);
        if (score > highScore) highScore = score;
    }

    private void updatePaused(InputHandler input) {
        if (input.consumePressed(KeyEvent.VK_ESCAPE) || input.consumePressed(KeyEvent.VK_P)) {
            state = GameState.PLAYING;
        }
        if (input.consumePressed(KeyEvent.VK_Q)) {
            state = GameState.MENU;
            active.clear();
        }
    }

    private void updateWaveCleared() {
        waveClearedDelay--;
        if (waveClearedDelay <= 0) {
            wave++;
            bullets.clear();
            ufo = null;
            boss = null;
            spawnWave();
            state = GameState.PLAYING;
        }
    }

    private void updateGameOver(InputHandler input) {
        if (gameOverDelay > 0) {
            gameOverDelay--;
            return;
        }
        if (!savedThisGame && hallOfFame.qualifies(score)) {
            savedThisGame = true;
            initialsEntry.reset();
            state = GameState.ENTERING_INITIALS;
            return;
        }
        if (input.consumePressed(KeyEvent.VK_ENTER) || input.consumePressed(KeyEvent.VK_SPACE)) {
            state = GameState.MENU;
        }
    }

    private void updateInitials(InputHandler input) {
        if (input.consumePressed(KeyEvent.VK_LEFT) || input.consumePressed(KeyEvent.VK_A)) {
            initialsEntry.left();
        }
        if (input.consumePressed(KeyEvent.VK_RIGHT) || input.consumePressed(KeyEvent.VK_D)) {
            initialsEntry.right();
        }
        if (input.consumePressed(KeyEvent.VK_UP) || input.consumePressed(KeyEvent.VK_W)) {
            initialsEntry.prev();
        }
        if (input.consumePressed(KeyEvent.VK_DOWN) || input.consumePressed(KeyEvent.VK_S)) {
            initialsEntry.next();
        }
        if (input.consumePressed(KeyEvent.VK_ENTER) || input.consumePressed(KeyEvent.VK_SPACE)) {
            if (initialsEntry.advance()) {
                hallOfFame.add(initialsEntry.asString(), score);
                state = GameState.HALL_OF_FAME;
            }
        }
    }

    public void startNewGame() {
        score = 0;
        wave = 1;
        nextRewardIndex = 0;
        savedThisGame = false;
        player.setStartingLives(difficulty.startingLives);
        player.reset();
        bullets.clear();
        explosions.clear();
        particles.clear();
        powerUps.clear();
        popups.clear();
        active.clear();
        combo.breakCombo();
        ufo = null;
        boss = null;
        spawnWave();
        state = GameState.PLAYING;
    }

    private void spawnShields(int count) {
        shields.clear();
        if (count <= 0) return;
        int shieldWidthPx = GameConfig.SHIELD_COLS * GameConfig.SHIELD_CELL;
        int totalWidth = shieldWidthPx * count;
        int spacing = (GameConfig.WIDTH - totalWidth) / (count + 1);
        for (int i = 0; i < count; i++) {
            int x = spacing + i * (shieldWidthPx + spacing);
            shields.add(new Shield(x, GameConfig.SHIELD_Y));
        }
    }

    private void spawnWave() {
        currentPhase = Phases.forWave(wave);
        aliens.clear();

        int cols = currentPhase.cols();
        int rows = currentPhase.rows();
        int spacingH = currentPhase.has(Mechanic.NARROW_FORMATION)
                ? GameConfig.ALIEN_H_SPACING / 2
                : GameConfig.ALIEN_H_SPACING;
        int totalWidth = cols * GameConfig.ALIEN_WIDTH + (cols - 1) * spacingH;
        int startX = (GameConfig.WIDTH - totalWidth) / 2;
        int startY = computeAlienStartY(currentPhase, wave);

        boolean armored = currentPhase.has(Mechanic.ARMORED_FRONT_ROWS);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = startX + c * (GameConfig.ALIEN_WIDTH + spacingH);
                int y = startY + r * (GameConfig.ALIEN_HEIGHT + GameConfig.ALIEN_V_SPACING);
                AlienType type = AlienType.forRow(r, rows, armored);
                aliens.add(new Alien(r, c, x, y, type));
            }
        }

        int shieldCount = currentPhase.has(Mechanic.FEWER_SHIELDS)
                ? Math.min(currentPhase.shieldCount(), 2)
                : currentPhase.shieldCount();
        spawnShields(shieldCount);

        alienDirection = 1;
        alienFireTicks = randomFireDelay();
        ufoTimer = computeUfoInterval();
        diveTimer = 60 * 6;
        alienAnimAccum = 0;

        if (currentPhase.has(Mechanic.BOSS)) {
            boss = new Boss(wave);
            cameraShake.shake(30, GameConfig.CAMERA_SHAKE_BOSS);
            addPopup(GameConfig.WIDTH / 2 - 60, 120, "WARNING!! BOSS", new Color(0xFF3344));
        } else {
            boss = null;
        }
    }

    private void moveAliens() {
        if (aliens.isEmpty()) return;
        List<Alien> formationAliens = new ArrayList<>();
        for (Alien a : aliens) if (!a.isDiving()) formationAliens.add(a);
        if (formationAliens.isEmpty()) return;

        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        for (Alien a : formationAliens) {
            minX = Math.min(minX, a.getX());
            maxX = Math.max(maxX, a.getX() + GameConfig.ALIEN_WIDTH);
        }
        double total = currentPhase.rows() * currentPhase.cols();
        double aliveFrac = formationAliens.size() / total;
        double slow = active.isActive(PowerUpType.SLOW_MO) ? 0.5 : 1.0;
        double speed = GameConfig.ALIEN_START_SPEED
                * currentPhase.speedMultiplier()
                * difficulty.speedMultiplier
                * (1.0 + (1.0 - aliveFrac) * 2.5)
                * slow;
        double dx = alienDirection * speed;
        boolean hitRight = dx > 0 && maxX + dx > GameConfig.WIDTH - 4;
        boolean hitLeft = dx < 0 && minX + dx < 4;
        boolean drop = hitRight || hitLeft;
        if (drop) alienDirection *= -1;
        for (Alien a : formationAliens) {
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

    private void updateDivers() {
        for (Alien a : aliens) a.diveStep();
        if (!currentPhase.has(Mechanic.DIVERS)) return;
        diveTimer--;
        if (diveTimer > 0) return;
        diveTimer = 60 * 6 + rng.nextInt(120);
        Alien candidate = pickRandomAlien();
        if (candidate == null) return;
        double vx = (player.getX() - candidate.getX()) > 0 ? GameConfig.DIVER_SPEED * 0.4 : -GameConfig.DIVER_SPEED * 0.4;
        candidate.startDive(vx, GameConfig.DIVER_SPEED);
    }

    private void maybeAlienFire(SoundManager sounds) {
        alienFireTicks--;
        if (alienFireTicks > 0) return;
        int simultaneous = currentPhase.simultaneousAlienBullets();
        for (int i = 0; i < simultaneous; i++) {
            Alien shooter = pickBottomAlien();
            if (shooter == null) break;
            double bx = shooter.getX() + (GameConfig.ALIEN_WIDTH - GameConfig.BULLET_WIDTH) / 2.0;
            double by = shooter.getY() + GameConfig.ALIEN_HEIGHT;
            Bullet b = new Bullet(bx, by, GameConfig.ALIEN_BULLET_SPEED, Bullet.Side.ALIEN);
            if (currentPhase.has(Mechanic.SPLITTING_BULLETS)) b.splittable();
            bullets.add(b);
        }
        sounds.play("alien_shot");
        alienFireTicks = randomFireDelay();
    }

    private Alien pickBottomAlien() {
        if (aliens.isEmpty()) return null;
        Map<Integer, Alien> byCol = new HashMap<>();
        for (Alien a : aliens) {
            if (a.isDiving()) continue;
            byCol.merge(a.getCol(), a,
                    (existing, incoming) -> existing.getY() > incoming.getY() ? existing : incoming);
        }
        if (byCol.isEmpty()) return null;
        List<Alien> bottoms = new ArrayList<>(byCol.values());
        return bottoms.get(rng.nextInt(bottoms.size()));
    }

    private Alien pickRandomAlien() {
        List<Alien> formation = new ArrayList<>();
        for (Alien a : aliens) if (!a.isDiving()) formation.add(a);
        if (formation.isEmpty()) return null;
        formation.sort(Comparator.comparingDouble(Alien::getY).reversed());
        int range = Math.min(10, formation.size());
        return formation.get(rng.nextInt(range));
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
            ufoTimer = computeUfoInterval();
        }
    }

    private void updateBoss(SoundManager sounds) {
        if (boss == null) return;
        boss.update();
        if (boss.readyToFire()) {
            double bx = boss.getX() + GameConfig.BOSS_WIDTH / 2.0 - GameConfig.BULLET_WIDTH / 2.0;
            double by = boss.getY() + GameConfig.BOSS_HEIGHT;
            bullets.add(new Bullet(bx, by, GameConfig.ALIEN_BULLET_SPEED * 1.3, Bullet.Side.ALIEN));
            double angle = 0.6;
            bullets.add(new Bullet(bx, by, -angle, GameConfig.ALIEN_BULLET_SPEED * 1.1, Bullet.Side.ALIEN));
            bullets.add(new Bullet(bx, by, angle, GameConfig.ALIEN_BULLET_SPEED * 1.1, Bullet.Side.ALIEN));
            sounds.play("alien_shot");
        }
    }

    private int computeUfoInterval() {
        int min = GameConfig.UFO_MIN_INTERVAL_TICKS;
        int max = GameConfig.UFO_MAX_INTERVAL_TICKS;
        if (currentPhase.has(Mechanic.FREQUENT_UFO)) {
            min /= 3;
            max /= 3;
        }
        return min + rng.nextInt(Math.max(1, max - min));
    }

    public static int computeAlienStartY(Phase phase, int wave) {
        int baseDrop = Math.min((wave - 1) * 6, 60);
        int defaultY = GameConfig.ALIEN_START_Y + baseDrop;
        if (phase.has(Mechanic.BOSS)) {
            int belowBoss = GameConfig.BOSS_Y + GameConfig.BOSS_HEIGHT + 16;
            return Math.max(belowBoss, defaultY);
        }
        return defaultY;
    }

    private int randomFireDelay() {
        int min = currentPhase.fireMinTicks();
        int max = currentPhase.fireMaxTicks();
        int delay = min + rng.nextInt(Math.max(1, max - min));
        return Math.max(8, (int) (delay * difficulty.fireDelayMultiplier));
    }

    public void onAlienKilled(Alien a, SoundManager sounds) {
        combo.registerKill();
        int base = a.scoreValue();
        int gained = base * combo.getMultiplier();
        addScore(gained);
        String text = combo.getMultiplier() > 1 ? ("+" + gained + " x" + combo.getMultiplier()) : ("+" + gained);
        Color color = combo.getMultiplier() >= 4 ? new Color(0xFFCC33)
                : combo.getMultiplier() > 1 ? new Color(0xFFFFFF)
                : new Color(0xCCCCCC);
        addPopup((int) a.getX(), (int) a.getY(), text, color);
        spawnExplosion((int) a.getX() + GameConfig.ALIEN_WIDTH / 2,
                (int) a.getY() + GameConfig.ALIEN_HEIGHT / 2, 14);
        sounds.play("alien_died");
        if (rng.nextDouble() < difficulty.powerUpDropChance) {
            powerUps.add(new PowerUp(a.getX() + GameConfig.ALIEN_WIDTH / 2.0 - GameConfig.POWERUP_SIZE / 2.0,
                    a.getY(), PowerUpType.randomDrop(rng)));
        }
    }

    public void onUfoKilled(Ufo u, SoundManager sounds) {
        combo.registerKill();
        int gained = GameConfig.UFO_SCORE * combo.getMultiplier();
        addScore(gained);
        addPopup((int) u.getX(), (int) u.getY(), "+" + gained, new Color(0xFF66AA));
        spawnExplosion((int) u.getX() + GameConfig.UFO_WIDTH / 2,
                (int) u.getY() + GameConfig.UFO_HEIGHT / 2, 20);
        sounds.play("alien_died");
        cameraShake.shake(8, 6);
        ufo = null;
    }

    public void onBossKilled(Boss b, SoundManager sounds) {
        int gained = GameConfig.BOSS_SCORE + wave * 100;
        addScore(gained);
        addPopup((int) b.getX() + GameConfig.BOSS_WIDTH / 2 - 50,
                (int) b.getY() + GameConfig.BOSS_HEIGHT / 2,
                "BOSS DOWN +" + gained, new Color(0xFFCC33));
        for (int i = 0; i < 4; i++) {
            int ox = (int) b.getX() + rng.nextInt(GameConfig.BOSS_WIDTH);
            int oy = (int) b.getY() + rng.nextInt(GameConfig.BOSS_HEIGHT);
            spawnExplosion(ox, oy, 30);
        }
        cameraShake.shake(30, GameConfig.CAMERA_SHAKE_BOSS);
        sounds.play("alien_died");
        boss = null;
    }

    public void onPlayerHit(SoundManager sounds) {
        cameraShake.shake(20, GameConfig.CAMERA_SHAKE_PLAYER);
        spawnExplosion((int) player.getX() + GameConfig.PLAYER_WIDTH / 2,
                (int) player.getY() + GameConfig.PLAYER_HEIGHT / 2, 24);
        combo.breakCombo();
        sounds.play("player_died");
    }

    public void onPowerUpCollected(PowerUp pu) {
        PowerUpType type = pu.getType();
        switch (type) {
            case EXTRA_LIFE -> {
                player.addLife();
                addPopup((int) pu.getX(), (int) pu.getY(), "1UP!", new Color(0xFF6688));
            }
            case SHIELD_REPAIR -> {
                spawnShields(currentPhase.has(Mechanic.FEWER_SHIELDS)
                        ? Math.min(currentPhase.shieldCount(), 2)
                        : currentPhase.shieldCount());
                addPopup((int) pu.getX(), (int) pu.getY(), "SHIELDS!", new Color(0x66FF77));
            }
            default -> {
                active.activate(type);
                addPopup((int) pu.getX(), (int) pu.getY(), type.label, type.color);
            }
        }
    }

    public void onShieldHitBySplittable(double x, double y) {
        Bullet left = new Bullet(x - 6, y, -1.2, GameConfig.ALIEN_BULLET_SPEED * 0.9, Bullet.Side.ALIEN);
        Bullet right = new Bullet(x + 6, y, 1.2, GameConfig.ALIEN_BULLET_SPEED * 0.9, Bullet.Side.ALIEN);
        bullets.add(left);
        bullets.add(right);
    }

    public void addScore(int n) {
        score += n;
        if (score > highScore) highScore = score;
        checkScoreRewards();
    }

    private void checkScoreRewards() {
        while (nextRewardIndex < GameConfig.SCORE_REWARD_THRESHOLDS.length
                && score >= GameConfig.SCORE_REWARD_THRESHOLDS[nextRewardIndex]) {
            applyReward(GameConfig.SCORE_REWARD_THRESHOLDS[nextRewardIndex], nextRewardIndex);
            nextRewardIndex++;
        }
    }

    private void applyReward(int threshold, int index) {
        switch (index) {
            case 0, 2, 3 -> {
                player.addLife();
                addPopup(GameConfig.WIDTH / 2 - 40, GameConfig.HEIGHT / 3,
                        threshold + " - 1UP!", new Color(0xFF6688));
            }
            case 1 -> {
                spawnShields(currentPhase.shieldCount());
                addPopup(GameConfig.WIDTH / 2 - 60, GameConfig.HEIGHT / 3,
                        threshold + " - SHIELDS!", new Color(0x66FF77));
            }
            case 4 -> {
                player.addLife();
                addPopup(GameConfig.WIDTH / 2 - 60, GameConfig.HEIGHT / 3,
                        threshold + " - LEGEND!", new Color(0xFFCC33));
            }
            default -> addPopup(GameConfig.WIDTH / 2 - 30, GameConfig.HEIGHT / 3,
                    "+" + threshold, new Color(0xFFFFFF));
        }
    }

    public void spawnExplosion(int x, int y, int particleCount) {
        explosions.add(new Explosion(x - GameConfig.ALIEN_WIDTH / 2, y - GameConfig.ALIEN_HEIGHT / 2));
        for (int i = 0; i < particleCount; i++) {
            double angle = rng.nextDouble() * Math.PI * 2;
            double speed = 1 + rng.nextDouble() * 3;
            Color c = i % 3 == 0 ? new Color(0xFFCC33)
                    : i % 3 == 1 ? new Color(0xFFFFFF)
                    : new Color(0xFF6633);
            particles.add(new Particle(x, y,
                    Math.cos(angle) * speed,
                    Math.sin(angle) * speed,
                    GameConfig.PARTICLE_LIFE - rng.nextInt(8),
                    c));
        }
    }

    public void addPopup(int x, int y, String text, Color color) {
        popups.add(new ScorePopup(x, y, text, color));
    }

    public List<Alien> getAliens() { return aliens; }
    public List<Bullet> getBullets() { return bullets; }
    public List<Shield> getShields() { return shields; }
    public List<Explosion> getExplosions() { return explosions; }
    public List<Particle> getParticles() { return particles; }
    public List<PowerUp> getPowerUps() { return powerUps; }
    public List<ScorePopup> getPopups() { return popups; }
    public Player getPlayer() { return player; }
    public Ufo getUfo() { return ufo; }
    public Boss getBoss() { return boss; }
    public ActivePowerUps getActivePowerUps() { return active; }
    public Combo getCombo() { return combo; }
    public CameraShake getCameraShake() { return cameraShake; }
    public StarField getStarField() { return starField; }
    public HallOfFame getHallOfFame() { return hallOfFame; }
    public InitialsEntry getInitialsEntry() { return initialsEntry; }
    public Phase getCurrentPhase() { return currentPhase; }
    public Difficulty getDifficulty() { return difficulty; }
    public GameState getState() { return state; }
    public int getScore() { return score; }
    public int getHighScore() { return highScore; }
    public int getWave() { return wave; }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void clearUfo() {
        ufo = null;
    }
}
