package org.quirkle.resourceEngine;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Scene {
    protected final List<Entity> sceneEntities = new CopyOnWriteArrayList<>();

    public int getWidth() { return EngineConfig.WINDOW_WIDTH; }
    public int getHeight() { return EngineConfig.WINDOW_HEIGHT; }
    public int getCenterX() { return (int) (getWidth() / 2.0); }
    public int getCenterY() { return (int) (getHeight() / 2.0); }

    public Scene() {
        onCreate();
    }

    /**
     * easy Background function that sets the {@link #bgTexture} utilizing {@link AssetManager#getTexture()}
     * @deprecated WILL PROBABLY BE REMOVE IN FAVOR OF MANUAL BACKGROUND DRAWING
     *             USING sceneEntities OR THE {@link #onRender(Graphics2D)} HOOK!
     * @param path path to background Texture asset
     */
    public void setBgTexture(String path) {
        this.bgTexture = AssetManager.getTexture(path);
        this.bgTextureEnabled = true;
    }

    /**
     * Called automatically during Scene creation
     * Use this to initialize custom properties
     * @note Executed inside the base constructor layout
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
     * @note Executed immediately after the sprite textures of the sceneEntities get drawn
     */
    public void onRender(Graphics2D g) {}

    /**
     * Handles custom cleanup operations right before scene removal
     */
    public void onDestroy() {}

    /**
     * Adds given {@link Entity}'s to the Scenes {@link #sceneEntities}-CopyOnWriteArrayList in given Order
     * @param entities n >= 1 Entity Objects that should be added to the scene
     * @note Render Event will paint Entities in creation Order (from Oldest [bottom] to youngest [top])
     */
    public void addEntities(Entity... entities) { //really nice solution i found here https://www.geeksforgeeks.org/java/variable-arguments-varargs-in-java/
        for (Entity e : entities) {
            this.sceneEntities.add(e); // Note: Make sure the list name matches (entities vs sceneEntities)
        }
    }
    
    /**
     * Removs given {@link Entity}'s from the Scenes {@link #sceneEntities}-CopyOnWriteArrayList
     * @param entities Entity Objects that should be removed from the scene
     * @note utilizing this will affect paint Order in the render event
     */
    public void removeEntities(Entity... entities) {
        for (Entity e : entities) {
            e.destroy();
            sceneEntities.remove(e);
        }
    }

    public void update(double dt) {
        onTick(dt);
        for (Entity entity : sceneEntities) {
            if (!entity.destroyed) {
                entity.update(dt);
            }
        }
        sceneEntities.removeIf(entity -> entity.destroyed);
    }

    public BufferedImage bgTexture;
    public boolean bgTextureEnabled = false;

    public void render(Graphics2D g) {
        g.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        if (bgTextureEnabled && bgTexture != null) { //maybe unecessary but for now nice to have [would like to use parralax background using sceneEntities]
            g.drawImage(bgTexture, 0, 0, (int) getWidth(), (int)  getHeight(), null);
        }

        onRender(g);
        for (Entity entity : sceneEntities) {
            entity.render(g);
        }
    }

    public void destroy() {
        onDestroy();
        for (Entity entity : sceneEntities) {
            entity.destroy();
        }
        sceneEntities.clear();
    }
}