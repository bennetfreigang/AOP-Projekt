package quirkle.game.gameplay.ui;

import quirkle.game.gameplay.Game;
import quirkle.game.gameplay.player.Player;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * The banner in the band above the frame: whose turn it is, which turn it is and what the game
 * is waiting for.
 *
 * @note Three lines, three weights: the name is the headline and carries the accent color, the
 *       score is the number being played for, and the turn and prompt are background information
 *       that steps back into {@link UiTheme#TEXT_DIMMED}. The waiting players' scores stay on
 *       their cards; this banner only ever speaks for whoever is at the table.
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
        int panelTop = getTop();

        drawLine(g, shownPlayer.getName().toUpperCase(), UiTheme.FONT_SIZE_TURN_NAME,
                UiTheme.GOLD, centerX, panelTop + UiTheme.TURN_NAME_TOP);

        drawLine(g, UiTheme.text("points") + ": " + shownPlayer.getScore(), UiTheme.FONT_SIZE_TURN_SCORE,
                UiTheme.TEXT, centerX, panelTop + UiTheme.TURN_SCORE_TOP);

        drawLine(g, buildStatusLine(), UiTheme.FONT_SIZE_TURN_STATUS,
                UiTheme.TEXT_DIMMED, centerX, panelTop + UiTheme.TURN_STATUS_TOP);
    }

    private void drawLine(Graphics2D g, String text, float fontSize, Color color, int centerX, int top) {
        drawText(text, fontSize, color, UiTheme.FONT, centerX, top, 0.0, OriginPresets.TOP_MID, g);
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
