package quirkle.game.gameplay.ui;

import quirkle.game.gameplay.Game;
import quirkle.game.gameplay.player.Player;

import java.awt.Graphics2D;

public class PlayerCard extends PanelEntity {

    private final Game game;
    private final Handover handover;

    private Player shownPlayer;

    public PlayerCard(Game game, Handover handover) {
        this.game = game;
        this.handover = handover;

        takeOver();

        setSprite(UiTheme.SPRITE_PLAYER_CARD);
        setBounds(UiTheme.CARD_LEFT, UiTheme.cardTop(),
                UiTheme.CARD_WIDTH, UiTheme.CARD_HEIGHT, OriginPresets.TOP_LEFT);
    }

    @Override
    public void onTick(double dt) {
        // Like the rack, the card only changes hands during the moment it cannot be seen.
        if (!handover.isRunning() || handover.getPhase() == Handover.Phase.ARRIVING) takeOver();

        applySlide();
    }

    private void takeOver() {
        shownPlayer = game.getCurrentPlayer();
    }

    private void applySlide() {
        if (!handover.isRunning()) {
            y = UiTheme.cardTop();
            return;
        }

        int hiddenTop = UiTheme.cardHiddenTop();
        double progress = handover.getPhaseProgress();

        y = handover.getPhase() == Handover.Phase.LEAVING
                ? Handover.at(UiTheme.cardTop(), hiddenTop, progress)
                : Handover.at(hiddenTop, UiTheme.cardTop(), progress);
    }

    @Override
    public void onRender(Graphics2D g) {
        drawText(String.valueOf(shownPlayer.getScore()), UiTheme.FONT_SIZE_CARD_SCORE, UiTheme.TEXT,
                UiTheme.FONT_SCORE, getLeft() + getWidth() / 2, getTop() + UiTheme.CARD_SCORE_CENTER_Y,
                0.0, OriginPresets.CENTER, g);
    }
}
