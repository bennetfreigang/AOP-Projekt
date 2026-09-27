package scenes.startmenu;

import engine.Entity;
import engine.SceneManager;

public class StartMenuShadow extends Entity {

    public void onCreate() {
        setSprite("startmenu/shadow");
        origin = OriginPresets.CENTER;
        x = SceneManager.getCurrentScene().getCenterX();
    }
}
