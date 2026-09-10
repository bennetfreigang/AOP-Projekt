package quirkle.game.gamePlay.ui;

import quirkle.engine.Entity;
import quirkle.engine.NineSlice;

import java.awt.Graphics2D;

/**
 * Base for every HUD element that is a rectangle with a brush-stroke frame around it.
 *
 * @note Lives in screen space: it deliberately ignores the board camera, so panels stay put
 *       while the board is panned and zoomed.
 * @implNote Collects the {@code getLeft()} / {@code getTop()} pair that every HUD class used to
 *           carry its own copy of, and draws the frame through {@link NineSlice} so the stroke
 *           keeps an even weight at any panel size.
 */
public abstract class PanelEntity extends Entity {

    /** Frame texture identifier, or {@code null} for a panel that draws no frame. */
    private String frameIdentifier;

    /** On-screen thickness the frame's brush stroke is drawn at. */
    private int frameBorder = UiTheme.PANEL_BORDER;

    protected PanelEntity() {
        this.renderOrder = UiTheme.LAYER_HUD;
    }

    /**
     * Places the panel and sizes it.
     *
     * @param x      screen x of the point named by {@link #origin}
     * @param y      screen y of the point named by {@link #origin}
     * @param origin which corner or edge of the panel ({@code x}, {@code y}) refers to
     */
    protected void setBounds(int x, int y, int width, int height, OriginPresets origin) {
        this.origin = origin;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /** Sets the frame drawn by {@link #drawFrame}; pass {@code null} to draw none. */
    protected void setFrame(String frameIdentifier) {
        this.frameIdentifier = frameIdentifier;
    }

    /** @param frameBorder on-screen thickness of the frame's brush stroke */
    protected void setFrame(String frameIdentifier, int frameBorder) {
        this.frameIdentifier = frameIdentifier;
        this.frameBorder = frameBorder;
    }

    /** Draws the panel's frame across its full bounds. */
    protected void drawFrame(Graphics2D g) {
        if (frameIdentifier == null) return;
        NineSlice.draw(g, frameIdentifier, getLeft(), getTop(), getWidth(), getHeight(),
                UiTheme.CONTAINER_SOURCE_INSET, frameBorder);
    }

    public int getLeft() {
        return (int) (x - origin.x * getScaledWidth());
    }

    public int getTop() {
        return (int) (y - origin.y * getScaledHeight());
    }

    public int getRight() {
        return getLeft() + getWidth();
    }

    public int getBottom() {
        return getTop() + getHeight();
    }

    public int getWidth() {
        return (int) getScaledWidth();
    }

    public int getHeight() {
        return (int) getScaledHeight();
    }
}
