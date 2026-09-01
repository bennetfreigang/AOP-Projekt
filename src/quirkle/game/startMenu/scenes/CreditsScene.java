package quirkle.game.startMenu.scenes;

import quirkle.engine.AssetManager;
import quirkle.engine.Scene;
import quirkle.engine.SceneManager;
import quirkle.game.startMenu.entities.StartMenuButton;

public class CreditsScene extends Scene {

    StartMenuButton returnButton;


    @Override
    public void onCreate() {
        returnButton = new StartMenuButton(AssetManager.getMessage("return from Credits"));

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
