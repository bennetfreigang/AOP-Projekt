package scenes.gameplay;

import engine.EngineConfig;
import engine.Entity;
import engine.assets.AssetManager;
import model.board.Move;
import ui.UiTheme;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/** Shows the points of the hinted move in the top right corner, on the same baseline as the turn order */
public class HintScoreDisplay extends Entity {

    private boolean visible = false;
    private Move move;

    public HintScoreDisplay() {
        this.renderOrder = UiTheme.LAYER_HUD;
        this.y = UiTheme.TURN_TOP;
    }

    /** @param move the hinted move, null if the hand has no legal move */
    public void show(Move move) {
        this.visible = true;
        this.move = move;
    }

    public void hide() {
        this.visible = false;
        this.move = null;
    }

    @Override
    public void onRender(Graphics2D g) {
        if (!visible) return;

        int right = EngineConfig.WINDOW_WIDTH - UiTheme.TURN_MARGIN_X;
        int baseline = (int) y + metrics(g, UiTheme.FONT, UiTheme.FONT_SIZE_TURN_CURRENT).getAscent();

        if (move == null) {
            drawOnBaseline(g, UiTheme.text("hint_no_move"), UiTheme.FONT, UiTheme.FONT_SIZE_TURN_NEXT,
                    UiTheme.TEXT_DIMMED, right, baseline);
            return;
        }

        String score = String.valueOf(move.score());
        drawOnBaseline(g, score, UiTheme.FONT_SCORE, UiTheme.FONT_SIZE_TURN_CURRENT, UiTheme.GOLD, right, baseline);

        int labelRight = right - metrics(g, UiTheme.FONT_SCORE, UiTheme.FONT_SIZE_TURN_CURRENT).stringWidth(score)
                - UiTheme.TURN_NAME_GAP;
        drawOnBaseline(g, UiTheme.text("hint_best_move"), UiTheme.FONT, UiTheme.FONT_SIZE_TURN_NEXT,
                UiTheme.TEXT_DIMMED, labelRight, baseline);
    }

    /** draws {@code text} right-aligned to {@code right}, sitting on {@code baseline} */
    private void drawOnBaseline(Graphics2D g, String text, String fontIdentifier, float fontSize,
                                Color color, int right, int baseline) {
        int top = baseline - metrics(g, fontIdentifier, fontSize).getAscent();
        drawText(text, fontSize, color, fontIdentifier, right, top, 0.0, OriginPresets.TOP_RIGHT, g);
    }

    private static FontMetrics metrics(Graphics2D g, String fontIdentifier, float fontSize) {
        Font font = AssetManager.getFont(fontIdentifier).deriveFont(fontSize);
        return g.getFontMetrics(font);
    }
}
