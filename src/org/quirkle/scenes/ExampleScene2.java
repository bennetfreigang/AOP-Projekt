package org.quirkle.scenes;

import org.quirkle.entities.ExampleEntityMove;
import org.quirkle.resourceEngine.Scene;

public class ExampleScene2 extends Scene {

    @Override
    public void onCreate() {
        ExampleEntityMove moveEntity = new ExampleEntityMove();
        add(moveEntity);

        //temporarily move to scene creation because "Scene creation cant handle coordinate change at creation (for now)"
        moveEntity.x = getWidth()/2;
        moveEntity.y = getHeight()/2;
    }
}