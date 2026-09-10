package quirkle.game.quickmenu.scenes;

import quirkle.PersistentData;
import quirkle.engine.*;

import quirkle.game.credits.scenes.CreditsScene;
import quirkle.game.settings.scenes.SettingsScene;
import quirkle.game.startmenu.scenes.StartMenuScene;
import quirkle.game.util.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.security.Key;

public class QuickMenuScene extends Scene {
    private RectangularButton buttonContinue;
    private RectangularButton buttonSettings;
    private RectangularButton buttonCredits;
    private RectangularButton buttonQuit;

    private static int buttonSpacing = 20;
    private static float optionsTitleFontSize = 80;

    @Override
    public void onCreate() {
        SimpleSpriteEntity background = new SimpleSpriteEntity("quickmenu/background", Entity.OriginPresets.TOP_LEFT);
        SimpleTextEntity optionsTitle = new SimpleTextEntity(AssetManager.getMessage("options"), optionsTitleFontSize, "higher_jump", Color.WHITE, Entity.OriginPresets.BOTTOM_LEFT);

        buttonContinue = new RectangularButton("Continue");
        buttonSettings = new RectangularButton("Settings");
        buttonCredits = new RectangularButton("Credits");
        buttonQuit = new RectangularButton("Quit");

        addEntities(background, optionsTitle, buttonContinue, buttonSettings, buttonCredits, buttonQuit);

        optionsTitle.x = getCenterX()/8;
        optionsTitle.y = getCenterY()*0.8;

        int i = 0;
        for (Entity e : new Entity[] {buttonContinue, buttonSettings, buttonCredits, buttonQuit}) {
            e.y = getCenterY() + i*buttonContinue.getScaledHeight() + i*buttonSpacing;
            e.x = getCenterX()/3;
            i++;
        }

        if (PersistentData.debugMode) addEntities(new DebugIndicator());
    }

    @Override
    public void onTick(double dt) {
        if (buttonContinue.isClicked()) SceneManager.stopTempScene();
        if (buttonSettings.isClicked()) SceneManager.setScene(new SettingsScene());
        if (buttonCredits.isClicked()) SceneManager.setTempScene(new CreditsScene(), false, true);
        if (buttonQuit.isClicked()) SceneManager.setScene(new StartMenuScene());

        if (InputManager.isKeyPressed(KeyEvent.VK_ESCAPE)) SceneManager.stopTempScene();
    }
}
