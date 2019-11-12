/*
 * It controls the gameplay, keylistener, and the position of the shot and the ship.
 */
package Sonda;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * @author renan.fernandes
 */
public class GamePlay implements MouseListener, MouseMotionListener, KeyListener, ActionListener {

    private short positionX = 400;
    Sounds sound = new Sounds();

    private short shotX; // it gets inicially the X of the spaceship
    private short shotY = 500; // 490 is the pattern position of the Y ship position.( where it beggin ).
    private boolean shot; // Tells the Jframe to iniciate the shot and starts the function.


    public void setShotX(short shotX) {
        this.shotX = shotX;
    }

    public void setShotY(short shotY) {
        this.shotY = shotY;
    }

    public void setShot(boolean shot) {
        this.shot = shot;
    }

    public boolean isShot() {
        return shot;
    }

    public short getShotX() {
        return shotX;
    }

    public short getShotY() {
        return shotY;
    }

    public short getPositionX() {
        return positionX;
    }

    public void setPositionX(short positionX) {
        this.positionX = positionX;
    }

    public void shotAnimation() {
        if (shot) {
            validatingShot();
            shotY -= 1;
        } else {
            shotY = 500;
        }
    }

    public void validatingShot() {
        if (shotY < 10) {
            shot = false;
            shotY = 500;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {

        if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!shot) {
                shotX = (short) (positionX + 23);
                shot = true;
                sound.shot();
            }

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (positionX <= 790) {
            positionX = (short) e.getX();
        } else {
            positionX = 790;
        }

    }

    @Override
    public void mouseClicked(MouseEvent ke) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!shot) {
            shotX = (short) (positionX + 23);
            shot = true;
            sound.shot();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}
