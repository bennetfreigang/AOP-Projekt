package org.quirkle.entities;

import java.awt.event.KeyEvent;

import org.quirkle.PersistentData;
import org.quirkle.resourceEngine.Entity;
import org.quirkle.resourceEngine.InputManager;
import org.quirkle.resourceEngine.SceneManager;
import org.quirkle.scenes.ExampleScene;

public class ExampleEntityMove extends Entity {

    @Override
    public void onCreate() {
        setTexture("/assets/textures/iconResourceEngine.png");
        scaleX = 0.6;
        scaleY = 0.6;
        speed = 1000.0; // Pixels per second

        origin = Entity.OriginPresets.CENTER;

        //not really doing anything for now - Scene creation cant handle coordinate change at creation (for now)
        x = SceneManager.getCurrentScene().getCenterX();
        y = SceneManager.getCurrentScene().getCenterY();

        //testing the PersistentData playerCount
        System.out.println("PersistentData playerCount: " + PersistentData.playerCount);
    }

    @Override
    public void onTick(double dt) {
        if (!InputManager.isKeyDown(KeyEvent.VK_SPACE)) {
            targetX = InputManager.getMouseX();
            targetY = InputManager.getMouseY();
            moveTowardsTarget(dt);
        }
        if (isClicked()) {
            SceneManager.setScene(new ExampleScene());
        }
    }
}