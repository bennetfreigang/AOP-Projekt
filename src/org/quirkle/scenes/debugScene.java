package org.quirkle.scenes;

import org.quirkle.resourceEngine.Scene;
import org.quirkle.entities.TextEntity;

public class DebugScene extends Scene{
    @Override
    public void onCreate() {
        TextEntity entity = new TextEntity();
        addEntities(entity);
    }
}
