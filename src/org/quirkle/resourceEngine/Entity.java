package org.quirkle.resourceEngine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class Entity {

    public enum OriginPresets {
        TOP_LEFT(0.0, 0.0),
        TOP_MID(0.5, 0.0),
        TOP_RIGHT(1.0, 0.0),
        CENTER(0.5, 0.5),
        BOTTOM_LEFT(0.0, 1.0),
        BOTTOM_MID(0.5, 1.0),
        BOTTOM_RIGHT(1.0, 1.0);

        public final double x;
        public final double y;

        OriginPresets(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public OriginPresets origin = OriginPresets.TOP_LEFT;

    public double x = 0;
    public double y = 0;
    public double targetX = 0;
    public double targetY = 0;
    public double speed = 0; // 0 = instant movement

    public double width = 0;
    public double height = 0;
    public double scaleX = 1.0;
    public double scaleY = 1.0;

    public int renderOrder = 0;

    public String texturePath;
    public BufferedImage texture;
    public boolean visible = true;
    public boolean destroyed = false;

    public Entity() {
        try {
            onCreate();
        } catch (Throwable t) {
            System.err.println("[ERROR] resourceEngine / Entity: Throwable Error in onCreate() of [" + getClass().getSimpleName() + "]: " + t.getMessage());
        }
    }

    // Lifecycle hooks
    public void onCreate() {}
    public void onTick(double dt) {}
    public void onRender(Graphics2D g) {}
    public void onDestroy() {}

    public void setTexture(String path) {
        this.texturePath = path;
        this.texture = AssetManager.getTexture(path);
        if (this.texture != null) {
            this.width = this.texture.getWidth();
            this.height = this.texture.getHeight();
        }
    }

    public double getScaledWidth() {
        return width * scaleX;
    }

    public double getScaledHeight() {
        return height * scaleY;
    }

    public boolean isHovered() {
        double mx = InputManager.getMouseX();
        double my = InputManager.getMouseY();

        double scaledWidth = getScaledWidth();
        double scaledHeight = getScaledHeight();

        //origin implied adjustion calculations
        double drawX = x - origin.x * scaledWidth;
        double drawY = y - origin.y * scaledHeight;

        return mx >= drawX && mx <= drawX + scaledWidth &&
               my >= drawY && my <= drawY + scaledHeight;
    }

    public boolean isClicked() {
        return isHovered() && InputManager.isMouseClicked();
    }

    public void moveTowardsTarget(double dt) {
        if (speed <= 0) {
            x = targetX;
            y = targetY;
            return;
        }

        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        double maxDist = speed * dt;

        if (distance <= maxDist || distance == 0) {
            x = targetX;
            y = targetY;
        } else {
            x += (dx / distance) * maxDist;
            y += (dy / distance) * maxDist;
        }
    }

    public void update(double dt) {
        try {
            onTick(dt);
        } catch (Throwable t) {
            System.err.println("[ERROR] resourceEngine / Entity: Throwable Error in onTick() of [" + getClass().getSimpleName() + "]: " + t.getMessage());
        }

        if (destroyed) return;
    }

    public void render(Graphics2D g) {
        if (!visible || destroyed) return;
        if (texture != null) {
            double scaledWidth = getScaledWidth();
            double scaledHeight = getScaledHeight();

            //origin implied conversion calculations
            int drawX = (int) (x - origin.x * scaledWidth);
            int drawY = (int) (y - origin.y * scaledHeight);

            g.drawImage(texture, drawX, drawY, (int) scaledWidth, (int) scaledHeight, null);
        }
        onRender(g);
    }

    public void destroy() {
        if (!destroyed) {
            destroyed = true;
            onDestroy();
        }
    }
}