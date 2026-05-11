package com.renan.spaceinvaders.world;

import com.renan.spaceinvaders.core.GameConfig;
import com.renan.spaceinvaders.core.Mechanic;
import com.renan.spaceinvaders.core.Phase;
import com.renan.spaceinvaders.core.Phases;
import org.junit.jupiter.api.Test;

import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoundsTest {

    @Test
    void playerBoundsMatchConfig() {
        Player p = new Player(3);
        Rectangle r = p.getBounds();
        assertEquals(GameConfig.PLAYER_WIDTH, r.width);
        assertEquals(GameConfig.PLAYER_HEIGHT, r.height);
    }

    @Test
    void playerBoundsStayInsideScreen() {
        Player p = new Player(3);
        for (int i = 0; i < 5000; i++) p.moveLeft();
        Rectangle r = p.getBounds();
        assertTrue(r.x >= 0, "left clamp");
        for (int i = 0; i < 10000; i++) p.moveRight();
        r = p.getBounds();
        assertTrue(r.x + r.width <= GameConfig.WIDTH, "right clamp");
    }

    @Test
    void alienBoundsMatchConfig() {
        Alien a = new Alien(0, 0, 50, 50, AlienType.SQUID);
        Rectangle r = a.getBounds();
        assertEquals(GameConfig.ALIEN_WIDTH, r.width);
        assertEquals(GameConfig.ALIEN_HEIGHT, r.height);
    }

    @Test
    void bulletBoundsMatchConfig() {
        Bullet b = new Bullet(100, 200, -5, Bullet.Side.PLAYER);
        Rectangle r = b.getBounds();
        assertEquals(GameConfig.BULLET_WIDTH, r.width);
        assertEquals(GameConfig.BULLET_HEIGHT, r.height);
    }

    @Test
    void ufoBoundsMatchConfig() {
        Ufo u = new Ufo(true);
        Rectangle r = u.getBounds();
        assertEquals(GameConfig.UFO_WIDTH, r.width);
        assertEquals(GameConfig.UFO_HEIGHT, r.height);
    }

    @Test
    void bossBoundsMatchConfig() {
        Boss b = new Boss(1);
        Rectangle r = b.getBounds();
        assertEquals(GameConfig.BOSS_WIDTH, r.width);
        assertEquals(GameConfig.BOSS_HEIGHT, r.height);
    }

    @Test
    void powerUpBoundsMatchConfig() {
        PowerUp pu = new PowerUp(100, 100, PowerUpType.RAPID_FIRE);
        Rectangle r = pu.getBounds();
        assertEquals(GameConfig.POWERUP_SIZE, r.width);
        assertEquals(GameConfig.POWERUP_SIZE, r.height);
    }

    @Test
    void shieldBoundsCoverFullPixelGrid() {
        Shield s = new Shield(100, 200);
        Rectangle r = s.getBounds();
        assertEquals(GameConfig.SHIELD_COLS * GameConfig.SHIELD_CELL, r.width);
        assertEquals(GameConfig.SHIELD_ROWS * GameConfig.SHIELD_CELL, r.height);
    }

    @Test
    void bossDoesNotOverlapInitialAlienFleet() {
        int bossBottom = GameConfig.BOSS_Y + GameConfig.BOSS_HEIGHT;
        for (int wave = 1; wave <= 10; wave++) {
            Phase p = Phases.forWave(wave);
            if (!p.has(Mechanic.BOSS)) continue;
            int startY = World.computeAlienStartY(p, wave);
            assertTrue(startY > bossBottom,
                    "Phase " + wave + " alien startY=" + startY
                            + " must be below boss bottom=" + bossBottom);
        }
    }

    @Test
    void nonBossPhasesUseDefaultAlienStartY() {
        Phase p1 = Phases.LIST.get(0);
        int startY = World.computeAlienStartY(p1, 1);
        assertEquals(GameConfig.ALIEN_START_Y, startY);
    }

    @Test
    void alienFleetBottomStaysAboveShieldTopOnSpawn() {
        for (int wave = 1; wave <= 10; wave++) {
            Phase p = Phases.forWave(wave);
            int startY = World.computeAlienStartY(p, wave);
            int rows = p.rows();
            int bottom = startY + rows * GameConfig.ALIEN_HEIGHT
                    + (rows - 1) * GameConfig.ALIEN_V_SPACING;
            assertTrue(bottom < GameConfig.SHIELD_Y,
                    "Wave " + wave + " fleet bottom=" + bottom
                            + " must be above shield y=" + GameConfig.SHIELD_Y);
        }
    }

    @Test
    void alienFleetStaysAboveShields() {
        Phase phase = Phases.LIST.get(0);
        int rows = phase.rows();
        int initialBottom = GameConfig.ALIEN_START_Y
                + rows * GameConfig.ALIEN_HEIGHT
                + (rows - 1) * GameConfig.ALIEN_V_SPACING;
        assertTrue(initialBottom < GameConfig.SHIELD_Y,
                "initial alien bottom " + initialBottom
                        + " must be above shield top " + GameConfig.SHIELD_Y);
    }

    @Test
    void alienGameOverYIsAboveAndCloseToPlayer() {
        assertTrue(GameConfig.ALIEN_GAME_OVER_Y < GameConfig.PLAYER_Y);
        assertTrue(GameConfig.ALIEN_GAME_OVER_Y > GameConfig.PLAYER_Y - 20);
    }

    @Test
    void shieldsSitBetweenAliensAndPlayer() {
        int shieldBottom = GameConfig.SHIELD_Y
                + GameConfig.SHIELD_ROWS * GameConfig.SHIELD_CELL;
        assertTrue(shieldBottom < GameConfig.PLAYER_Y,
                "shield bottom " + shieldBottom
                        + " must be above player y " + GameConfig.PLAYER_Y);
    }

    @Test
    void bulletDoesNotTunnelThroughEntities() {
        assertTrue(GameConfig.PLAYER_BULLET_SPEED < GameConfig.PLAYER_HEIGHT,
                "player bullet must not skip over player-sized entities");
        assertTrue(GameConfig.PLAYER_BULLET_SPEED < GameConfig.ALIEN_HEIGHT,
                "player bullet must not skip aliens");
        assertTrue(GameConfig.ALIEN_BULLET_SPEED < GameConfig.SHIELD_ROWS * GameConfig.SHIELD_CELL,
                "alien bullet must not skip shields");
    }

    @Test
    void playerBoundsIntersectIncomingAlienBullet() {
        Player p = new Player(3);
        Bullet b = new Bullet(p.getX() + 10, p.getY() + 5,
                GameConfig.ALIEN_BULLET_SPEED, Bullet.Side.ALIEN);
        assertTrue(p.getBounds().intersects(b.getBounds()));
    }

    @Test
    void alienBoundsIntersectIncomingPlayerBullet() {
        Alien a = new Alien(0, 0, 100, 100, AlienType.CRAB);
        Bullet b = new Bullet(a.getX() + 18, a.getY() + 8,
                -GameConfig.PLAYER_BULLET_SPEED, Bullet.Side.PLAYER);
        assertTrue(a.getBounds().intersects(b.getBounds()));
    }

    @Test
    void firedBulletStartsInsidePlayerXRange() {
        Player p = new Player(3);
        ActivePowerUps active = new ActivePowerUps();
        Bullet b = p.fire(active).get(0);
        Rectangle bounds = b.getBounds();
        assertTrue(bounds.x >= p.getBounds().x);
        assertTrue(bounds.x + bounds.width <= p.getBounds().x + p.getBounds().width);
    }

    @Test
    void doubleShotBulletsStayInsidePlayer() {
        Player p = new Player(3);
        ActivePowerUps active = new ActivePowerUps();
        active.activate(PowerUpType.DOUBLE_SHOT);
        for (Bullet b : p.fire(active)) {
            Rectangle bb = b.getBounds();
            assertTrue(bb.x >= p.getBounds().x, "double-shot bullet leaks left");
            assertTrue(bb.x + bb.width <= p.getBounds().x + p.getBounds().width,
                    "double-shot bullet leaks right");
        }
    }

    @Test
    void fleetDoesNotInfiniteDropAfterCrossingEdge() {
        World world = new World(0);
        world.startNewGame();
        Alien rightmost = world.getAliens().get(0);
        for (Alien a : world.getAliens()) {
            if (a.getX() > rightmost.getX()) rightmost = a;
        }
        double pushNeeded = (GameConfig.WIDTH - 2)
                - (rightmost.getX() + GameConfig.ALIEN_WIDTH);
        for (Alien a : world.getAliens()) a.move(pushNeeded, 0);

        double initialMinY = Double.POSITIVE_INFINITY;
        for (Alien a : world.getAliens()) {
            initialMinY = Math.min(initialMinY, a.getY());
        }

        com.renan.spaceinvaders.input.InputHandler input =
                new com.renan.spaceinvaders.input.InputHandler();
        com.renan.spaceinvaders.assets.SoundManager s =
                new com.renan.spaceinvaders.assets.SoundManager();
        for (int i = 0; i < 10; i++) world.update(input, s);

        double finalMinY = Double.POSITIVE_INFINITY;
        for (Alien a : world.getAliens()) {
            finalMinY = Math.min(finalMinY, a.getY());
        }
        int drops = (int) Math.round((finalMinY - initialMinY) / GameConfig.ALIEN_DROP);
        assertTrue(drops <= 1,
                "fleet should drop at most once across 10 ticks at the edge, got " + drops);
    }

    @Test
    void divingAlienDoesNotTriggerGameOverByCrossingPlayerLine() {
        World world = new World(0);
        world.startNewGame();
        Alien a = world.getAliens().get(0);
        a.startDive(0, 4);
        for (int i = 0; i < 200; i++) a.diveStep();
        com.renan.spaceinvaders.input.InputHandler input =
                new com.renan.spaceinvaders.input.InputHandler();
        com.renan.spaceinvaders.assets.SoundManager s =
                new com.renan.spaceinvaders.assets.SoundManager();
        com.renan.spaceinvaders.core.GameState before = world.getState();
        world.update(input, s);
        assertEquals(before, world.getState(),
                "diver crossing the player line must not end the game");
    }

    @Test
    void powerUpInstantTypesAreInstant() {
        assertTrue(PowerUpType.EXTRA_LIFE.instant);
        assertTrue(PowerUpType.SHIELD_REPAIR.instant);
        assertFalse(PowerUpType.RAPID_FIRE.instant);
        assertFalse(PowerUpType.DOUBLE_SHOT.instant);
        assertFalse(PowerUpType.PIERCING.instant);
        assertFalse(PowerUpType.SLOW_MO.instant);
    }

}
