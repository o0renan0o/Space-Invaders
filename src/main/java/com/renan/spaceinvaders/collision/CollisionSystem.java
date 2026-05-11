package com.renan.spaceinvaders.collision;

import com.renan.spaceinvaders.assets.SoundManager;
import com.renan.spaceinvaders.core.GameConfig;
import com.renan.spaceinvaders.world.Alien;
import com.renan.spaceinvaders.world.Boss;
import com.renan.spaceinvaders.world.Bullet;
import com.renan.spaceinvaders.world.Player;
import com.renan.spaceinvaders.world.PowerUp;
import com.renan.spaceinvaders.world.PowerUpType;
import com.renan.spaceinvaders.world.Shield;
import com.renan.spaceinvaders.world.Ufo;
import com.renan.spaceinvaders.world.World;

import java.awt.Rectangle;
import java.util.List;

public final class CollisionSystem {

    private CollisionSystem() {
    }

    public static void handle(World world, SoundManager sounds) {
        handleBulletsVsShields(world);
        handleBulletsVsAliens(world, sounds);
        handleBulletsVsBoss(world, sounds);
        handleBulletsVsUfo(world, sounds);
        handleBulletsVsPlayer(world, sounds);
        handlePlayerVsPowerUps(world, sounds);
        handleAliensVsShields(world);
        handleAliensVsPlayer(world, sounds);
    }

    private static void handleBulletsVsShields(World world) {
        boolean reflect = world.getActivePowerUps().isActive(PowerUpType.REFLECT);
        for (Bullet b : world.getBullets()) {
            if (!b.isAlive()) continue;
            Rectangle bb = b.getBounds();
            for (Shield s : world.getShields()) {
                if (s.handleHit(bb, b.getSide())) {
                    if (b.isSplittable()) {
                        world.onShieldHitBySplittable(b.getX(), b.getY());
                    }
                    if (reflect && b.getSide() == Bullet.Side.ALIEN) {
                        world.spawnReflectedBullet(b.getX(), b.getY());
                    }
                    b.kill();
                    break;
                }
            }
        }
        world.getShields().removeIf(s -> !s.isAlive());
    }

    private static void handleBulletsVsAliens(World world, SoundManager sounds) {
        List<Alien> aliens = world.getAliens();
        for (Bullet b : world.getBullets()) {
            if (!b.isAlive() || b.getSide() != Bullet.Side.PLAYER) continue;
            Rectangle bb = b.getBounds();
            for (Alien a : aliens) {
                if (!a.isAlive()) continue;
                if (!a.getBounds().intersects(bb)) continue;
                boolean killed = a.hit(b.getDamage());
                if (killed) {
                    world.onAlienKilled(a, sounds);
                } else {
                    world.addPopup((int) a.getX(), (int) a.getY(), "!", java.awt.Color.WHITE);
                }
                if (!b.isPiercing()) {
                    b.kill();
                    break;
                }
            }
        }
        aliens.removeIf(a -> !a.isAlive());
    }

    private static void handleBulletsVsBoss(World world, SoundManager sounds) {
        Boss boss = world.getBoss();
        if (boss == null || !boss.isAlive()) return;
        Rectangle bb = boss.getBounds();
        for (Bullet b : world.getBullets()) {
            if (!b.isAlive() || b.getSide() != Bullet.Side.PLAYER) continue;
            if (b.getBounds().intersects(bb)) {
                boolean dead = boss.hit(b.getDamage());
                if (dead) {
                    world.onBossKilled(boss, sounds);
                }
                if (!b.isPiercing()) b.kill();
                if (dead) break;
            }
        }
    }

    private static void handleBulletsVsUfo(World world, SoundManager sounds) {
        Ufo ufo = world.getUfo();
        if (ufo == null || !ufo.isAlive()) return;
        Rectangle ub = ufo.getBounds();
        for (Bullet b : world.getBullets()) {
            if (!b.isAlive() || b.getSide() != Bullet.Side.PLAYER) continue;
            if (b.getBounds().intersects(ub)) {
                ufo.kill();
                if (!b.isPiercing()) b.kill();
                world.onUfoKilled(ufo, sounds);
                return;
            }
        }
    }

    private static void handleBulletsVsPlayer(World world, SoundManager sounds) {
        Player player = world.getPlayer();
        if (player.isInvulnerable()) return;
        boolean ghost = world.getActivePowerUps().isActive(PowerUpType.GHOST);
        Rectangle pb = player.getBounds();
        for (Bullet b : world.getBullets()) {
            if (!b.isAlive() || b.getSide() != Bullet.Side.ALIEN) continue;
            if (b.getBounds().intersects(pb)) {
                if (ghost) {
                    if (!b.isNearMissCredited()) {
                        b.creditNearMiss();
                        world.addPopup((int) b.getX(), (int) b.getY(), "GHOST",
                                new java.awt.Color(0xCCFFFF));
                    }
                    continue;
                }
                b.kill();
                if (player.hit()) {
                    world.onPlayerHit(sounds);
                }
                return;
            }
        }
    }

    private static void handlePlayerVsPowerUps(World world, SoundManager sounds) {
        Player player = world.getPlayer();
        Rectangle pb = player.getBounds();
        for (PowerUp pu : world.getPowerUps()) {
            if (!pu.isAlive()) continue;
            if (pu.getBounds().intersects(pb)) {
                pu.collect();
                world.onPowerUpCollected(pu, sounds);
            }
        }
    }

    private static void handleAliensVsShields(World world) {
        for (Alien a : world.getAliens()) {
            Rectangle ab = a.getBounds();
            for (Shield s : world.getShields()) {
                Rectangle sb = s.getBounds();
                if (!ab.intersects(sb)) continue;
                boolean[][] cells = s.getCells();
                for (int r = 0; r < cells.length; r++) {
                    for (int c = 0; c < cells[0].length; c++) {
                        if (!cells[r][c]) continue;
                        int cx = s.getX() + c * GameConfig.SHIELD_CELL;
                        int cy = s.getY() + r * GameConfig.SHIELD_CELL;
                        Rectangle cellRect =
                                new Rectangle(cx, cy, GameConfig.SHIELD_CELL, GameConfig.SHIELD_CELL);
                        if (ab.intersects(cellRect)) cells[r][c] = false;
                    }
                }
            }
        }
        world.getShields().removeIf(s -> !s.isAlive());
    }

    private static void handleAliensVsPlayer(World world, SoundManager sounds) {
        Player player = world.getPlayer();
        if (player.isInvulnerable()) return;
        Rectangle pb = player.getBounds();
        for (Alien a : world.getAliens()) {
            if (!a.isDiving()) continue;
            if (a.getBounds().intersects(pb)) {
                a.hit();
                if (player.hit()) world.onPlayerHit(sounds);
                return;
            }
        }
    }
}
