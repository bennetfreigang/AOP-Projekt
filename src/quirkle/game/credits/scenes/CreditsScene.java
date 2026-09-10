package quirkle.game.credits.scenes;

import quirkle.engine.*;
import quirkle.game.credits.entities.*;
import quirkle.game.util.*;
import quirkle.game.util.SimpleSpriteEntity;

import java.awt.event.KeyEvent;

public class CreditsScene extends Scene {
    PrlxEntity prlxBackground;
    CreditTextManager creditManager;

    @Override
    public void onCreate() {
        PrlxEntity.PrlxLayer prlxLayer0 = new PrlxEntity.PrlxLayer("credits/grid", 0.1);
        PrlxEntity.PrlxLayer prlxLayer1 = new PrlxEntity.PrlxLayer("credits/big_brush", 0.2);
        PrlxEntity.PrlxLayer prlxLayer2 = new PrlxEntity.PrlxLayer("credits/small_brush", 0.3);

        prlxBackground = new PrlxEntity(prlxLayer0, prlxLayer1, prlxLayer2);
        creditManager = new CreditTextManager(6);
        SimpleSpriteEntity frame = new SimpleSpriteEntity("credits/credit_frame", Entity.OriginPresets.TOP_LEFT);

        addEntities(prlxBackground, creditManager, frame);
    }

    @Override
    public void onTick(double dt) {
        prlxBackground.tarY = (creditManager.y + creditManager.viewportHeight)/4; // good enough

        if (InputManager.isKeyPressed(KeyEvent.VK_ESCAPE)) SceneManager.stopTempScene();
    }
}
