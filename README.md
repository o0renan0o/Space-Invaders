# Space Invaders

A Java 17 arcade-style Space Invaders with phase progression, power-ups,
combo scoring, boss fights and a hall of fame. Originally a Java 8 learning
project; fully rewritten with a modular architecture.

## Build & run

```bash
mvn package
java -jar target/space-invaders.jar
```

`mvn test` runs the JUnit 5 suite (48 tests).

## Controls

| Key                | Action                |
|--------------------|-----------------------|
| `LEFT` / `RIGHT`   | Move (also `A` / `D`) |
| `SPACE` tap        | Fire (max 2 bullets onscreen) |
| `SPACE` hold (0.5s)| Charge shot (piercing, 3 dmg) |
| `P` / `ESC`        | Pause / resume        |
| `Q` (while paused) | Quit to menu          |
| `M`                | Toggle music / SFX    |
| `D` (in menu)      | Cycle difficulty      |
| `H` (in menu)      | View Hall of Fame     |
| `ENTER`            | Start / continue      |

In the initials-entry screen, `UP`/`DOWN` change the letter and
`LEFT`/`RIGHT` move the cursor; `ENTER` confirms each letter.

## Phase progression

Ten themed phases with escalating difficulty, then an endless loop where
speed and fire rate keep increasing.

| # | Theme              | Mechanic added                                          | Clear bonus |
|---|--------------------|---------------------------------------------------------|-------------|
| 1 | Patrulha           | warm-up, 4×8 fleet                                      | 100         |
| 2 | Primeira Onda      | standard 5×10                                           | 200         |
| 3 | Rede Cerrada       | narrow formation                                        | 300         |
| 4 | Bombardeiros       | up to 2 alien bullets at once                           | 400         |
| 5 | Mergulhadores      | aliens dive at the player + **mid-boss**                | 500         |
| 6 | Estilhaco          | alien bullets split when they hit a shield              | 600         |
| 7 | Blindados          | front rows take 2 hits (50 pts each)                    | 700         |
| 8 | Tempestade         | 3 simultaneous bullets, only 2 shields                  | 800         |
| 9 | Escolta UFO        | UFO crosses much more frequently                        | 900         |
| 10 | Investida Final   | divers + splitting + armored + **final boss**           | 1000        |
| 11+ | Endless          | +speed / -fire-delay / +clear-bonus each loop           | scaling     |

## Score events

| Threshold | Reward                          |
|-----------|---------------------------------|
| 1,000     | +1 life                         |
| 2,500     | All shields restored            |
| 5,000     | +1 life                         |
| 10,000    | +1 life                         |
| 25,000    | +1 life + "LEGEND" popup        |

Hitting an alien within 1.5 s of the previous kill doubles the combo
multiplier (max ×8). Score earned per kill is multiplied accordingly.

## Aliens & power-ups

- **Squid** (top row, 30 pts), **Crab** (20 pts), **Octopus** (10 pts)
- **Armored** (front rows in phase 7+; 2 hp; 50 pts)
- **Diver** (breaks formation, dives toward player)
- **UFO** crosses occasionally (150 pts)
- **Boss** at phases 5 and 10 with HP bar (1000+ pts)

12% drop chance per killed alien (lower on `HARD`, higher on `EASY`):

| Power-up      | Effect                                    |
|---------------|-------------------------------------------|
| `1UP`         | +1 life (instant)                         |
| `RAPID`       | Faster fire cooldown for 10 s             |
| `DOUBLE`      | Two parallel bullets for 10 s             |
| `PIERCE`      | Bullets pass through aliens for 10 s      |
| `SLOW`        | Aliens move at 50% for 10 s               |
| `SHIELD`      | Restores all shields (instant)            |

## Difficulty

| Mode    | Lives | Speed × | Drop chance | Fire delay × |
|---------|-------|---------|-------------|--------------|
| Easy    | 4     | 0.85    | 18 %        | 1.30         |
| Normal  | 3     | 1.00    | 12 %        | 1.00         |
| Hard    | 2     | 1.20    | 8 %         | 0.75         |

## Hall of Fame

Top 10 scores are persisted to `~/.spaceinvaders/hof` with 3-letter
arcade-style initials.

## Status bar (Doom STBAR)

A 70 px strip across the bottom of the screen unifies every HUD element
into five regions, matching the layout of the original Doom STBAR:

```
┌────────┬──────────┬────────┬──────────┬─────────────┐
│ SCORE  │ WAVE  N  │        │ COMBO    │ POWER-UPS   │
│ 000123 │ NAME     │ COCKPIT│ x4 ━━━━  │ ▤ ▤ ▤ ▤     │
│ HI     │ LIVES x3 │  FACE  │          │ ▒▒░ ▓░ ▓▒░  │
│ 005000 │ ▲ ▲ ▲    │        │ DIFFICULTY│ RAPID DOUBLE│
└────────┴──────────┴────────┴──────────┴─────────────┘
```

The playfield shrinks to 530 px tall (HEIGHT - STBAR_HEIGHT). Player,
shields and the alien game-over line all move up to stay above the
bar. Boss HP bar stays floating at the top of the screen.

## Doom face HUD

A nostalgic Doom-style STBAR face sits inside the ship cockpit at the top-right.
The expression changes with what's happening:

| Face          | Trigger                                    |
|---------------|--------------------------------------------|
| Forward       | Default                                    |
| Look left     | Holding `LEFT` / `A`                       |
| Look right    | Holding `RIGHT` / `D`                      |
| Attack        | ~0.13 s after firing                       |
| Hurt          | ~0.5 s after taking damage                 |
| Evil grin     | ~0.5 s after killing a UFO or boss         |
| Win           | While `WAVE_CLEARED` is showing            |
| Dead          | While `GAME_OVER` or out of lives          |

A red overlay tints the face proportional to remaining lives — by the last
life it visibly bleeds through, just like the original Doomguy.

## Mechanics depth

- **Two-bullet onscreen limit** forces precision — no SPACE-mashing.
- **Charge shot**: holding SPACE for ~0.5 s suspends auto-fire and starts
  charging. Releasing fires a wide piercing bullet that does 3 damage
  (one-shots armoured aliens).
- **Predictive alien fire**: aliens read the player's horizontal velocity
  and lead the shot (factor 0.6, capped at ±2 px/tick of horizontal
  bullet velocity). Standing still no longer keeps you safe.
- **Boss has three escalating phases** based on HP:
  - >66%: classic 3-bullet spread (green HP bar)
  - 33–66%: spread + 3-column rain (yellow HP bar)
  - <33%: spread + rain + aimed shot, ~40% faster fire cadence (red bar)
- **Wave bonuses**: clearing a wave without losing a life doubles the
  clear bonus (`PERFECT!`); clearing inside 30 s adds `+500` (`FAST!`).
- **Near-miss reward**: dodge an alien bullet within 12 px of your
  hitbox and you bank +25 points + 30 ticks of combo window, popup
  `CLOSE!`. Dancing through bullets pays.

## Visual / juice features

- Three-layer parallax star field
- 16-frame explosion sprite sheet (from Phaser examples) with particle bursts
- Camera shake on hits, boss damage, and UFO kills
- Floating score popups with combo color tier
- Active power-up HUD with countdown bars
- HP bar over the boss
- Sprite assets from Clear Code and Phaser-examples (red / yellow / green
  aliens, ship, UFO, baddies, explosion, starfield, spin objects)

## Project layout

```
src/main/java/com/renan/spaceinvaders/
  Main.java
  core/      GameConfig, GameState, GameLoop, Difficulty, Mechanic, Phase, Phases
  world/     Entity, Player, Alien, AlienType, Bullet, Shield, Ufo, Boss,
             PowerUp, PowerUpType, ActivePowerUps, Particle, Explosion,
             ScorePopup, Combo, CameraShake, StarField, World
  collision/ CollisionSystem
  input/     InputHandler
  render/    GamePanel, Renderer, SpriteFactory, PixelArt
  assets/    AssetManager, SoundManager (javax.sound.sampled.Clip)
  ui/        HallOfFame, InitialsEntry
src/test/java/com/renan/spaceinvaders/
  core/PhasesTest
  collision/CollisionSystemTest
  ui/HallOfFameTest
  world/PlayerTest, ShieldTest, WorldTest, WorldSmokeTest,
        ComboTest, ActivePowerUpsTest
```

## Notes

- Audio now uses `javax.sound.sampled.Clip` so it runs on modern JDKs
  (the original used `java.applet.AudioClip`, removed in Java 17).
- The game loop is a fixed-timestep loop on a dedicated daemon thread;
  rendering goes through `paintComponent` with nearest-neighbour
  interpolation to preserve the pixel-art look.
- Sprite assets are public/free pieces from
  [clear-code-projects/Space-invaders](https://github.com/clear-code-projects/Space-invaders)
  and [photonstorm/phaser-examples](https://github.com/photonstorm/phaser-examples).
