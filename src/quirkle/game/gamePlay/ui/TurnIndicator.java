package quirkle.game.gamePlay.ui;

import quirkle.game.gamePlay.Game;
import quirkle.game.gamePlay.player.Player;

import java.awt.Graphics2D;

/**
 * The banner in the band above the frame: whose turn it is, which turn it is and what the game
 * is waiting for.
 *
 * @note Deliberately does not repeat the scores. Those are on the cards for the waiting players;
 *       the banner answers only "whose move is it, and now what".
 * @note Leaves upwards on a handover and the next one drops in from above. It therefore has to
 *       hold on to the name and turn number it is carrying rather than reading them live: the
 *       banner on its way out still belongs to the player who just finished.
 */
public class TurnIndicator extends PanelEntity {

    /** Separator between the turn number and the prompt. */
    private static final String SEPARATOR = "  ·  ";

    private final Game game;
    private final Handover handover;

    /** The player the banner is carrying, which lags the game while one banner swaps for the next. */
    private Player shownPlayer;
    private int shownTurnNumber;

    /** @param centerX horizontal screen center the banner is centered on */
    public TurnIndicator(Game game, int centerX, Handover handover) {
        this.game = game;
        this.handover = handover;

        // Read straight away rather than waiting for the first tick: the window can paint before
        // the game loop has run a single frame.
        this.shownPlayer = game.getCurrentPlayer();
        this.shownTurnNumber = game.getTurnNumber();

        setFrame(UiTheme.FRAME_PLAYER_CARD);
        setBounds(centerX, UiTheme.TURN_PANEL_TOP,
                UiTheme.TURN_PANEL_WIDTH, UiTheme.TURN_PANEL_HEIGHT, OriginPresets.TOP_MID);
    }

    @Override
    public void onTick(double dt) {
        // The outgoing banner keeps its name all the way off the top; the incoming one is already
        // the new player's by the time it drops back into view.
        if (!handover.isRunning() || handover.getPhase() == Handover.Phase.ARRIVING) {
            shownPlayer = game.getCurrentPlayer();
            shownTurnNumber = game.getTurnNumber();
        }

        applySlide();
    }

    /** Puts the banner where the handover clock says it should be: off the top and back down. */
    private void applySlide() {
        if (!handover.isRunning()) {
            y = UiTheme.TURN_PANEL_TOP;
            return;
        }

        int hiddenTop = UiTheme.turnHiddenTop();
        double progress = handover.getPhaseProgress();

        y = handover.getPhase() == Handover.Phase.LEAVING
                ? Handover.at(UiTheme.TURN_PANEL_TOP, hiddenTop, progress)
                : Handover.at(hiddenTop, UiTheme.TURN_PANEL_TOP, progress);
    }

    @Override
    public void onRender(Graphics2D g) {
        drawFrame(g);

        int centerX = getLeft() + getWidth() / 2;
        int textY = getTop() + UiTheme.TURN_PADDING_Y;

        drawText(shownPlayer.getName().toUpperCase(), UiTheme.FONT_SIZE_TURN_NAME, UiTheme.GOLD,
                UiTheme.FONT, centerX, textY, 0.0, OriginPresets.TOP_MID, g);

        drawText(buildStatusLine(), UiTheme.FONT_SIZE_TURN_STATUS, UiTheme.TEXT_DIMMED,
                UiTheme.FONT, centerX, textY + UiTheme.TURN_LINE_HEIGHT, 0.0, OriginPresets.TOP_MID, g);
    }

    /**
     * @return the turn number followed by what the player is expected to do, or by how many tiles
     *         they have staged once they have started placing
     */
    private String buildStatusLine() {
        int stagedTiles = game.getBoard().getPendingTiles().size();

        String prompt = stagedTiles == 0
                ? UiTheme.text("turn_place_tile")
                : stagedTiles + " " + UiTheme.text(stagedTiles == 1 ? "turn_tile_placed" : "turn_tiles_placed");

        return UiTheme.text("turn") + " " + shownTurnNumber + SEPARATOR + prompt;
    }
}
