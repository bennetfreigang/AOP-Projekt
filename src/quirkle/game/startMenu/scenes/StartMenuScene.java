package quirkle.game.startmenu.scenes;

import quirkle.engine.*;

import quirkle.game.startmenu.entities.*;
import quirkle.game.util.PrlxEntity;

public class StartMenuScene extends Scene {

    StartMenuButton playButton;
    StartMenuButton settingsButton;
    StartMenuButton creditsButton;
    StartMenuButton quitButton;

    PrlxEntity prlxBackground;

    @Override
    public void onCreate() {

        PrlxEntity.PrlxLayer prlxLayer0 = new PrlxEntity.PrlxLayer("startmenu/background/0", 0.15);
        PrlxEntity.PrlxLayer prlxLayer1 = new PrlxEntity.PrlxLayer("startmenu/background/1", 0.1);
        PrlxEntity.PrlxLayer prlxLayer2 = new PrlxEntity.PrlxLayer("startmenu/background/2", 0.05);

        prlxBackground = new PrlxEntity(prlxLayer0, prlxLayer1, prlxLayer2);

        addEntities(prlxBackground, new Title("Qwirkle"));

        playButton = new StartMenuButton(AssetManager.getMessage("play"));
        settingsButton = new StartMenuButton(AssetManager.getMessage("settings"));
        creditsButton = new StartMenuButton(AssetManager.getMessage("credits"));
        quitButton = new StartMenuButton(AssetManager.getMessage("quit"));

        addEntities(playButton, settingsButton, creditsButton, quitButton);
        playButton.x = getCenterX(); playButton.y = getCenterY();
        settingsButton.x = getCenterX(); settingsButton.y = getCenterY()+100;
        creditsButton.x = getCenterX(); creditsButton.y = getCenterY()+200;
        quitButton.x = getCenterX(); quitButton.y = getCenterY()+300;
    }

    @Override
    public void onTick(double dt) {
        prlxBackground.tarX = InputManager.getMouseX();
        prlxBackground.tarY = InputManager.getMouseY();

        if (playButton.isClicked()) SceneManager.setTempScene(new Scene(), true, true); //dummy
        if (settingsButton.isClicked()) SceneManager.setTempScene(new Scene(), true, true); //dumy
        if (creditsButton.isClicked()) SceneManager.setTempScene(new Scene(), true, true); //dumy
        if (quitButton.isClicked()) System.exit(0);
    }

}
