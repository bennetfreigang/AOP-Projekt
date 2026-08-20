package quirkle.resourceEngine;

import java.awt.Graphics2D;

public class SceneManager {
    //allows basic scene request at Entity initilisation by initialising currentScene as a Dummy Scene
    private static Scene currentScene = new Scene();

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