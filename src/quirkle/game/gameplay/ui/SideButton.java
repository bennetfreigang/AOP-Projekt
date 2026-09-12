package quirkle.game.gameplay.ui;

import quirkle.engine.AssetManager;
import quirkle.engine.InputManager;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class SideButton extends PanelEntity {

    private static final int PLACEHOLDER_CORNER_RADIUS = 10;
    private static final float DISABLED_ALPHA = 0.35f;

    private String artwork;

    private final String label;

    private final ButtonGlyph glyph;

    private Runnable action;

    private boolean enabled = true;

    public SideButton(String label, ButtonGlyph glyph, Runnable action) {
        this.label = label;
        this.glyph = glyph;
        this.action = action;
    }

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public void setAction(Runnable action) { this.action = action; }

    public void setArtwork(String artwork) { this.artwork = artwork; }

    public void place(int rightX, int centerY) {
        setBounds(rightX - UiTheme.SIDE_BUTTON_WIDTH / 2, centerY,
                UiTheme.SIDE_BUTTON_WIDTH, UiTheme.SIDE_BUTTON_HEIGHT, OriginPresets.CENTER);
    }

    @Override
    public boolean isClicked() {
        return enabled && super.isClicked();
    }

    public boolean handleInput() {
        if (!InputManager.isMouseClicked() || !isHovered()) return false;

        if (enabled && action != null) action.run();
        return true;
    }

    @Override
    public void onRender(Graphics2D g) {
        Graphics2D gButton = (Graphics2D) g.create();
        if (!enabled) {
            gButton.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, DISABLED_ALPHA));
        }

        drawBody(gButton);
        drawCaption(gButton);

        gButton.dispose();
    }

    private void drawBody(Graphics2D g) {
        if (artwork != null) {
            g.drawImage(AssetManager.getTexture(artwork), getLeft(), getTop(), getWidth(), getHeight(), null);
            return;
        }

        g.setColor(UiTheme.PLACEHOLDER_FILL);
        g.fillRoundRect(getLeft(), getTop(), getWidth(), getHeight(),
                PLACEHOLDER_CORNER_RADIUS, PLACEHOLDER_CORNER_RADIUS);

        g.setColor(UiTheme.PLACEHOLDER_BORDER);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(getLeft(), getTop(), getWidth(), getHeight(),
                PLACEHOLDER_CORNER_RADIUS, PLACEHOLDER_CORNER_RADIUS);
    }

    private void drawCaption(Graphics2D g) {
        boolean highlighted = enabled && isHovered();
        Color color = highlighted ? UiTheme.GOLD : UiTheme.TEXT;

        int centerX = getLeft() + getWidth() / 2;
        int centerY = getTop() + getHeight() / 2;

        if (glyph != null) {
            glyph.draw(g, centerX, centerY, UiTheme.GLYPH_SIZE, color);
        } else if (label != null) {
            drawText(label, UiTheme.FONT_SIZE_BUTTON, color, UiTheme.FONT,
                    centerX, centerY, 0.0, OriginPresets.CENTER, g);
        }
    }
}
