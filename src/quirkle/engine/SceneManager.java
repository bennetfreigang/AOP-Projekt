package quirkle.engine;

import java.awt.AlphaComposite;
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

    private static boolean renderStoredScene;
    private static boolean pauseStoredScene;

    public static float currentSceneAlpha = 1.0f;
    public static float storedSceneAlpha = 1.0f;

    public static void setTempScene(Scene tempScene, boolean _renderStoredScene, boolean _pauseStoredScene) {
        if (storedScene == null) {
            renderStoredScene = _renderStoredScene;
            pauseStoredScene = _pauseStoredScene;

            storedScene = currentScene;
            currentScene = tempScene;
        }   else    {
            if (!EngineConfig.SUPPRES_WARNINGS) System.err.println("[ERROR] resourceEngine / SceneManager: cant load temporary Scene! There is already a stored Scene");
        }
    }

    public static void stopTempScene() {
        if (storedScene != null) {
            currentScene.destroy();
            currentScene = storedScene;
            storedScene = null;
        }   else    {
            if (!EngineConfig.SUPPRES_WARNINGS) System.err.println("[ERROR] resourceEngine / SceneManager: cant return to stored Scene! There is no stored Scene");
        }
    }

    public static Scene getCurrentScene() {
        return currentScene;
    }

    public static void update(double dt) {
        if (currentScene != null) currentScene.update(dt);
        if (!pauseStoredScene && storedScene != null) storedScene.update(dt);

        InputManager.endFrame();
    }

    public static void render(Graphics2D g) {
        if (currentScene != null) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, currentSceneAlpha));
            currentScene.render(g);
        }

        if (renderStoredScene && storedScene != null) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, storedSceneAlpha));
            storedScene.render(g);
        }
    }
}