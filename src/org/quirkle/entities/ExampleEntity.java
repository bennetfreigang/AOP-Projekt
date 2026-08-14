package org.quirkle.entities;

import org.quirkle.PersistentData;
import org.quirkle.resourceEngine.Entity;
import org.quirkle.resourceEngine.InputManager;
import org.quirkle.resourceEngine.SceneManager;
import org.quirkle.scenes.ExampleScene2;

import java.awt.event.KeyEvent;

public class ExampleEntity extends Entity {

    @Override
    public void onCreate() {
        setTexture("/assets/textures/example.png");
        scaleX = 0.5;
        scaleY = 0.5;

        origin = Entity.OriginPresets.CENTER;

        //not really doing anything for now - Scene creation cant handle coordinate change at creation (for now)
        x = SceneManager.getCurrentScene().getCenterX();
        y = SceneManager.getCurrentScene().getCenterY();

        PersistentData.playerCount = 12;
    }

    @Override
    public void onTick(double dt) {
        if (InputManager.isKeyPressed(KeyEvent.VK_SPACE)) {
            System.out.println("Spacebar pressed!");
        }

        if (isHovered()) {
            if (scaleX <= 0.7 || scaleY <= 0.7) {
                scaleX += 0.01;
                scaleY += 0.01;
            }
        } else {
            if (scaleX > 0.5 && scaleY > 0.5) {
                scaleX -= 0.01;
                scaleY -= 0.01;
            }
        }
        if (isClicked()) {
            System.out.println("Clicked! Switching scene...");
            SceneManager.setScene(new ExampleScene2());
        }
    }
}