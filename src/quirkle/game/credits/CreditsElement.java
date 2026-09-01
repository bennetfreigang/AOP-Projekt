package quirkle.game.credits;

import quirkle.engine.AssetManager;
import quirkle.engine.Entity;
import quirkle.engine.SceneManager;

import java.awt.*;

public class CreditsElement extends Entity {

    private final int TITLE_FONT_SIZE = 42;
    private final int CONTENT_FONT_SIZE = 24;
    private final String FONT_IDENTIFIER = "TITEL";
    private final double SCROLL_SPEED = 80.0;   // pixel per second
    private final int ESCAPE_TARGET_Y = -2000;  // is never actually reached

    private final String title;
    private final String content;

    private double preciseY;

    public CreditsElement(int item) {
        this.title = AssetManager.getMessage("creditsTitle" + item);
        this.content = AssetManager.getMessage("creditsContent" + item);
    }

    @Override
    public void onCreate() {
        origin = OriginPresets.CENTER;

        x = SceneManager.getCurrentScene().getCenterX();
        preciseY = SceneManager.getCurrentScene().getHeight() + 60;
        y = (int) preciseY;
    }

    @Override
    public void onTick(double dt) {
        preciseY -= SCROLL_SPEED * dt;
        y = (int) preciseY;

        if (isOffScreenTop()) {
            destroy();
        }
    }

    private boolean isOffScreenTop() {
        return y < -100; // Titel + Content = ca. 80-100px (height)
    }

    @Override
    public void onRender(Graphics2D g) {
        drawTitle(g);
        drawContent(g);
    }

    private void drawTitle(Graphics2D g) {
        drawText(title, TITLE_FONT_SIZE, Color.WHITE, FONT_IDENTIFIER, x, y, 0.0, OriginPresets.CENTER, g);
    }

    private void drawContent(Graphics2D g) {
        drawText(content, CONTENT_FONT_SIZE, Color.WHITE, FONT_IDENTIFIER, x, y + 40, 0.0, OriginPresets.CENTER, g);
    }
}
