package com.renan.spaceinvaders.collision;

import com.renan.spaceinvaders.assets.SoundManager;
import com.renan.spaceinvaders.core.GameConfig;
import com.renan.spaceinvaders.world.Alien;
import com.renan.spaceinvaders.world.AlienType;
import com.renan.spaceinvaders.world.Bullet;
import com.renan.spaceinvaders.world.Player;
import com.renan.spaceinvaders.world.PowerUp;
import com.renan.spaceinvaders.world.PowerUpType;
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
    void armoredAlienTakesTwoHits() {
        Alien armored = new Alien(0, 0, 100, 100, AlienType.ARMORED);
        world.getAliens().clear();
        world.getAliens().add(armored);

        Bullet b1 = new Bullet(armored.getX() + 5, armored.getY() + 5,
                -1, Bullet.Side.PLAYER);
        world.getBullets().add(b1);
        CollisionSystem.handle(world, sounds);
        assertTrue(armored.isAlive(), "armored survives first hit");
        assertFalse(b1.isAlive());
        assertEquals(1, armored.getHp());

        Bullet b2 = new Bullet(armored.getX() + 5, armored.getY() + 5,
                -1, Bullet.Side.PLAYER);
        world.getBullets().add(b2);
        CollisionSystem.handle(world, sounds);
        assertFalse(armored.isAlive(), "armored dies on second hit");
    }

    @Test
    void piercingBulletGoesThroughMultiple() {
        world.getAliens().clear();
        Alien a1 = new Alien(0, 0, 100, 200, AlienType.SQUID);
        Alien a2 = new Alien(0, 1, 110, 200, AlienType.SQUID);
        world.getAliens().add(a1);
        world.getAliens().add(a2);
        Bullet b = new Bullet(115, 205, -1, Bullet.Side.PLAYER).piercing();
        world.getBullets().add(b);

        CollisionSystem.handle(world, sounds);

        assertFalse(a1.isAlive());
        assertFalse(a2.isAlive(), "piercing bullet kills both overlapping aliens");
        assertTrue(b.isAlive(), "piercing bullet stays alive after hits");
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

    @Test
    void powerUpCollectedByPlayer() {
        Player p = world.getPlayer();
        PowerUp pu = new PowerUp(p.getX() + 5, p.getY() + 5, PowerUpType.RAPID_FIRE);
        world.getPowerUps().add(pu);

        CollisionSystem.handle(world, sounds);

        assertFalse(pu.isAlive());
        assertTrue(world.getActivePowerUps().isActive(PowerUpType.RAPID_FIRE));
    }

    @Test
    void extraLifePowerUpAddsLife() {
        Player p = world.getPlayer();
        int lives = p.getLives();
        PowerUp pu = new PowerUp(p.getX() + 5, p.getY() + 5, PowerUpType.EXTRA_LIFE);
        world.getPowerUps().add(pu);

        CollisionSystem.handle(world, sounds);

        assertEquals(Math.min(lives + 1, GameConfig.PLAYER_MAX_LIVES), p.getLives());
    }

    @Test
    void reflectShieldHitDoesNotConcurrentModify() {
        world.getActivePowerUps().activate(PowerUpType.REFLECT);
        if (world.getShields().isEmpty()) {
            return;
        }
        var shield = world.getShields().get(0);
        double bx = shield.getX() + 4;
        double by = shield.getY();
        for (int i = 0; i < 4; i++) {
            world.getBullets().add(new Bullet(bx + i * 6, by, 0, Bullet.Side.ALIEN));
        }
        CollisionSystem.handle(world, sounds);
        long playerBullets = world.getBullets().stream()
                .filter(b -> b.getSide() == Bullet.Side.PLAYER)
                .count();
        assertTrue(playerBullets >= 1, "at least one reflected player bullet expected");
    }
}
