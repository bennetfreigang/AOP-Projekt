package quirkle.game.gamePlay.ui;

import quirkle.game.gamePlay.player.Player;

import java.awt.Graphics2D;

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

    private String formatLastRound() {
        return "+" + player.getLastRoundScore();
    }
}
