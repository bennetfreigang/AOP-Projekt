package quirkle.game.gamePlay.ui;

import quirkle.game.gamePlay.Game;
import quirkle.game.gamePlay.player.Player;

import java.awt.Color;
import java.awt.Graphics2D;

public class TurnIndicator extends PanelEntity {

    private static final String SEPARATOR = "  ·  ";

    private final Game game;
    private final Handover handover;

    private Player shownPlayer;
    private int shownTurnNumber;

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

    private String buildStatusLine() {
        int stagedTiles = game.getBoard().getPendingTiles().size();

        String prompt = stagedTiles == 0
                ? UiTheme.text("turn_place_tile")
                : stagedTiles + " " + UiTheme.text(stagedTiles == 1 ? "turn_tile_placed" : "turn_tiles_placed");

        return UiTheme.text("turn") + " " + shownTurnNumber + SEPARATOR + prompt;
    }
}
