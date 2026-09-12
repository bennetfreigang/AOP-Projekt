package quirkle.game.endgame.scenes;

import quirkle.engine.Entity;
import quirkle.engine.AssetManager;
import quirkle.engine.Scene;
import quirkle.engine.SceneManager;
import quirkle.game.gameplay.player.Player;
import quirkle.game.startmenu.scenes.StartMenuScene;
import quirkle.game.util.RectangularButton;
import quirkle.game.util.SimpleSpriteEntity;
import quirkle.game.util.SimpleTextEntity;

import java.awt.Color;

/**
 * The screen shown once the game has run out: the winner's name, and the way back to the menu.
 */
public class EndGameScene extends Scene {

    private static final float WINNER_FONT_SIZE = 130f;

    /** How far above the middle the name sits, leaving the button the space below it */
    private static final int NAME_RISE = 90;

    private static final int BUTTON_DROP = 110;

    private final RectangularButton returnButton;

    /**
     * @param winner the player whose name the screen names
     * @implNote Everything is built here rather than in {@link #onCreate()}: the base constructor
     *           already calls that hook, which is before {@code winner} has been assigned.
     */
    public EndGameScene(Player winner) {
        SimpleSpriteEntity background =
                new SimpleSpriteEntity("quickmenu/background", Entity.OriginPresets.TOP_LEFT);

        SimpleTextEntity winnerName = new SimpleTextEntity(winner.getName(), WINNER_FONT_SIZE,
                "higher_jump", Color.WHITE, Entity.OriginPresets.CENTER);

        this.returnButton = new RectangularButton(AssetManager.getMessage("return"));

        addEntities(background, winnerName, returnButton);

        winnerName.x = getCenterX();
        winnerName.y = getCenterY() - NAME_RISE;

        returnButton.x = getCenterX();
        returnButton.y = getCenterY() + BUTTON_DROP;
    }

    @Override
    public void onTick(double dt) {
        if (returnButton.isClicked()) SceneManager.setScene(new StartMenuScene());
    }
}
