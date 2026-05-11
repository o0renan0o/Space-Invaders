package com.renan.spaceinvaders;

import com.renan.spaceinvaders.assets.AssetManager;
import com.renan.spaceinvaders.assets.SoundManager;
import com.renan.spaceinvaders.core.GameConfig;
import com.renan.spaceinvaders.core.GameLoop;
import com.renan.spaceinvaders.input.InputHandler;
import com.renan.spaceinvaders.render.GamePanel;
import com.renan.spaceinvaders.ui.HighScoreStore;
import com.renan.spaceinvaders.world.World;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public final class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::launch);
    }

    private static void launch() {
        AssetManager assets = new AssetManager();
        SoundManager sounds = new SoundManager();
        HighScoreStore highScores = new HighScoreStore();

        World world = new World(highScores.load());
        InputHandler input = new InputHandler();
        GamePanel panel = new GamePanel(world, assets);

        panel.addKeyListener(input);
        panel.setFocusable(true);

        JFrame frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);

        BufferedImage blank = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit()
                .createCustomCursor(blank, new Point(0, 0), "blank");
        frame.getContentPane().setCursor(blankCursor);

        frame.setVisible(true);
        panel.requestFocusInWindow();

        sounds.playMusic("music");

        GameLoop loop = new GameLoop(world, input, panel, sounds, highScores, GameConfig.TICK_RATE);
        loop.start();
    }

    private Main() {
    }
}
