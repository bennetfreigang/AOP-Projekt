package quirkle.game.gameplay.ui;

import quirkle.engine.Entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * The points a turn scored, flashed over the middle of the board right after the turn is ended.
 *
 * @note Lives in screen space and keeps its own clock, so it neither follows the board camera nor
 *       depends on the handover animation.
 */
public class TurnScorePopup extends Entity {

    /** Share of the flash spent at full strength, before the fade out takes the rest */
    private static final double HOLD_UNTIL = 0.5;

    private final int centerX;
    private final int centerY;

    private int points;

    private double remaining = 0.0;

    public TurnScorePopup(Rectangle boardViewport) {
        this.centerX = (int) boardViewport.getCenterX();
        this.centerY = (int) boardViewport.getCenterY();
        this.renderOrder = UiTheme.LAYER_HUD;
    }

    /**
     * Starts the flash over, showing {@code points}.
     *
     * @note A turn that scored nothing gets no popup at all; a "+0" would say less than silence.
     */
    public void show(int points) {
        if (points <= 0) return;

        this.points = points;
        this.remaining = UiTheme.SCORE_POPUP_SECONDS;
    }

    @Override
    public void onTick(double dt) {
        if (remaining > 0.0) remaining = Math.max(0.0, remaining - dt);
    }

    @Override
    public void onRender(Graphics2D g) {
        if (remaining <= 0.0) return;

        double progress = 1.0 - remaining / UiTheme.SCORE_POPUP_SECONDS;

        drawText("+" + points, UiTheme.FONT_SIZE_SCORE_POPUP, fadedText(progress), UiTheme.FONT_SCORE,
                centerX, centerY - rise(progress), 0.0, OriginPresets.CENTER, g);
    }

    /** Lifts the number off the board, fast at first and then settling. */
    private static double rise(double progress) {
        double remainingRise = 1.0 - progress;
        return UiTheme.SCORE_POPUP_RISE * (1.0 - remainingRise * remainingRise);
    }

    private static Color fadedText(double progress) {
        double alpha = progress < HOLD_UNTIL ? 1.0 : (1.0 - progress) / (1.0 - HOLD_UNTIL);

        return new Color(UiTheme.TEXT.getRed(), UiTheme.TEXT.getGreen(), UiTheme.TEXT.getBlue(),
                (int) Math.round(Math.clamp(alpha, 0.0, 1.0) * 255));
    }
}
