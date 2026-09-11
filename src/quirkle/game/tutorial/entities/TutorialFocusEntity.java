package quirkle.game.tutorial.entities;

import quirkle.engine.AssetManager;
import quirkle.engine.Entity;
import quirkle.engine.InputManager;

public class TutorialFocusEntity extends Entity {
    @Override
    public void onCreate() {
        setSprite("tutorial/tutorial_Focus");
        origin = OriginPresets.CENTER;
    }

    @Override
    public void onTick(double dt) {
        x = InputManager.getMouseX();
        y = InputManager.getMouseY();
    }
}
