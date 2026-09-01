package quirkle.game.startMenu.entities;

import quirkle.engine.Entity;

import java.awt.*;

public class StartMenuButton extends Entity {

    String text;
    public double minSize = 1.0;
    double sizeModifier = minSize;
    public double maxSizeModifier = 1.1;
    public double growthSpeed = 0.01;

    public StartMenuButton(String text) {
        this.text = text;
    }

    @Override
    public void onCreate() {
        origin = OriginPresets.CENTER;
        scale = minSize;
        setSprite("startmenu/button/inactive");
    }

    @Override
    public void onTick(double dt) {
        if (isHovered() && sizeModifier <= maxSizeModifier) {
            if (sizeModifier <= maxSizeModifier) sizeModifier += growthSpeed;

            setSprite("startmenu/button/active");
        }   else if (!isHovered() && sizeModifier >= minSize) {
            if (sizeModifier >= minSize) sizeModifier -= growthSpeed;

            setSprite("startmenu/button/inactive");
        }
        scale = sizeModifier;
    }

    @Override
    public void onRender(Graphics2D g) {
        drawText(text, (float) (50*sizeModifier), Color.WHITE, "DEBUG_Poly-Regular", x, y-4, 0.0, OriginPresets.CENTER, g);
    }
}
