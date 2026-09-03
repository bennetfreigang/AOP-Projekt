package quirkle.game.gamePlay.ui;

import quirkle.engine.AssetManager;
import quirkle.engine.InputManager;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * One button of the bar along the right edge.
 *
 * @note The brush-stroke artwork for this bar has not been delivered yet. Until it is, a button
 *       without artwork draws a neutral placeholder body at exactly the same size and in the
 *       same place, so handing it its texture later moves nothing on screen - it is the single
 *       call {@link #setArtwork(String)}.
 * @see SideButtonBar for the buttons the gameplay scene uses
 */
public class SideButton extends PanelEntity {

    private static final int PLACEHOLDER_CORNER_RADIUS = 10;
    /** Alpha applied to a disabled button, so it reads as unavailable without moving. */
    private static final float DISABLED_ALPHA = 0.35f;

    /** Texture identifier of the button artwork, or {@code null} while it is missing. */
    private String artwork;

    /** Caption drawn across the button, or {@code null} for a glyph-only button. */
    private final String label;

    /** Pictogram drawn in the button's center, or {@code null} for a labelled button. */
    private final ButtonGlyph glyph;

    /** What pressing the button does, or {@code null} for a placeholder without a function. */
    private Runnable action;

    /** Whether the button reacts to clicks; a disabled button is drawn faded. */
    private boolean enabled = true;

    /**
     * @param label  caption, or {@code null} when {@code glyph} carries the meaning
     * @param glyph  pictogram for the button's center, or {@code null}
     * @param action what a press does, or {@code null} for a placeholder
     */
    public SideButton(String label, ButtonGlyph glyph, Runnable action) {
        this.label = label;
        this.glyph = glyph;
        this.action = action;
    }

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public void setAction(Runnable action) { this.action = action; }

    /** Points the button at its artwork; the one call needed once the missing assets land. */
    public void setArtwork(String artwork) { this.artwork = artwork; }

    /** Places the button with its right edge at {@code rightX} and its center at {@code centerY}. */
    public void place(int rightX, int centerY) {
        setBounds(rightX - UiTheme.SIDE_BUTTON_WIDTH / 2, centerY,
                UiTheme.SIDE_BUTTON_WIDTH, UiTheme.SIDE_BUTTON_HEIGHT, OriginPresets.CENTER);
    }

    /** @return {@code true} only while the button is enabled, unlike the inherited check. */
    @Override
    public boolean isClicked() {
        return enabled && super.isClicked();
    }

    /**
     * Runs the button's action if this frame's click landed on it.
     *
     * @return {@code true} if the click hit the button, whether or not it did anything
     * @note Reports a hit for placeholders too, so a click on one is swallowed rather than
     *       falling through onto the board cell behind it.
     */
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
