package com.renan.spaceinvaders.assets;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class AssetManager {

    private final Map<String, BufferedImage> images = new HashMap<>();

    public AssetManager() {
        loadAll();
    }

    private void loadAll() {
        put("background", "pics/back.png");
        put("ship", "pics/ship.png");
        put("alien", "pics/alien.png");
        put("ufo", "pics/25.png");
        put("pow", "pics/pow.gif");
    }

    private void put(String name, String path) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                System.err.println("Missing asset: " + path);
                return;
            }
            BufferedImage img = ImageIO.read(in);
            if (img != null) images.put(name, img);
        } catch (IOException e) {
            System.err.println("Failed loading " + path + ": " + e.getMessage());
        }
    }

    public BufferedImage get(String name) {
        return images.get(name);
    }
}
