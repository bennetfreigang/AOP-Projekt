package quirkle.game.gamePlay.ui;

import quirkle.engine.Entity;

import java.awt.Graphics2D;
import java.util.List;

/**
 * The four buttons stacked along the right edge: new game, take back, end turn and settings.
 *
 * @note Only end turn is wired up; the other three are placeholders that render and swallow
 *       their clicks but do nothing, since the actions behind them do not exist yet. Giving one
 *       a function later means handing it a {@code Runnable} through
 *       {@link SideButton#setAction(Runnable)}, and its artwork through
 *       {@link SideButton#setArtwork(String)}.
 * @implNote Draws and ticks its buttons itself rather than registering them as scene entities,
 *           so the scene adds and removes the whole bar as one unit.
 */
public class SideButtonBar extends Entity {

    private final SideButton newGameButton;
    private final SideButton undoButton;
    private final SideButton endTurnButton;
    private final SideButton settingsButton;

    private final List<SideButton> buttons;

    /**
     * @param rightX screen x the buttons' right edges line up on
     * @param endTurnAction what the checkmark button does
     */
    public SideButtonBar(int rightX, Runnable endTurnAction) {
        this.renderOrder = UiTheme.LAYER_HUD;

        this.newGameButton = new SideButton(UiTheme.text("new_game"), null, null);
        this.undoButton = new SideButton(null, ButtonGlyph.UNDO, null);
        this.endTurnButton = new SideButton(null, ButtonGlyph.CONFIRM, endTurnAction);
        this.settingsButton = new SideButton(UiTheme.text("game_settings"), null, null);

        this.buttons = List.of(newGameButton, undoButton, endTurnButton, settingsButton);

        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).place(rightX, UiTheme.SIDE_BUTTON_CENTERS_Y[i]);
        }
    }

    /** @return the checkmark button, so the scene can enable it once a tile is staged. */
    public SideButton getEndTurnButton() {
        return endTurnButton;
    }

    public SideButton getNewGameButton() { return newGameButton; }

    public SideButton getUndoButton() { return undoButton; }

    public SideButton getSettingsButton() { return settingsButton; }

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
