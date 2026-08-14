package org.quirkle.resourceEngine;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {
    private static final Map<String, BufferedImage> textureCache = new HashMap<>();
    private static BufferedImage fallbackTexture = null;

    //temporary solution (dynamic texture load request)
    // -> should be swapped out later by a precaching approach [wouldnt change anything for frontend / only performance]
    public static BufferedImage getTexture(String path) {
        if (path == null || path.isEmpty()) return getFallbackTexture();
        if (textureCache.containsKey(path)) return textureCache.get(path);

        try (InputStream in = AssetManager.class.getResourceAsStream(path)) {
            if (in == null) {
                System.err.println("[WARNING] resourceEngine / AssetManager: Could not find texture: " + path);
                return getFallbackTexture();
            }
            BufferedImage img = ImageIO.read(in);
            textureCache.put(path, img);
            return img;
        } catch (Exception e) {
            System.err.println("[ERROR] resourceEngine / AssetManager: Failed to load texture: " + path);
            return getFallbackTexture();
        }
    }

    //future lang loading implementation
    //public static String getMessage(String identifier) { ... } ! also utilizing precaching

    private static BufferedImage getFallbackTexture() {
        if (fallbackTexture == null) {
            int size = 128;
            int halfSize = size / 2;
            
            fallbackTexture = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = fallbackTexture.createGraphics();
            g.setColor(Color.MAGENTA);
            g.fillRect(0, 0, halfSize, halfSize);
            g.fillRect(halfSize, halfSize, halfSize, halfSize);
            g.setColor(Color.BLACK);
            g.fillRect(halfSize, 0, halfSize, halfSize);
            g.fillRect(0, halfSize, halfSize, halfSize);
            g.dispose();
        }
        return fallbackTexture;
    }
}