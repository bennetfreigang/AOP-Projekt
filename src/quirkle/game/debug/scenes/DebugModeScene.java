package quirkle.game.debug.scenes;

import quirkle.engine.Entity;
import quirkle.engine.Scene;
import quirkle.game.debug.DebugAction;
import quirkle.game.debug.DebugMode;
import quirkle.game.gamePlay.ui.SideButton;
import quirkle.game.gamePlay.ui.UiTheme;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * The debug panel: one button per {@link DebugAction}, grouped under a heading per
 * {@link DebugAction.Group}, plus a button back to the game.
 *
 * @note Driven directly by {@link quirkle.game.gamePlay.GamePlayScene} - {@code update()} and
 *       {@code render()} called by hand, {@code destroy()} on close - rather than opened through
 *       {@link quirkle.engine.SceneManager#setTempScene}. The gameplay scene is already itself a
 *       temp scene over the start menu, and {@code SceneManager} only tracks one background scene
 *       at a time, so a second nested {@code setTempScene} call would destroy the gameplay scene
 *       instead of just this panel.
 */
public class DebugModeScene extends Scene {

    private static final float TITLE_FONT_SIZE = 34f;
    private static final int TOP_MARGIN = 60;
    private static final int TITLE_TO_GROUPS_GAP = 70;
    private static final int HEADING_HEIGHT = 40;
    private static final int BUTTON_GAP = 10;
    private static final int GROUP_GAP = 20;

    private List<SideButton> buttons;
    private SideButton backButton;

    /**
     * @note {@code buttons} is filled here rather than at field-declaration: {@link Scene}'s
     *       constructor calls {@code onCreate()} before a subclass's own field initializers run,
     *       so a {@code = new ArrayList<>()} initializer would still be {@code null} at this point.
     *       The same reasoning is why {@link #setOnClose} exists instead of a constructor
     *       parameter: a value handed to this class's own constructor would not be assigned to a
     *       field until after {@code super()} - and therefore after {@code onCreate()} - has
     *       already run.
     */
    @Override
    public void onCreate() {
        buttons = new ArrayList<>();

        int centerX = getCenterX();
        int y = TOP_MARGIN;

        addEntities(new SectionHeading("DEBUG MODE", centerX, y, TITLE_FONT_SIZE));
        y += TITLE_TO_GROUPS_GAP;

        for (DebugAction.Group group : DebugAction.Group.values()) {
            addEntities(new SectionHeading(group.getLabel(), centerX, y + HEADING_HEIGHT / 2, UiTheme.FONT_SIZE_BUTTON));
            y += HEADING_HEIGHT;

            for (DebugAction action : DebugAction.of(group)) {
                y += placeActionButton(action, centerX, y);
            }

            y += GROUP_GAP;
        }

        backButton = new SideButton("Back to game", null, null);
        backButton.place(centerX + UiTheme.SIDE_BUTTON_WIDTH / 2, y + UiTheme.SIDE_BUTTON_HEIGHT / 2);
        addEntities(backButton);
        buttons.add(backButton);
    }

    /** @param onClose what closing the panel does; wired in by whoever opens this scene. */
    public void setOnClose(Runnable onClose) {
        backButton.setAction(onClose);
    }

    /** @return how far the row cursor advances past this button. */
    private int placeActionButton(DebugAction action, int centerX, int rowTop) {
        SideButton button = new SideButton(action.getLabel(), null, action::run);
        button.setEnabled(DebugMode.hasGame());
        button.place(centerX + UiTheme.SIDE_BUTTON_WIDTH / 2, rowTop + UiTheme.SIDE_BUTTON_HEIGHT / 2);

        addEntities(button);
        buttons.add(button);

        return UiTheme.SIDE_BUTTON_HEIGHT + BUTTON_GAP;
    }

    @Override
    public void onTick(double dt) {
        for (SideButton button : buttons) {
            button.handleInput();
        }
    }

    /** @note Dims the gameplay scene showing through underneath so the panel's own text reads. */
    @Override
    public void onRender(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    /** A heading centered on ({@code centerX}, {@code centerY}); the title or one group's label. */
    private static final class SectionHeading extends Entity {
        private final String text;
        private final float fontSize;

        SectionHeading(String text, int centerX, int centerY, float fontSize) {
            this.text = text;
            this.fontSize = fontSize;
            this.x = centerX;
            this.y = centerY;
        }

        @Override
        public void onRender(Graphics2D g) {
            drawText(text, fontSize, UiTheme.GOLD, UiTheme.FONT, x, y, 0.0, OriginPresets.CENTER, g);
        }
    }
}
