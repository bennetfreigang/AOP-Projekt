package quirkle.game.credits;

import quirkle.engine.AssetManager;
import quirkle.engine.Entity;
import quirkle.engine.Scene;

public class CreditsScene extends Scene {

    private final int TOTAL_CREDITS = 5;
    private final double SPAWN_INTERVAL = 2.0;

    private int nextIndex = 0;
    private double timeSinceLastSpawn = SPAWN_INTERVAL;

    public CreditsScene() {
        Entity background = new Background();
        addEntities(background);

        background.x = getCenterX();
        background.y = getCenterY();
    }

    @Override
    public void onTick(double dt) {
        timeSinceLastSpawn += dt;

        // spawn every 4.0 seconds (SPAWN_INTERVALL) a new CreditsElement
        if (timeSinceLastSpawn >= SPAWN_INTERVAL && nextIndex < TOTAL_CREDITS) {
            addEntities(new CreditsElement(nextIndex));
            nextIndex++;
            timeSinceLastSpawn = 0;
        }
    }
}
