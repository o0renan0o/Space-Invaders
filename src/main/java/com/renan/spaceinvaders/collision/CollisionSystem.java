package com.renan.spaceinvaders.collision;

import com.renan.spaceinvaders.assets.SoundManager;
import com.renan.spaceinvaders.core.GameConfig;
import com.renan.spaceinvaders.world.Alien;
import com.renan.spaceinvaders.world.Bullet;
import com.renan.spaceinvaders.world.Player;
import com.renan.spaceinvaders.world.Shield;
import com.renan.spaceinvaders.world.Ufo;
import com.renan.spaceinvaders.world.World;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;

public final class CollisionSystem {

    private CollisionSystem() {
    }

    public static void handle(World world, SoundManager sounds) {
        handleBulletsVsShields(world);
        handleBulletsVsAliens(world, sounds);
        handleBulletsVsUfo(world, sounds);
        handleBulletsVsPlayer(world, sounds);
        handleAliensVsShields(world);
    }

    private static void handleBulletsVsShields(World world) {
        Iterator<Bullet> it = world.getBullets().iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            if (!b.isAlive()) continue;
            Rectangle bb = b.getBounds();
            for (Shield s : world.getShields()) {
                if (s.handleHit(bb, b.getSide())) {
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
                if (a.getBounds().intersects(bb)) {
                    a.kill();
                    b.kill();
                    world.addScore(a.scoreValue());
                    world.spawnExplosion((int) a.getX(), (int) a.getY());
                    sounds.play("alien_died");
                    break;
                }
            }
        }
        aliens.removeIf(a -> !a.isAlive());
    }

    private static void handleBulletsVsUfo(World world, SoundManager sounds) {
        Ufo ufo = world.getUfo();
        if (ufo == null || !ufo.isAlive()) return;
        Rectangle ub = ufo.getBounds();
        for (Bullet b : world.getBullets()) {
            if (!b.isAlive() || b.getSide() != Bullet.Side.PLAYER) continue;
            if (b.getBounds().intersects(ub)) {
                ufo.kill();
                b.kill();
                world.addScore(GameConfig.UFO_SCORE);
                world.spawnExplosion((int) ufo.getX(), (int) ufo.getY());
                sounds.play("alien_died");
                world.clearUfo();
                return;
            }
        }
    }

    private static void handleBulletsVsPlayer(World world, SoundManager sounds) {
        Player player = world.getPlayer();
        if (player.isInvulnerable()) return;
        Rectangle pb = player.getBounds();
        for (Bullet b : world.getBullets()) {
            if (!b.isAlive() || b.getSide() != Bullet.Side.ALIEN) continue;
            if (b.getBounds().intersects(pb)) {
                b.kill();
                if (player.hit()) {
                    world.spawnExplosion((int) player.getX(), (int) player.getY());
                    sounds.play("player_died");
                }
                return;
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
}
