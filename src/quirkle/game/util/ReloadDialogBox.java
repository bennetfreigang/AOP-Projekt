package quirkle.game.util;

import quirkle.engine.Entity;
import quirkle.engine.Scene;
import quirkle.engine.SceneManager;
import quirkle.game.startmenu.scenes.StartMenuScene;

import java.awt.*;

public class ReloadDialogBox extends Scene {
    public static String fontIdentifier = "poly_regular";

    public String message;
    public RectangularButton continueButton;

    public ReloadDialogBox(String message) {
        this.message = message;
    }

    @Override
    public void onCreate() {
        SimpleSpriteEntity background = new SimpleSpriteEntity("util/reloaddialogbox/background", Entity.OriginPresets.CENTER);
        SimpleTextEntity dialog = new SimpleTextEntity("test\\hate\\and hate tests", 40, "poly_regular", Color.WHITE, Entity.OriginPresets.CENTER);

        continueButton = new RectangularButton("proceed");

        addEntities(background, dialog, continueButton);

        for (Entity e : sceneEntities) {
            e.x = SceneManager.getCurrentScene().getCenterX();
            e.y = SceneManager.getCurrentScene().getCenterY();
        }

        continueButton.y += background.getScaledHeight()/2;
    }

    @Override
    public void onTick(double dt) {
        if (continueButton.isClicked()) SceneManager.setScene(new StartMenuScene());
    }
}