package quirkle.engine;

import java.awt.Graphics2D;

public class SceneManager {
    //allows basic scene request at Entity initilisation by initialising currentScene as a Dummy Scene
    private static Scene currentScene = new Scene();
    private static Scene storedScene;

    public static void setScene(Scene newScene) {
        if (currentScene != null) {
            currentScene.destroy();
        }
        currentScene = newScene;
    }

    public static void setTempScene(Scene tempScene) {
        if (storedScene == null) {
            storedScene = currentScene;
            currentScene = tempScene;
        }   else    {
            System.err.println("[ERROR] resourceEngine / SceneManager: cant load temporary Scene! There is already a stored Scene");
        }
    }

    public static void stopTempScene() {
        if (storedScene != null) {
            currentScene.destroy();
            currentScene = storedScene;
            storedScene = null;
        }   else    {
            System.err.println("[ERROR] resourceEngine / SceneManager: cant return to stored Scene! There is no stored Scene");
        }
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