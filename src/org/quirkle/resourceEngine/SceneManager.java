package org.quirkle.resourceEngine;

import java.awt.Graphics2D;

public class SceneManager {
    private static Scene currentScene;

    public static void setScene(Scene newScene) {
        if (currentScene != null) {
            currentScene.destroy();
        }
        currentScene = newScene;
    }

    public static Scene getCurrentScene() {
        return currentScene;
    }

    public static void update(double dt) {
        if (currentScene != null) {
            currentScene.update(dt);
        }
        InputManager.endFrame();
    }

    public static void render(Graphics2D g) {
        if (currentScene != null) {
            currentScene.render(g);
        }
    }
}