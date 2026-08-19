package org.quirkle.entities;

import java.awt.Color;
import java.awt.Graphics2D;

import org.quirkle.resourceEngine.*;

public class TextEntity extends Entity {
    private double speedX = 200.0;
    private double speedY = 150.0;

    @Override
    public void onCreate() {
        setSprite("iconResourceEngine");

        scale = 2.0;
        origin = Entity.OriginPresets.CENTER;

        x = SceneManager.getCurrentScene().getCenterX();
        y = SceneManager.getCurrentScene().getCenterY();
    }

    double vol = 1.0;

    private static int countTick = 0;

    //https://stackoverflow.com/questions/2626835/is-there-functionality-to-generate-a-random-character-in-java
    private static String rndChar () {
        int rnd0 = (int) (Math.random() * 52);
        int rnd1 = (int) (Math.random() * 52);
        char base0 = (rnd0 < 26) ? 'A' : 'a';
        char base1 = (rnd1 < 26) ? 'A' : 'a';
        return "" + ((char) (base0 + rnd0 % 26)) + ((char) (base1 + rnd1 % 26));

    }

    String ranS = "";
    float textScale = 40;

    @Override
    public void onTick(double dt) {
        if (vol <= 0.0) {
            x = SceneManager.getCurrentScene().getCenterX();
            y = SceneManager.getCurrentScene().getCenterY();
            scale = 16.0;
            rotation = 0.0;
            ranS = "DEMO FINISHED";
            textScale = 10;
            return;
        }

        if (countTick >= 8) {
            ranS = rndChar();
            countTick = 0;
        }   else    {
            countTick += 1;
        }

        rotation += 0.7;

        x += speedX * dt;
        y += speedY * dt;

        int halfWidth = (int) (getScaledWidth() / 2);
        int halfHeight = (int) (getScaledHeight() / 2);

        int sceneWidth = SceneManager.getCurrentScene().getCenterX() * 2;
        int sceneHeight = SceneManager.getCurrentScene().getCenterY() * 2;


        if (x - halfWidth <= 0) {
            AssetManager.playSound("chord0", vol);
            vol -= 0.1;
            x = halfWidth;
            speedX = Math.abs(speedX);
        } else if (x + halfWidth >= sceneWidth) {
            AssetManager.playSound("chord1", vol);
            vol -= 0.1;
            x = sceneWidth - halfWidth;
            speedX = -Math.abs(speedX);
        }

        if (y - halfHeight <= 0) {
            AssetManager.playSound("chord2", vol);
            vol -= 0.1;
            y = halfHeight;
            speedY = Math.abs(speedY);
        } else if (y + halfHeight >= sceneHeight) {
            AssetManager.playSound("chord3", vol);
            vol -= 0.1;
            y = sceneHeight - halfHeight;
            speedY = -Math.abs(speedY);
        }
    }

    @Override
    public void onRender(Graphics2D g) {

        drawText(ranS, (float) (textScale * scale), Color.BLACK, "DEBUG_Poly-Regular", x, y, rotation * 0.5, OriginPresets.CENTER, g);

        drawSprite("ResourceEngine", 0.4, x, y - 220, 0.0, OriginPresets.BOTTOM_MID, g);
    }
}