package quirkle.game.gamePlay.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;

/**
 * The pictograms on the icon-only side buttons, drawn as vector shapes.
 *
 * @note Not text: {@code DEBUG_Poly-Regular} carries no glyph for U+21BA or U+2713, so setting
 *       them as a string would draw two missing-glyph boxes.
 */
public enum ButtonGlyph {
    /** Circular arrow, for taking the staged tiles back. */
    UNDO,
    /** Checkmark, for ending the turn. */
    CONFIRM;

    /** Stroke width relative to {@code size}, so the mark scales as one piece. */
    private static final double STROKE_RATIO = 0.11;

    /**
     * Draws the glyph centered on ({@code centerX}, {@code centerY}).
     *
     * @param size edge length of the square the mark is drawn inside
     */
    public void draw(Graphics2D g, int centerX, int centerY, int size, Color color) {
        Graphics2D gGlyph = (Graphics2D) g.create();
        gGlyph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gGlyph.setColor(color);
        gGlyph.setStroke(new BasicStroke((float) (size * STROKE_RATIO),
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (this) {
            case UNDO -> drawUndo(gGlyph, centerX, centerY, size);
            case CONFIRM -> drawConfirm(gGlyph, centerX, centerY, size);
        }

        gGlyph.dispose();
    }

    /** An almost-closed ring with an arrowhead on its upper end. */
    private void drawUndo(Graphics2D g, int centerX, int centerY, int size) {
        double radius = size * 0.36;
        double diameter = radius * 2;

        // Left open, so the gap and the arrowhead sit next to each other at the top.
        g.draw(new Arc2D.Double(centerX - radius, centerY - radius, diameter, diameter,
                100, 320, Arc2D.OPEN));

        double headSize = size * 0.2;
        double headX = centerX + radius * Math.cos(Math.toRadians(100));
        double headY = centerY - radius * Math.sin(Math.toRadians(100));

        Path2D.Double head = new Path2D.Double();
        head.moveTo(headX - headSize * 0.5, headY - headSize * 0.5);
        head.lineTo(headX + headSize * 0.6, headY - headSize * 0.1);
        head.lineTo(headX - headSize * 0.1, headY + headSize * 0.6);
        head.closePath();
        g.fill(head);
    }

    /** A two-segment checkmark. */
    private void drawConfirm(Graphics2D g, int centerX, int centerY, int size) {
        Path2D.Double check = new Path2D.Double();
        check.moveTo(centerX - size * 0.34, centerY + size * 0.02);
        check.lineTo(centerX - size * 0.09, centerY + size * 0.27);
        check.lineTo(centerX + size * 0.36, centerY - size * 0.29);
        g.draw(check);
    }
}
