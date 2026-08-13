package org.quirkle.resourceEngine;

import java.awt.Graphics2D;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Scene {
    protected final List<Entity> entities = new CopyOnWriteArrayList<>();

    public Scene() {
        onCreate();
    }

    public void onCreate() {}
    public void onTick(double dt) {}
    public void onRender(Graphics2D g) {}
    public void onDestroy() {}

    private int entityCounter = 0;

    public void add(Entity entity) {
        entity.renderOrder = entityCounter++;
        entities.add(entity);
    }
    
    public void remove(Entity entity) {
        entity.destroy();
        entities.remove(entity);
    }

    public void update(double dt) {
        onTick(dt);
        for (Entity entity : entities) {
            if (entity.destroyed) {
                entities.remove(entity);
            } else {
                entity.update(dt);
            }
        }
    }

    public void render(Graphics2D g) {
        onRender(g);
        
        //rendering from oldest to newest
        entities.stream().sorted(Comparator.comparingInt(e -> e.renderOrder)).forEach(entity -> entity.render(g));
    }

    public void destroy() {
        onDestroy();
        for (Entity entity : entities) {
            entity.destroy();
        }
        entities.clear();
    }

    public double getWidth() { return EngineConfig.WINDOW_WIDTH; }
    public double getHeight() { return EngineConfig.WINDOW_HEIGHT; }
    public double getCenterX() { return getWidth() / 2.0; }
    public double getCenterY() { return getHeight() / 2.0; }
}