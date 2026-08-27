package quirkle.game.gamePlay.ui;

import quirkle.engine.Entity;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * The "Zug beenden" button in the bottom right corner.
 *
 * @note Lives in screen space and is drawn from scratch rather than from a sprite, since the
 *       asset folder holds no button texture.
 */
public class EndTurnButton extends Entity {
    /** Draw above every board tile, which uses the default renderOrder of 0. */
    public static final int RENDER_ORDER = 100;

    private static final int WIDTH = 220;
    private static final int HEIGHT = 64;
    private static final int CORNER_RADIUS = 14;
    private static final float LABEL_FONT_SIZE = 26f;
    private static final String LABEL_FONT = "DEBUG_Poly-Regular";
    private static final String LABEL = "Zug beenden";

    private static final Color IDLE_COLOR = new Color(46, 125, 50);
    private static final Color HOVER_COLOR = new Color(67, 160, 71);
    private static final Color DISABLED_COLOR = new Color(38, 42, 52, 90);
    private static final Color BORDER_COLOR = new Color(255, 255, 255, 60);
    private static final Color LABEL_COLOR = Color.WHITE;
    private static final Color DISABLED_LABEL_COLOR = new Color(255, 255, 255, 110);

    /** Whether the button can be pressed; a turn cannot be ended before a tile was placed. */
    private boolean enabled = false;

    /**
     * @param rightX  screen x of the button's right edge
     * @param bottomY screen y of the button's lower edge
     */
    public EndTurnButton(int rightX, int bottomY) {
        this.origin = OriginPresets.BOTTOM_RIGHT;
        this.renderOrder = RENDER_ORDER;
        this.x = rightX;
        this.y = bottomY;
        this.width = WIDTH;
        this.height = HEIGHT;
    }

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    @Override
    public void onRender(Graphics2D g) {
        Graphics2D gButton = (Graphics2D) g.create();

        gButton.setColor(getBackgroundColor());
        gButton.fillRoundRect(getLeft(), getTop(), WIDTH, HEIGHT, CORNER_RADIUS, CORNER_RADIUS);

        gButton.setColor(BORDER_COLOR);
        gButton.setStroke(new BasicStroke(2f));
        gButton.drawRoundRect(getLeft(), getTop(), WIDTH, HEIGHT, CORNER_RADIUS, CORNER_RADIUS);

        gButton.dispose();

        Color labelColor = enabled ? LABEL_COLOR : DISABLED_LABEL_COLOR;
        drawText(LABEL, LABEL_FONT_SIZE, labelColor, LABEL_FONT,
                getLeft() + WIDTH / 2, getTop() + HEIGHT / 2, 0.0, OriginPresets.CENTER, g);
    }

    private Color getBackgroundColor() {
        if (!enabled) return DISABLED_COLOR;
        return isHovered() ? HOVER_COLOR : IDLE_COLOR;
    }

    private int getLeft() {
        return (int) (x - origin.x * width);
    }

    private int getTop() {
        return (int) (y - origin.y * height);
    }
}