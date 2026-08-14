package org.quirkle.scenes;

import org.quirkle.entities.ExampleEntity;
import org.quirkle.resourceEngine.Scene;

public class ExampleScene extends Scene {

    @Override
    public void onCreate() {
        ExampleEntity entity = new ExampleEntity();
        add(entity);

        //temporarily move to scene creation because "Scene creation cant handle coordinate change at creation (for now)"
        entity.x = getWidth()/2;
        entity.y = getHeight()/2;
    }
}