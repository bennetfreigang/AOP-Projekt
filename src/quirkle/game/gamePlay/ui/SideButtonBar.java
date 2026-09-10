package quirkle.game.gamePlay.ui;

import quirkle.engine.Entity;

import java.awt.Graphics2D;
import java.util.List;

/**
 * The two buttons along the right edge: settings at the top, end turn at the bottom.
 *
 * @note Only end turn is wired up; settings is a placeholder that renders and swallows its
 *       clicks but does nothing, since the action behind it does not exist yet. Giving it a
 *       function later means handing it a {@code Runnable} through
 *       {@link SideButton#setAction(Runnable)}, and its artwork through
 *       {@link SideButton#setArtwork(String)}.
 * @implNote Draws and ticks its buttons itself rather than registering them as scene entities,
 *           so the scene adds and removes the whole bar as one unit.
 */
public class SideButtonBar extends Entity {

    private final SideButton settingsButton;
    private final SideButton endTurnButton;

    private final List<SideButton> buttons;

    /**
     * @param rightX screen x the buttons' right edges line up on
     * @param endTurnAction what the checkmark button does
     */
    public SideButtonBar(int rightX, Runnable endTurnAction) {
        this.renderOrder = UiTheme.LAYER_HUD;

        this.settingsButton = new SideButton(UiTheme.text("game_settings"), null, null);
        this.endTurnButton = new SideButton(null, ButtonGlyph.CONFIRM, endTurnAction);

        this.buttons = List.of(settingsButton, endTurnButton);

        settingsButton.place(rightX, UiTheme.SIDE_BUTTON_SETTINGS_Y);
        endTurnButton.place(rightX, UiTheme.SIDE_BUTTON_END_TURN_Y);
    }

    /** @return the checkmark button, so the scene can enable it once a tile is staged. */
    public SideButton getEndTurnButton() {
        return endTurnButton;
    }

    public SideButton getSettingsButton() { return settingsButton; }

    /**
     * @return whether the mouse is over one of the bar's buttons
     * @note Asks the buttons rather than the bar itself: the bar is a container without bounds
     *       of its own, so the inherited check would never report a hit.
     */
    @Override
    public boolean isHovered() {
        for (SideButton button : buttons) {
            if (button.isHovered()) return true;
        }
        return false;
    }

    /**
     * Lets the bar react to the current frame's click.
     *
     * @return {@code true} if the click hit any button and the scene should not also act on it
     * @note Call this before the board input handling, since
     *       {@link quirkle.engine.InputManager#isMouseClicked()} is a global flag that both
     *       would otherwise consume.
     */
    public boolean handleInput() {
        for (SideButton button : buttons) {
            if (button.handleInput()) return true;
        }
        return false;
    }

    @Override
    public void onRender(Graphics2D g) {
        for (SideButton button : buttons) {
            button.render(g);
        }
    }
}
