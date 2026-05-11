package com.renan.spaceinvaders.collision;

import com.renan.spaceinvaders.assets.SoundManager;
import com.renan.spaceinvaders.core.GameConfig;
import com.renan.spaceinvaders.world.Alien;
import com.renan.spaceinvaders.world.Bullet;
import com.renan.spaceinvaders.world.Player;
import com.renan.spaceinvaders.world.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollisionSystemTest {

    private World world;
    private SoundManager sounds;

    @BeforeEach
    void setup() {
        world = new World(0);
        world.startNewGame();
        sounds = new SoundManager();
    }

    @Test
    void playerBulletKillsAlienAndScores() {
        Alien alien = world.getAliens().get(0);
        int initialScore = world.getScore();
        Bullet b = new Bullet(alien.getX() + 5, alien.getY() + 5,
                -1, Bullet.Side.PLAYER);
        world.getBullets().add(b);

        CollisionSystem.handle(world, sounds);

        assertFalse(alien.isAlive());
        assertFalse(b.isAlive());
        assertTrue(world.getScore() > initialScore);
    }

    @Test
    void playerBulletDoesNotHurtPlayer() {
        Player p = world.getPlayer();
        int lives = p.getLives();
        Bullet b = new Bullet(p.getX() + 5, p.getY() + 5,
                -1, Bullet.Side.PLAYER);
        world.getBullets().add(b);

        CollisionSystem.handle(world, sounds);

        assertEquals(lives, p.getLives());
    }

    @Test
    void alienBulletDamagesPlayerOnceInvulnExpires() {
        Player p = world.getPlayer();
        for (int i = 0; i < GameConfig.RESPAWN_INVULN_TICKS; i++) p.tick();
        assertFalse(p.isInvulnerable());
        int lives = p.getLives();

        Bullet b = new Bullet(p.getX() + 5, p.getY() + 5,
                1, Bullet.Side.ALIEN);
        world.getBullets().add(b);
        CollisionSystem.handle(world, sounds);

        assertEquals(lives - 1, p.getLives());
        assertFalse(b.isAlive());
    }

    @Test
    void alienBulletIgnoredWhilePlayerInvulnerable() {
        Player p = world.getPlayer();
        assertTrue(p.isInvulnerable());
        int lives = p.getLives();
        Bullet b = new Bullet(p.getX() + 5, p.getY() + 5,
                1, Bullet.Side.ALIEN);
        world.getBullets().add(b);

        CollisionSystem.handle(world, sounds);

        assertEquals(lives, p.getLives());
    }

    @Test
    void shieldStopsBullet() {
        if (world.getShields().isEmpty()) return;
        var shield = world.getShields().get(0);
        Bullet b = new Bullet(
                shield.getX() + shield.widthPx() / 2.0,
                shield.getY() + 4,
                -1, Bullet.Side.PLAYER);
        world.getBullets().add(b);

        CollisionSystem.handle(world, sounds);

        assertFalse(b.isAlive());
    }
}
