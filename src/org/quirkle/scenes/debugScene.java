package org.quirkle.scenes;

import org.quirkle.resourceEngine.Scene;
import org.quirkle.entities.TextEntity;

public class debugScene extends Scene{
    @Override
    public void onCreate() {
        TextEntity tentity = new TextEntity();
        addEntities(tentity);
    }
}
