package quirkle.game.credits;

import quirkle.engine.Entity;

public class Background extends Entity {

    @Override
    public void onCreate() {
        origin = OriginPresets.CENTER;
        setSprite("credits/grid");
    }
}
