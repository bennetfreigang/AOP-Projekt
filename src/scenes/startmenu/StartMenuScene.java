package scenes.startmenu;

import engine.InputManager;
import engine.Scene;
import engine.SceneManager;
import engine.assets.AssetManager;
import scenes.credits.CreditsScene;
import scenes.playerselect.PlayerSelectScene;
import scenes.settings.SettingsScene;
import ui.PrlxEntity;
import ui.RectangularButton;


public class StartMenuScene extends Scene {

    RectangularButton playButton;
    RectangularButton settingsButton;
    RectangularButton creditsButton;
    RectangularButton quitButton;

    PrlxEntity prlxBackground;

    @Override
    public void onCreate() {
        PrlxEntity.PrlxLayer prlxLayer0 = new PrlxEntity.PrlxLayer("startmenu/background/0", 0.07);
        PrlxEntity.PrlxLayer prlxLayer1 = new PrlxEntity.PrlxLayer("startmenu/background/1", 0.05);
        PrlxEntity.PrlxLayer prlxLayer2 = new PrlxEntity.PrlxLayer("startmenu/background/2", 0.03);
        PrlxEntity.PrlxLayer prlxLayerVignette = new PrlxEntity.PrlxLayer("startmenu/vignette", 0.01);

        prlxBackground = new PrlxEntity(prlxLayer0, prlxLayer1, prlxLayer2, prlxLayerVignette);

        StartMenuShadow startMenuShadow = new StartMenuShadow();

        addEntities(prlxBackground, startMenuShadow, new Title("Qwirkle"));

        playButton = new RectangularButton(AssetManager.getMessage("play"));
        settingsButton = new RectangularButton(AssetManager.getMessage("settings"));
        creditsButton = new RectangularButton(AssetManager.getMessage("credits"));
        quitButton = new RectangularButton(AssetManager.getMessage("quit"));

        addEntities(playButton, settingsButton, creditsButton, quitButton);
        playButton.y = getCenterY();
        settingsButton.y = getCenterY()+100;
        creditsButton.y = getCenterY()+200;
        quitButton.y = getCenterY()+300;

        startMenuShadow.y = getCenterY() + 150;
    }

    @Override
    public void onTick(double dt) {
        prlxBackground.tarX = InputManager.getMouseX();
        prlxBackground.tarY = InputManager.getMouseY();

        if (playButton.isClicked()) SceneManager.setScene(new PlayerSelectScene());
        if (settingsButton.isClicked()) SceneManager.setTempScene(new SettingsScene(), false, true);
        if (creditsButton.isClicked()) SceneManager.setTempScene(new CreditsScene(), false, true);
        if (quitButton.isClicked()) System.exit(0);
    }
}
