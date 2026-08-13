package org.quirkle.resourceEngine;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {
    private static final Map<String, BufferedImage> cache = new HashMap<>();
    private static BufferedImage fallbackTexture = null;

    public static BufferedImage getTexture(String path) {
        if (path == null || path.isEmpty()) return getFallbackTexture();
        if (cache.containsKey(path)) return cache.get(path);

        try (InputStream in = AssetManager.class.getResourceAsStream(path)) {
            if (in == null) {
                System.err.println("[Warning] AssetManager: Could not find texture: " + path);
                return getFallbackTexture();
            }
            BufferedImage img = ImageIO.read(in);
            cache.put(path, img);
            return img;
        } catch (Exception e) {
            System.err.println("[Error] AssetManager: Failed to load texture: " + path);
            return getFallbackTexture();
        }
    }

    private static BufferedImage getFallbackTexture() {
        if (fallbackTexture == null) {
            fallbackTexture = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = fallbackTexture.createGraphics();
            g.setColor(Color.MAGENTA);
            g.fillRect(0, 0, 16, 16);
            g.fillRect(16, 16, 16, 16);
            g.setColor(Color.BLACK);
            g.fillRect(16, 0, 16, 16);
            g.fillRect(0, 16, 16, 16);
            g.dispose();
        }
        return fallbackTexture;
    }
}