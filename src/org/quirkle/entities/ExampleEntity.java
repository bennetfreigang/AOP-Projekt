package org.quirkle.entities;

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
    }

    @Override
    public void onTick(double dt) {
        if (InputManager.isKeyPressed(KeyEvent.VK_RIGHT)) {
            System.out.println("[ExampleEntity] Spacebar pressed!");
        }

        if (isHovered()) {
        	if(scaleX <= 0.7 || scaleY <= 0.7) {
        		centerAt(SceneManager.getCurrentScene().getCenterX(), SceneManager.getCurrentScene().getCenterY());
        		scaleX += 0.01;
        		scaleY += 0.01;
        	}
        }	else	{
        	if(scaleX > 0.5 && scaleY > 0.5) {
        		centerAt(SceneManager.getCurrentScene().getCenterX(), SceneManager.getCurrentScene().getCenterY());
        		scaleX -= 0.01;
        		scaleY -= 0.01;
        	}
        }
        if (isClicked()) {
            System.out.println("[ExampleEntity] Clicked! Switching scene...");
            SceneManager.setScene(new ExampleScene2());
        }
    }
}