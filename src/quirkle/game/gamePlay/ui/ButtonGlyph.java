package quirkle.game.gamePlay.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

public enum ButtonGlyph {
    CONFIRM;

    private static final double STROKE_RATIO = 0.11;

    public void draw(Graphics2D g, int centerX, int centerY, int size, Color color) {
        Graphics2D gGlyph = (Graphics2D) g.create();
        gGlyph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gGlyph.setColor(color);
        gGlyph.setStroke(new BasicStroke((float) (size * STROKE_RATIO),
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (this) {
            case CONFIRM -> drawConfirm(gGlyph, centerX, centerY, size);
        }

        gGlyph.dispose();
    }

    private void drawConfirm(Graphics2D g, int centerX, int centerY, int size) {
        Path2D.Double check = new Path2D.Double();
        check.moveTo(centerX - size * 0.34, centerY + size * 0.02);
        check.lineTo(centerX - size * 0.09, centerY + size * 0.27);
        check.lineTo(centerX + size * 0.36, centerY - size * 0.29);
        g.draw(check);
    }
}
