package org.quirkle.scenes;

import org.quirkle.entities.ExampleEntity;
import org.quirkle.resourceEngine.Scene;

public class ExampleScene extends Scene {

    @Override
    public void onCreate() {
        ExampleEntity entity = new ExampleEntity();
        add(entity);
        entity.centerAt(getCenterX(), getCenterY());
    }
}