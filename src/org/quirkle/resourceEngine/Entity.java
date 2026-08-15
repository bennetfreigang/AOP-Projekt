package org.quirkle.resourceEngine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/*********************************************************************************************
 * An Entity is a transformable object managed withing a Scene,
 * providing basic lifecycle hooks for
 * initilisation ticking, rendering  and destruction,
 * alongside built in support for positioning, rotation, interpolation and mouse detection
 * 
 * @note must be registered to a {@link Scene} using {@link Scene#add(Entity)}
 *       to activly receive update and render steps
 ********************************************************************************************/
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

    /**
     * The Origin of an entity is the anchor point
     * used as the absolute reference for its position,
     * rotation and scaling in the Scene
     * @see Entity.OriginPresets
     * */
    public OriginPresets origin = OriginPresets.TOP_LEFT;

    public double x = 0;
    public double y = 0;
    public double targetX = 0;
    public double targetY = 0;
    public double rotation = 0;

    /**
     * velocity is used to interpolate consistently across
     * different FPS values using deltaTime
     * @note if set to 0 movement will become instant
     * @see #moveToTarget
     */
    public double velocity = 0;

    public double width = 0;
    public double height = 0;
    public double scaleX = 1.0;
    public double scaleY = 1.0;

    public int renderOrder = 0;

    public String texturePath;
    public BufferedImage texture;
    public boolean visible = true;
    public boolean destroyed = false;

    public Entity() { //should be shifted to Scene initialisation rather than object instation in the future
        try {
            onCreate();
        } catch (Throwable t) {
            System.err.println("[ERROR] resourceEngine / Entity: Throwable Error in onCreate() of [" + getClass().getSimpleName() + "]: " + t.getMessage());
        }
    }

    // Lifecycle hooks

    /**
     * Called automatically during entity instantion
     * Use this to initialize custom entity states or properties
     * @note Executed inside the base constructor layout [might change in the future -> correct instantation after beeing added to scene]
     */
    public void onCreate() {}

    /**
     * Executes core game logic updates once per frame loop
     * @param dt The delta time step value in seconds
     * @note Driven automatically by the central {@link #update} function
     */
    public void onTick(double dt) {}

    /**
     * Performs custom entity layer drawing operations
     * @param g The active Graphics2D rendering context
     * @note Executed immediately after the base sprite texture gets drawn
     */
    public void onRender(Graphics2D g) {}

    /**
     * Handles custom cleanup operations right before entity removal
     * @note Invoked immediately after the {@link #destroyed} flag flips is set to true
     */
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
        //full credit: https://web.archive.org/web/20100430183237/https://ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
        //inverse mouse rotation and then square checkk

        double mouseX = InputManager.getMouseX();
        double mouseY = InputManager.getMouseY();

        double scaledWidth = getScaledWidth();
        double scaledHeight = getScaledHeight();

        // Origin implied adjustion calculations (to swings top left based drawing)
        double drawX = x - origin.x * scaledWidth;
        double drawY = y - origin.y * scaledHeight;

        if (rotation % 360 != 0) {
            double rad = Math.toRadians(rotation);
            double cos = Math.cos(rad);
            double sin = Math.sin(rad);

            // Translate mouse relative to origin (x, y)
            double translatedX = mouseX - x;
            double translatedY = mouseY - y;

            //rotating mouse coords inversly around the origin
            mouseX = translatedX * cos + translatedY * sin + x;
            mouseY = -translatedX * sin + translatedY * cos + y;
        }

        return mouseX >= drawX && mouseX <= drawX + scaledWidth && mouseY >= drawY && mouseY <= drawY + scaledHeight;
    }

    public boolean isClicked() {
        return isHovered() && InputManager.isMouseClicked();
    }

    /**
     * velocity is used to interpolate consistently across
     * different FPS values using delta time
     * move to {@link #targetX} and {@link #targetY} dynamicly calculation movement distance using deltaTime
     * @param dt DeltaTime
     * @see #velocity
     */
    public void moveToTarget(double dt) {
        if (velocity <= 0) {
            x = targetX;
            y = targetY;
            return;
        }

        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        double maxDist = velocity * dt;

        if (dist <= maxDist || dist == 0) {
            x = targetX;
            y = targetY;
        } else {
            x += (dx / dist) * maxDist;
            y += (dy / dist) * maxDist;
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

            g.rotate(Math.toRadians(rotation), x, y);
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