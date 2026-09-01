package quirkle.game.startmenu.scenes;

import quirkle.engine.AssetManager;
import quirkle.engine.Scene;
import quirkle.engine.SceneManager;
import quirkle.game.settings.scenes.SettingsScene;
import quirkle.game.credits.scenes.CreditsScene;
import quirkle.game.startmenu.entities.BackgroundHelper;
import quirkle.game.startmenu.entities.StartMenuButton;
import quirkle.game.startmenu.entities.Title;

public class StartMenuScene extends Scene {

    StartMenuButton playButton;
    StartMenuButton settingsButton;
    StartMenuButton creditsButton;
    StartMenuButton quitButton;


    @Override
    public void onCreate() {
        addEntities(new BackgroundHelper(0.1), new Title("Qwirkle"));

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
        if (playButton.isClicked()) SceneManager.setTempScene(new TwoScene(), true, true);
        SceneManager.storedSceneAlpha = 0.2f;
        if (settingsButton.isClicked()) SceneManager.setTempScene(new SettingsScene(), true, true);
        SceneManager.storedSceneAlpha = 0.2f;
        if (creditsButton.isClicked()) SceneManager.setTempScene(new CreditsScene(), true, true);
        SceneManager.storedSceneAlpha = 0.2f;
        if (quitButton.isClicked()) System.exit(0);
    }

}
