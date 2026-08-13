package org.quirkle.entities;

import org.quirkle.resourceEngine.Entity;
import org.quirkle.resourceEngine.InputManager;

public class ExampleEntityMove extends Entity {

    @Override
    public void onCreate() {
        setTexture("/assets/textures/iconResourceEngine.png");
        scaleX = 0.6;
        scaleY = 0.6;
        speed = 1000.0; // Pixels per second
    }

    @Override
    public void onTick(double dt) {
        targetX = InputManager.getMouseX() - (getScaledWidth() / 2.0);
        targetY = InputManager.getMouseY() - (getScaledHeight() / 2.0);
        moveTowardsTarget(dt);
    }
}