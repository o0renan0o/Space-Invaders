/**
 * Game space invaders.
 *
 * @author Renan Fernandes
 * @version Beta
 * @since Beta
 */
/*
 * Javadoc
 * The constrocture of the Game class build and starts the game. Starting the
 * thread by calling this.start() and calling the graphics using graphicsGame().
 * The game runs by the pace of the chronometer started with this.start()
 * inicialized by the main.
 *
 * Link ? https://o0renan0o.github.io/Space-Invaders/
 */
package Sonda;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class Game extends JPanel implements Runnable {

    /**
     * Variables window gets the JFrame and starts the aplication using the
     * JPanel to do so. s call the game images. lasTimeMili and mili are used by
     * the chronometer to tic. ship calls the controls and ship positions to
     * implement in the Jframe.
     */
    JFrame window = new JFrame();
    private long lastTimeMili = System.nanoTime();
    private byte miliEnemy;
    private byte miliShot;
    private byte miliShotAlien;
    private boolean alienDied = true;
    Sprites s = new Sprites();
    GamePlay ship = new GamePlay();
    List<Aliens> enemies = new ArrayList();
    List<Map> mapShot = new ArrayList<>();
    Sounds sound = new Sounds();
    private short shipPosition = ship.getPositionX();

    //It prints the faces..............
    private ImageIcon faceIcon = s.getFace();
    private short faceTime;
    private boolean faceBoo;
    private boolean faceRBoo;
    private boolean faceLBoo;
    //.................................

    //make pow after alien die.........
    private short powX;
    private short powY;
    private short powCount;
    private boolean powValidation;
    //.................................

    // Transparent 16 x 16 pixel cursor image.
    BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

    Game() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        sound.playMusic();
        create();
        graphicsGame();
        this.start(); // starts the game
        //new Music().playMusic();
        //new Sound().musicStart();
        //new SimpleAudioPlayer().players(); // <<-- Read !!! The Thread does not allow the sequence to proceed after this.start

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        Game game = new Game(); //call to inicialize the game
    }

    // Create a new blank cursor.
    Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
            cursorImg, new Point(0, 0), "blank cursor");

// Set the blank cursor to the JFrame.

    public void graphicsGame() { // Opens the game window.
        window.setBounds(0, 0, 851, 627); // window size.
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false); // Do not allow the window to be resized.
        window.add(Game.this); // opens the JPanel
        window.addKeyListener(ship); // add the keyboard action.
        window.addMouseMotionListener(ship); // add the mouse movement.
        window.addMouseListener(ship); // add the mouse click.
        window.setLocationRelativeTo(null); // centralize the window on the screen.
        window.getContentPane().setCursor(blankCursor);// paint null cursor
    }

    @Override
    public void paint(Graphics g) { // Prints the game on the panel.

        window.setBackground(Color.BLACK);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 850, 625);
        s.getBackGround().paintIcon(this, g, 0, 0);
        s.getCabine().paintIcon(this, g, 0, 0);
        //It print the faces !...............................
        if (ship.getPositionX() < shipPosition) {
            s.getFaceL().paintIcon(this, g, 40, 65);
            faceIcon = s.getFaceL();
        } else if (ship.getPositionX() > shipPosition) {
            s.getFaceR().paintIcon(this, g, 40, 65);
            faceIcon = s.getFaceR();
        } else if (ship.isShot()) {
            s.getFaceShoot().paintIcon(this, g, 40, 65);
            faceIcon = s.getFaceShoot();
        } else {
            faceIcon.paintIcon(this, g, 40, 65);
        }
        //...................................................

        if (ship.isShot()) { // it prints if spacebar is pressed
            g.setColor(Color.white);
            g.fillRect(ship.getShotX(), ship.getShotY(), 4, 12);
        }


        // Aliens print.......
        for (Aliens enemies : enemies) {
            if (enemies.isAlive()) {
                enemies.getAlienSprite().paintIcon(this, g, enemies.getAlienX(), enemies.getAlienY());
                if (enemies.isShotBoo()) {
                    g.setColor(Color.white);
                    g.fillRect(enemies.getShotX(), enemies.getShotY(), 4, 12);
                }
            }
            if (powValidation) {// This prints the pow after die
                powCount++;
                if (powCount < 3000) {
                    s.getPow().paintIcon(this, g, powX, powY);
                }
            }

        }

        s.getSpaceShip().paintIcon(this, g, ship.getPositionX(), 500);
        shipPosition = ship.getPositionX();
    }


    @Override
    public void run() {

    }

    private void start() {
        while (true) {
            if (System.nanoTime() >= lastTimeMili + 100000) { // Chronometer.
                miliEnemy++;
                miliShot++;
                faceTime++;
                miliShotAlien++;
                lastTimeMili = System.nanoTime();
                if (miliShot == 25) {
                    if (ship.isShot()) { // calls the shotHit() till the shot gets out or hit something.
                        ship.shotAnimation();
                    }
                    miliShot = 0;
                }
                if (miliShotAlien == 50) {// Shot faster
                    for (Aliens alien : enemies) {
                        if (alien.isShotBoo()) {
                            alien.shotAnimation();
                        }
                    }
                    miliShotAlien = 0;
                }
                if (miliEnemy == 100) { // Every 200 nano, call methods.
                    repaint();
                    for (Aliens alien : enemies) {
                        alien.scriptMoviment();
                        alien.shot();
                    }

                    hit();// Verify if the shot hits the enemy
                    miliEnemy = 0;
                }
            }
        }
    }

    // If it hits -------------------------
    public void hit() {
        //System.out.println(" X = Shot:" + ship.getShotX() + " Alien:" + alien.getAlienX() + "      Y = Shot:" + ship.getShotY() + " Alien:" + alien.getAlienY());
        for (Aliens alien : enemies) {

            for (byte i = 0; i <= 40; i++) {
                for (byte l = 0; l <= 4; l++)
                    if (alien.isAlive() && (ship.getShotX() == alien.getAlienX() + i && ship.getShotY() + l == alien.getAlienY() + 30)) {// 30 to bring Y under the alien... that way the shot hits the bottom
                        alien.setAlive(false);
                        ship.setShot(false);
                        ship.setShotX(ship.getPositionX());
                        ship.setShotY((short) 500);
                        sound.alienDied();
                        //Make pow after die....
                        powX = (short) (alien.getAlienX() - 25);//-25 just to put the pow more precise on the screen
                        powY = alien.getAlienY();
                        powValidation = true;
                        powCount = 0;
                        //......................
                    }
            }
        }
    }

    private void create() {
        for (int i = 1; i < 20; i++) {
            enemies.add(new Aliens(i));
        }
    }
}
