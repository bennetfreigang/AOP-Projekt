package org.quirkle.entities;

import org.quirkle.resourceEngine.Entity;
import org.quirkle.resourceEngine.SceneManager;
import org.quirkle.scenes.ExampleScene2;

public class ExampleEntity extends Entity {

    @Override
    public void onCreate() {
        setTexture("/assets/textures/example.png");
        scaleX = 0.5;
        scaleY = 0.5;
    }

    @Override
    public void onTick(double dt) {
        if (isHovered()) {
            //i had some debug code here but removed it - but its a good example i guess
        }
        if (isClicked()) {
            System.out.println("[ExampleEntity] Clicked! Switching scene...");
            SceneManager.setScene(new ExampleScene2());
        }
    }
}