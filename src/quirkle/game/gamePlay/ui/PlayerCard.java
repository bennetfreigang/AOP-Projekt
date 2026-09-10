package quirkle.game.gamePlay.ui;

import quirkle.game.gamePlay.player.Player;

import java.awt.Graphics2D;

/**
 * One waiting player's card on the left edge: name, total score and what they scored last turn.
 *
 * @note Read-only view onto its {@link Player}; it never writes back.
 * @see PlayerCardColumn for the stacking and for which players get a card
 */
public class PlayerCard extends PanelEntity {

    private final Player player;

    public PlayerCard(Player player) {
        this.player = player;
        setFrame(UiTheme.FRAME_PLAYER_CARD);
        setBounds(UiTheme.CARD_LEFT, UiTheme.CARD_TOP,
                UiTheme.CARD_WIDTH, UiTheme.CARD_HEIGHT, OriginPresets.TOP_LEFT);
    }

    public Player getPlayer() {
        return player;
    }

    /** Moves the card so its top edge sits at {@code top}. */
    public void setTop(int top) {
        this.y = top;
    }

    @Override
    public void onRender(Graphics2D g) {
        drawFrame(g);

        int textX = getLeft() + UiTheme.CARD_PADDING_X;
        int textY = getTop() + UiTheme.CARD_PADDING_Y;

        drawLine(g, player.getName().toUpperCase(), textX, textY);
        drawLine(g, UiTheme.text("points") + ": " + player.getScore(),
                textX, textY + UiTheme.CARD_LINE_HEIGHT);
        drawLine(g, UiTheme.text("last_round") + ": " + formatLastRound(),
                textX, textY + 2 * UiTheme.CARD_LINE_HEIGHT);
    }

    private void drawLine(Graphics2D g, String text, int x, int y) {
        drawText(text, UiTheme.FONT_SIZE_CARD, UiTheme.TEXT, UiTheme.FONT,
                x, y, 0.0, OriginPresets.TOP_LEFT, g);
    }

    /** @return the last turn's score with an explicit {@code +}, as the mockup shows it. */
    private String formatLastRound() {
        return "+" + player.getLastRoundScore();
    }
}
