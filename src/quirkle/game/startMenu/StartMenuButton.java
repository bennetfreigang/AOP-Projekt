package quirkle.game.startMenu;

import quirkle.resourceEngine.Entity;

import java.awt.*;

public class StartMenuButton extends Entity {

    String text;

    StartMenuButton(String text) {
        this.text = text;
    }

    @Override
    public void onCreate() {
        origin = OriginPresets.CENTER;
        setSprite("button");
    }

    @Override
    public void onRender(Graphics2D g) {
        drawText(text, 30, Color.BLACK, "DEBUG_Poly-Regular", x, y, 0.0, OriginPresets.CENTER, g);
    }
}
