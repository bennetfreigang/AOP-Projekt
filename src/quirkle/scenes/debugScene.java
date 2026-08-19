package quirkle.scenes;

import quirkle.resourceEngine.Scene;
import quirkle.entities.TextEntity;

public class DebugScene extends Scene{
    @Override
    public void onCreate() {
        TextEntity entity = new TextEntity();
        addEntities(entity);
    }
}
