# Space Invaders

A Java port / overhaul of an old learning project. Runs on **Java 17+** and is
built with Maven.

![space](https://user-images.githubusercontent.com/53544613/62427424-22339b80-b6c9-11e9-9d5e-8d2a2251f2c3.png)

## Build & run

```bash
mvn package
java -jar target/space-invaders.jar
```

`mvn package` produces a self-contained jar via the shade plugin. `mvn test`
runs the JUnit 5 suite.

## Controls

| Key                | Action                |
|--------------------|-----------------------|
| `LEFT` / `RIGHT`   | Move (also `A` / `D`) |
| `SPACE`            | Fire                  |
| `P` / `ESC`        | Pause / resume        |
| `Q` (while paused) | Quit to menu          |
| `M`                | Toggle music / SFX    |
| `ENTER`            | Start / continue      |

## Gameplay

- Five rows of ten aliens descend in a classic fleet. Speed scales as the fleet
  thins out.
- Four destructible shields take erosion damage from any bullet (yours or
  theirs) and from descending aliens.
- A UFO crosses the top of the screen at random intervals; shooting it is worth
  150 points.
- Each cleared wave starts a new, faster wave a few rows lower.
- The game ends when you run out of lives or the aliens reach the player line.
- The high score persists in `~/.spaceinvaders/highscore`.

## Project layout

```
src/main/java/com/renan/spaceinvaders/
  Main.java                — entry point, wires the frame and game loop
  core/                    — GameConfig (constants), GameState, GameLoop
  world/                   — Entity, Player, Alien, Bullet, Shield, Ufo, Explosion, World
  collision/CollisionSystem.java  — AABB checks for bullets / shields / aliens
  input/InputHandler.java  — KeyListener with edge + held-key tracking
  render/                  — GamePanel (paintComponent) + Renderer
  assets/                  — AssetManager (images), SoundManager (javax.sound.sampled.Clip)
  ui/HighScoreStore.java   — persistent high score
src/test/java/...          — JUnit 5 tests (collision, player, shield, world, high score)
```

## Notes on the rewrite

The previous version targeted Java 8 and used `java.applet.AudioClip`, which
was removed from the JDK. The rewrite replaces it with `javax.sound.sampled`,
moves the game loop off the AWT thread into a fixed-timestep loop, overrides
`paintComponent` instead of `paint`, and splits the monolithic `Game` class
into the modules listed above.
