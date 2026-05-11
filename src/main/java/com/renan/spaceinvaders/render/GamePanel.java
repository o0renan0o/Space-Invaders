package com.renan.spaceinvaders.render;

import com.renan.spaceinvaders.assets.AssetManager;
import com.renan.spaceinvaders.core.GameConfig;
import com.renan.spaceinvaders.world.World;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public final class GamePanel extends JPanel {

    private final Renderer renderer;

    public GamePanel(World world, AssetManager assets) {
        this.renderer = new Renderer(world, assets);
        setPreferredSize(new Dimension(GameConfig.WIDTH, GameConfig.HEIGHT));
        setDoubleBuffered(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            renderer.render(g2);
        } finally {
            g2.dispose();
        }
    }
}
