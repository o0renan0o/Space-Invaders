package com.renan.spaceinvaders.assets;

import com.renan.spaceinvaders.core.GameConfig;
import com.renan.spaceinvaders.render.SpriteFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AssetManager {

    private final Map<String, BufferedImage> images = new HashMap<>();
    private final List<BufferedImage> explosionFrames = new ArrayList<>();

    public AssetManager() {
        loadAll();
    }

    private void loadAll() {
        put("ship", "pics/player.png");
        put("ufo", "pics/extra.png");
        put("alien_squid", "pics/red.png");
        put("alien_crab", "pics/yellow.png");
        put("alien_octopus", "pics/green.png");
        put("alien_diver", "pics/shmup-baddie.png");
        put("alien_armored", "pics/shmup-baddie3.png");
        put("starfield_bg", "pics/phaser_starfield.png");

        sliceExplosionSheet("pics/phaser_explode.png");

        for (Map.Entry<String, BufferedImage> e : SpriteFactory.buildAll().entrySet()) {
            images.putIfAbsent(e.getKey(), e.getValue());
        }
    }

    private void put(String name, String path) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(path)) {
            if (in == null) return;
            BufferedImage img = ImageIO.read(in);
            if (img != null) images.put(name, img);
        } catch (IOException e) {
            System.err.println("Failed loading " + path + ": " + e.getMessage());
        }
    }

    private void sliceExplosionSheet(String path) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(path)) {
            if (in == null) return;
            BufferedImage sheet = ImageIO.read(in);
            if (sheet == null) return;
            int frames = GameConfig.EXPLOSION_FRAMES;
            int frameW = sheet.getWidth() / frames;
            int frameH = sheet.getHeight();
            for (int i = 0; i < frames; i++) {
                explosionFrames.add(sheet.getSubimage(i * frameW, 0, frameW, frameH));
            }
        } catch (IOException e) {
            System.err.println("Failed loading explosion sheet: " + e.getMessage());
        }
    }

    public BufferedImage get(String name) {
        return images.get(name);
    }

    public BufferedImage explosionFrame(int index) {
        if (explosionFrames.isEmpty()) return null;
        return explosionFrames.get(Math.max(0, Math.min(explosionFrames.size() - 1, index)));
    }

    public boolean hasExplosionFrames() {
        return !explosionFrames.isEmpty();
    }
}
