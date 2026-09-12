package quirkle.game.gameplay.ui;

import quirkle.engine.Entity;
import quirkle.engine.extensions.NineSlice;

import java.awt.Graphics2D;

public abstract class PanelEntity extends Entity {

    private String frameIdentifier;

    private int frameBorder = UiTheme.PANEL_BORDER;

    protected PanelEntity() {
        this.renderOrder = UiTheme.LAYER_HUD;
    }

    protected void setBounds(int x, int y, int width, int height, OriginPresets origin) {
        this.origin = origin;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    protected void setFrame(String frameIdentifier) {
        this.frameIdentifier = frameIdentifier;
    }

    protected void setFrame(String frameIdentifier, int frameBorder) {
        this.frameIdentifier = frameIdentifier;
        this.frameBorder = frameBorder;
    }

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
