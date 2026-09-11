package quirkle.game.tutorial.scenes;

import quirkle.engine.Entity;
import quirkle.engine.Scene;
import quirkle.game.tutorial.entities.TutorialFocusEntity;
import quirkle.game.util.SimpleSpriteEntity;

public class TutorialScene extends Scene {
    @Override
    public void onCreate() {
        SimpleSpriteEntity background = new SimpleSpriteEntity("tutorial/viewport", Entity.OriginPresets.TOP_LEFT);
        addEntities(background, new TutorialFocusEntity());
    }
}
