package quirkle.game.settings.scenes;

import quirkle.engine.AssetManager;
import quirkle.engine.Scene;
import quirkle.engine.SceneManager;
import quirkle.game.util.RectangularButton;

public class SettingsScene extends Scene {

    RectangularButton returnButton;


    @Override
    public void onCreate() {
        returnButton = new RectangularButton(AssetManager.getMessage("return from Settings"));

        addEntities(returnButton);

        returnButton.x = getCenterX(); returnButton.y = getCenterY();

        returnButton.minSize = 2.0;
        returnButton.maxSizeModifier = 2.2;
        returnButton.growthSpeed = 0.1;
    }

    @Override
    public void onTick(double dt) {
            if (returnButton.isClicked()) SceneManager.stopTempScene();
        }

}
