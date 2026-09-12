package quirkle.game.gameplay.ui;

import quirkle.engine.AssetManager;
import quirkle.engine.Entity;
import quirkle.game.gameplay.Game;
import quirkle.game.gameplay.player.Player;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.List;

public class TurnIndicator extends Entity {

    private final Game game;
    private final Handover handover;

    private Player shownPlayer;

    public TurnIndicator(Game game, Handover handover) {
        this.game = game;
        this.handover = handover;
        this.renderOrder = UiTheme.LAYER_HUD;

        // Read straight away rather than waiting for the first tick: the window can paint before
        // the game loop has run a single frame.
        this.shownPlayer = game.getCurrentPlayer();

        this.y = UiTheme.TURN_TOP;
    }

    @Override
    public void onTick(double dt) {
        // The outgoing row keeps its order all the way off the top; the incoming one is already
        // the new player's by the time it drops back into view.
        if (!handover.isRunning() || handover.getPhase() == Handover.Phase.ARRIVING) {
            shownPlayer = game.getCurrentPlayer();
        }

        applySlide();
    }

    private void applySlide() {
        if (!handover.isRunning()) {
            y = UiTheme.TURN_TOP;
            return;
        }

        int hiddenTop = UiTheme.turnHiddenTop();
        double progress = handover.getPhaseProgress();

        y = handover.getPhase() == Handover.Phase.LEAVING
                ? Handover.at(UiTheme.TURN_TOP, hiddenTop, progress)
                : Handover.at(hiddenTop, UiTheme.TURN_TOP, progress);
    }

    @Override
    public void onRender(Graphics2D g) {
        List<Player> players = game.getPlayers();
        int currentIndex = Math.max(players.indexOf(shownPlayer), 0);

        int baseline = (int) y + metrics(g, UiTheme.FONT_SIZE_TURN_CURRENT).getAscent();
        int nextAscent = metrics(g, UiTheme.FONT_SIZE_TURN_NEXT).getAscent();
        int x = UiTheme.TURN_MARGIN_X;

        for (int offset = 0; offset < players.size(); offset++) {
            Player player = players.get((currentIndex + offset) % players.size());
            boolean isCurrent = offset == 0;

            float fontSize = isCurrent ? UiTheme.FONT_SIZE_TURN_CURRENT : UiTheme.FONT_SIZE_TURN_NEXT;

            drawText(player.getName(), fontSize, isCurrent ? UiTheme.TEXT : UiTheme.TEXT_DIMMED,
                    UiTheme.FONT, x, isCurrent ? y : baseline - nextAscent,
                    0.0, OriginPresets.TOP_LEFT, g);

            x += textWidth(g, player.getName(), fontSize)
                    + (isCurrent ? UiTheme.TURN_CURRENT_GAP : UiTheme.TURN_NAME_GAP);
        }
    }

    private static int textWidth(Graphics2D g, String text, float fontSize) {
        return metrics(g, fontSize).stringWidth(text);
    }

    private static FontMetrics metrics(Graphics2D g, float fontSize) {
        Font font = AssetManager.getFont(UiTheme.FONT).deriveFont(fontSize);
        return g.getFontMetrics(font);
    }
}
