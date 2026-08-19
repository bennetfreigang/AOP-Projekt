package org.quirkle.resourceEngine;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

//temporary dynamic loading solutions will be swapped out later for a full precaching solution
public class AssetManager {

    private static final Map<String, BufferedImage> textureCache = new HashMap<>();
    private static BufferedImage fallbackTexture = null;

    public static BufferedImage getTexture(String identifier) {
        identifier = "/assets/textures/" + identifier + ".png"; //direct translation in prep for future precaching (frontend-shift)

        if (identifier == null || identifier.isEmpty()) return getFallbackTexture();
        if (textureCache.containsKey(identifier)) return textureCache.get(identifier);

        try (InputStream in = AssetManager.class.getResourceAsStream(identifier)) {
            if (in == null) {
                System.err.println("[WARNING] resourceEngine / AssetManager: Could not find texture: " + identifier);
                return getFallbackTexture();
            }
            BufferedImage img = ImageIO.read(in);
            textureCache.put(identifier, img);
            return img;
        } catch (Exception e) {
            System.err.println("[ERROR] resourceEngine / AssetManager: Failed to load texture: " + identifier);
            return getFallbackTexture();
        }
    }

    private static final Map<String, Font> fontCache = new HashMap<>();
    private static final Font fallbackFont = new Font("Arial", Font.PLAIN, 24);

    public static Font getFont(String identifier) {
        identifier = "/assets/fonts/" + identifier + ".ttf"; //direct translation in prep for future precaching (frontend-shift)

        if (identifier == null || identifier.isEmpty()) return fallbackFont;
        if (fontCache.containsKey(identifier)) return fontCache.get(identifier);

        try (InputStream in = AssetManager.class.getResourceAsStream(identifier)) {
            if (in == null) {
                System.err.println("[WARNING] resourceEngine / AssetManager: Could not find font: " + identifier);
                return fallbackFont;
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, in);
            fontCache.put(identifier, font);
            return font;
        } catch (Exception e) {
            System.err.println("[ERROR] resourceEngine / AssetManager: Failed to load font: " + identifier);
            return fallbackFont;
        }
    }

    /**
     * future lang loading implementation
     * DUMMY function (for now)
     */
    public static String getMessage(String identifier) {
        System.err.println("[INFO] resourceEngine / AssetManager: Would return message: " + identifier + " but this isnt implemented yet");
        return new String("LANGMESSAGELOADING ISNT IMPLEMENTED YET");
    } //! also utilizing precaching

    private static final Map<String, byte[]> soundCache = new HashMap<>();
    private static void fallbackSound(String identifier) {
        System.err.println("[ERROR] resourceEngine / AssetManager: Failed to play sound: " + identifier);
    }

    private static void playRawBytes(byte[] soundData, double volume) {
        if (volume <= 0.0) return;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(soundData);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bais);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            //transform dB
            float dBvol = (float) (20 * Math.log10(volume * EngineConfig.VOLUME_MAIN)); //dB conversion
            dBvol = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dBvol));
            gainControl.setValue(dBvol);

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) clip.close();
            });

            clip.start();

        }   catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            fallbackSound("not identifiable");
        }
    }

    public static void playSound(String identifier, double volume) {
        if (identifier == null || identifier.isEmpty()) { fallbackSound(identifier); return; }

        identifier = "/assets/sounds/" + identifier + ".wav";

        if (soundCache.containsKey(identifier)) { playRawBytes(soundCache.get(identifier), volume); return; }

        try (InputStream in = AssetManager.class.getResourceAsStream(identifier)) {
            if (in == null) { fallbackSound(identifier); return; }
            byte[] soundData = in.readAllBytes();
            soundCache.put(identifier, soundData);
            playRawBytes(soundData, volume);
        } catch (Exception e) {
            fallbackSound(identifier);
        }
    }

    //Fallback textures only loads if AssetManager needs it
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