package quirkle.game.startMenu.scenes;

import quirkle.engine.*;

import quirkle.game.startMenu.entities.*;
import quirkle.game.startMenu.scenes.*;

public class TwoScene extends Scene {

    StartMenuButton returnButton;


    @Override
    public void onCreate() {
        returnButton = new StartMenuButton(AssetManager.getMessage("return"));

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
