package quirkle.game.gamePlay.ui;

import quirkle.engine.Entity;

import java.awt.Graphics2D;
import java.util.List;

public class SideButtonBar extends Entity {

    private final SideButton settingsButton;
    private final SideButton debugButton;
    private final SideButton endTurnButton;

    private final List<SideButton> buttons;

    public SideButtonBar(int rightX, Runnable endTurnAction, Runnable debugAction) {
        this.renderOrder = UiTheme.LAYER_HUD;

        this.settingsButton = new SideButton(UiTheme.text("game_settings"), null, null);
        this.debugButton = new SideButton("Debug", null, debugAction);
        this.endTurnButton = new SideButton(null, ButtonGlyph.CONFIRM, endTurnAction);

        this.buttons = List.of(settingsButton, debugButton, endTurnButton);

        settingsButton.place(rightX, UiTheme.SIDE_BUTTON_SETTINGS_Y);
        debugButton.place(rightX, UiTheme.SIDE_BUTTON_DEBUG_Y);
        endTurnButton.place(rightX, UiTheme.SIDE_BUTTON_END_TURN_Y);
    }

    public SideButton getEndTurnButton() {
        return endTurnButton;
    }

    public SideButton getSettingsButton() { return settingsButton; }

    @Override
    public boolean isHovered() {
        for (SideButton button : buttons) {
            if (button.isHovered()) return true;
        }
        return false;
    }

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
