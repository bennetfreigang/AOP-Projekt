package org.quirkle.scenes;

import org.quirkle.entities.ExampleEntityMove;
import org.quirkle.resourceEngine.Scene;

public class ExampleScene2 extends Scene {

    @Override
    public void onCreate() {
        ExampleEntityMove moveEntity = new ExampleEntityMove();
        add(moveEntity);
        moveEntity.centerAt(getCenterX(), getCenterY());
    }
}