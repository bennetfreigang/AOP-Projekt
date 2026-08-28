package quirkle.game.startMenu.entities;

import quirkle.engine.Entity;
import quirkle.engine.InputManager;
import quirkle.engine.Scene;
import quirkle.engine.SceneManager;

import java.awt.*;

public class BackgroundHelper extends Entity {

    private double parralaxControl;
    private int mouseX;
    private int mouseY;

    public BackgroundHelper(double parralaxControl) {
        this.parralaxControl = parralaxControl;
    }

    @Override
    public void onCreate() {
        origin = OriginPresets.CENTER;
    }

    @Override
    public void onTick(double dt) {
        mouseX = InputManager.getMouseX();
        mouseY = InputManager.getMouseY();
    }

    @Override
    public void onRender(Graphics2D g) {
        Scene currentScene = SceneManager.getCurrentScene();
        if (currentScene == null) return;

        int centerX = currentScene.getCenterX();
        int centerY = currentScene.getCenterY();

        for (int i = 0; i < 3; i++) {
            double depthFactor = (i + 1) / 5.0;

            int drawX = (int) (centerX + (centerX - mouseX) * parralaxControl * depthFactor);
            int drawY = (int) (centerY + (centerY - mouseY) * parralaxControl * depthFactor);

            drawSprite("startmenu/backdrop/" + i,
                    1.0 + i, drawX, drawY, 0.0, OriginPresets.CENTER, g);
        }
    }
}

