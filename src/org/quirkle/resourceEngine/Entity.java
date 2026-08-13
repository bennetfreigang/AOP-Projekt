package org.quirkle.resourceEngine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class Entity {
    public double x = 0;
    public double y = 0;
    public double targetX = 0;
    public double targetY = 0;
    public double speed = 0; // 0 = instant movement

    public double width = 0;
    public double height = 0;
    public double scaleX = 1.0;
    public double scaleY = 1.0;

    public String texturePath;
    public BufferedImage texture;
    public boolean visible = true;
    public boolean destroyed = false;

    public Entity() {
        onCreate();
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

    public void centerAt(double cx, double cy) {
        this.x = cx - (getScaledWidth() / 2.0);
        this.y = cy - (getScaledHeight() / 2.0);
        this.targetX = this.x;
        this.targetY = this.y;
    }

    public boolean isHovered() {
        double mx = InputManager.getMouseX();
        double my = InputManager.getMouseY();
        return mx >= x && mx <= x + getScaledWidth() &&
               my >= y && my <= y + getScaledHeight();
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
        if (destroyed) return;
        onTick(dt);
    }

    public void render(Graphics2D g) {
        if (!visible || destroyed) return;
        if (texture != null) {
            g.drawImage(texture, (int) x, (int) y, (int) getScaledWidth(), (int) getScaledHeight(), null);
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