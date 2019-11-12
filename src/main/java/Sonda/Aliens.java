/*
 * This creates the enemies.
 */
package Sonda;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author renan.fernandes
 */
public class Aliens extends Sprites {
    private int count;
    private boolean alive = true;
    private short alienX;
    private short alienY;
    private boolean alienVec;
    private boolean shotBoo;
    private short shot;
    private short shotX;
    private short shotY;
    private short nextNumb;
    Random random = new Random();
    private Map mapShoot;
    private int[] direction = {1, -1};
    Sounds som = new Sounds();

    Aliens(int number) {
        count = number;
        randomPosition();
        nextNumb = (short) random.nextInt(500);// Set random number to set shot time
    }

    public short getShotX() {
        return shotX;
    }

    public short getShotY() {
        return shotY;
    }

    public boolean isShotBoo() {
        return shotBoo;
    }

    public void setShotBoo(boolean shootBoo) {
        this.shotBoo = shootBoo;
    }

    public void randomPosition() {
        alienX = (short) random.nextInt(790);
        alienY = (short) random.nextInt(300);

    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public short getAlienX() {
        return alienX;
    }

    public short getAlienY() {
        return alienY;
    }


    @Override
    public ImageIcon getAlienSprite() {
        return super.getAlienSprite();
    }

    public void shot() {
        shot++;
        if (alive) {
            if (shot == nextNumb) {
                if (!shotBoo) {
                    shotBoo = true;
                    shotY = alienY;
                    shotX = alienX;
                    som.alienShot();
                    nextNumb = (short) random.nextInt(500);
                }
                shot = 0;
            }
        }

    }

    public void shotAnimation() {
        if (shotBoo) {
            shotY++;
        }
        if (shotY > 600) {
            shotBoo = false;
        }
    }

    public void scriptMoviment() {

        if (alienVec) {
            alienX += 1;
            if (alienX > 790) {
                alienVec = false;
                alienY += 40;
            }
        } else {
            alienX -= 1;
            if (alienX < 10) {
                alienVec = true;
                alienY += 40;
            }
        }
    }

    public String toString() {
        return "Alien number: " + String.valueOf(count) + "  Position: X=" + alienX + "   Y=" + alienY;
    }
}
