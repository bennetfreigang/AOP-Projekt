package quirkle.game.credits;

import quirkle.engine.AssetManager;
import quirkle.engine.Entity;
import quirkle.engine.Scene;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Background extends Scene {

    private String[] layers = {"big_brush", "small_brush", "grid"};
    Entity[] backgroundLayers;
    private double[] preciseY;

    private double baseVelocity = 5.0;

    Background() {
        backgroundLayers = setUpLayers();
        addEntities(backgroundLayers);

        preciseY = new double[backgroundLayers.length];
        for (int i = 0; i < backgroundLayers.length; i++) {
            backgroundLayers[i].origin = Entity.OriginPresets.CENTER;
            backgroundLayers[i].x = getCenterX();
            backgroundLayers[i].y = getCenterY();
            preciseY[i] = getCenterY();
        }
    }

    @Override
    public void onTick(double dt) {
        for (int i = 0; i < backgroundLayers.length; i++) {
            preciseY[i] -= moveVelocity(i) * dt;
            backgroundLayers[i].y = (int) preciseY[i];
        }
    }

    private Entity[] setUpLayers() {
        Entity[] layerEntities = new Entity[layers.length];
        for (int i = 0; i < layers.length; i++) {
            int finalI = i;
            Entity layer = new Entity() {
                private double baseY;
                private boolean baseYCaptured = false;

                @Override
                public void onCreate() {
                    spritePath = "credits/" + layers[finalI];
                    BufferedImage texture = AssetManager.getTexture(spritePath);
                    width = texture.getWidth();
                    height = texture.getHeight();
                }

                @Override
                public void onRender(Graphics2D g) {
                    if (!baseYCaptured) {
                        baseY = y;
                        baseYCaptured = true;
                    }

                    double wrapped = ((y - baseY) % height + height) % height;

                    drawSprite(spritePath, scale, x, (int) (baseY + wrapped), rotation, origin, g);
                    drawSprite(spritePath, scale, x, (int) (baseY + wrapped - height), rotation, origin, g);
                }
            };
            layerEntities[i] = layer;
        }

        return layerEntities;
    }

    private int moveVelocity(int index) {
        return (int) baseVelocity * (layers.length - index);
    }
}
