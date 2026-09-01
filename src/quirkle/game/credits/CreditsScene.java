package quirkle.game.credits;

import quirkle.engine.Scene;

import java.awt.*;

public class CreditsScene extends Scene {

    private final int TOTAL_CREDITS = 5;
    private final double SPAWN_INTERVAL = 2.0;

    private int nextIndex = 0;
    private double timeSinceLastSpawn = SPAWN_INTERVAL;

    private final Background background;

    public CreditsScene() {
        background = new Background();
    }

    @Override
    public void onTick(double dt) {
        background.onTick(dt);
        timeSinceLastSpawn += dt;

        // spawn every 4.0 seconds (SPAWN_INTERVALL) a new CreditsElement
        if (timeSinceLastSpawn >= SPAWN_INTERVAL && nextIndex < TOTAL_CREDITS) {
            addEntities(new CreditsElement(nextIndex));
            nextIndex++;
            timeSinceLastSpawn = 0;
        }
    }

    @Override
    public void onRender(Graphics2D g) {
        background.render(g);
    }
}
