/*
 * Responsable to open the pics from the folder pics.
 * Adding the pics to the game...
 */
package Sonda;

import javax.swing.ImageIcon;

public class Sprites {
    private final ImageIcon spaceShip = new ImageIcon(getClass().getClassLoader().getResource("pics/ship.png")); // SpaceShip
    private final ImageIcon alien1 = new ImageIcon(getClass().getClassLoader().getResource("pics/alien.png")); // Alien 1
    private final ImageIcon backGround = new ImageIcon(getClass().getClassLoader().getResource("pics/back.png")); // backGround
    private final ImageIcon cabine = new ImageIcon(getClass().getClassLoader().getResource("pics/cabineT.png")); // Cabin
    private final ImageIcon pow = new ImageIcon(getClass().getClassLoader().getResource("pics/pow.gif")); // Cabin


    //Doom faces ----------
    private final ImageIcon face = new ImageIcon(getClass().getClassLoader().getResource("pics/face.png"));
    private final ImageIcon faceR = new ImageIcon(getClass().getClassLoader().getResource("pics/faceR.png"));
    private final ImageIcon faceL = new ImageIcon(getClass().getClassLoader().getResource("pics/faceL.png"));
    private final ImageIcon faceShoot = new ImageIcon(getClass().getClassLoader().getResource("pics/faceAtack.png"));
    //---------------------

    public ImageIcon getBackGround() {
        return backGround;
    }

    public ImageIcon getSpaceShip() {
        return spaceShip;
    }

    public ImageIcon getAlienSprite() {
        return alien1;
    }

    public ImageIcon getCabine() {
        return cabine;
    }

    public ImageIcon getFace() {
        return face;
    }

    public ImageIcon getFaceR() {
        return faceR;
    }

    public ImageIcon getFaceL() {
        return faceL;
    }

    public ImageIcon getFaceShoot() {
        return faceShoot;
    }

    public ImageIcon getPow() {
        return pow;
    }


}
