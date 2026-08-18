package org.quirkle.entities;

import java.awt.Color;

import org.quirkle.resourceEngine.*;

public class TextEntity extends Entity {
    private double speedX = 200.0;
    private double speedY = 150.0;

    EmbeddedText textLabel; 

    @Override
    public void onCreate() {
        setSprite("iconResourceEngine");

        scale = 2.0;
        origin = Entity.OriginPresets.CENTER;

        x = SceneManager.getCurrentScene().getCenterX();
        y = SceneManager.getCurrentScene().getCenterY();

        EmbeddedText textLabel = new EmbeddedText("DEMO", Color.BLACK, 40, "DEBUG_Poly-Regular", 0, 0, Entity.OriginPresets.CENTER);
        embedTexts.add(textLabel);
        this.textLabel = textLabel;
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

    @Override
    public void onTick(double dt) {
        if (vol <= 0.0) {
            x = SceneManager.getCurrentScene().getCenterX();
            y = SceneManager.getCurrentScene().getCenterY();
            textLabel.msg = "DEMO FINISHED";
            textLabel.fontSize = 10;
            scale = 16.0;
            rotation = 0.0;
            return;
        }

        if (countTick >= 8) {
            String randomS = rndChar();
            System.out.print(randomS + " >> ");
            textLabel.msg = randomS;
            countTick = 0;
        }   else    {
            countTick += 1;
        }

        rotation += 0.7;

        x += speedX * dt;
        y += speedY * dt;

        double halfWidth = getScaledWidth() / 2.0;
        double halfHeight = getScaledHeight() / 2.0;

        double sceneWidth = SceneManager.getCurrentScene().getCenterX() * 2.0;
        double sceneHeight = SceneManager.getCurrentScene().getCenterY() * 2.0;


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
}