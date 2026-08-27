package quirkle.game.gamePlay.hand;

import quirkle.game.gamePlay.tiles.Tile;
import quirkle.game.gamePlay.tiles.TileEntity;

import java.awt.*;

public class HandTileEntity extends TileEntity {
    private static final Color SELECTION_COLOR = new Color(255, 193, 7);
    private static final Color HOVER_COLOR = new Color(255, 255, 255, 140);
    private static final Color REJECTION_COLOR = new Color(229, 57, 53);
    private static final float OUTLINE_WIDTH = 3f;

    /** How long the "that move is not allowed" feedback runs, in seconds. */
    private static final double REJECTION_DURATION = 0.35;
    /** Shake speed in radians per second; roughly three shakes over {@link #REJECTION_DURATION}. */
    private static final double SHAKE_FREQUENCY = 55.0;
    /** Peak horizontal shake offset in pixels, reached at the start of the animation. */
    private static final double SHAKE_AMPLITUDE = 9.0;

    private boolean selected = false;

    /** Seconds left of the rejection animation; {@code 0} while the tile is at rest. */
    private double rejectionTimer = 0.0;

    public HandTileEntity(Tile tile) {
        super(tile);
    }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    public void reject() {
        rejectionTimer = REJECTION_DURATION;
    }

    public boolean isRejecting() {
        return rejectionTimer > 0.0;
    }

    @Override
    public void onTick(double dt) {
        if (rejectionTimer > 0.0) {
            rejectionTimer = Math.max(0.0, rejectionTimer - dt);
        }
    }

    @Override
    public void render(Graphics2D g) {
        int restingX = x;
        x += getShakeOffset();
        super.render(g);
        x = restingX;
    }

    @Override
    public void onRender(Graphics2D g) {
        Color outlineColor = getOutlineColor();
        if (outlineColor == null) return;

        Graphics2D gOutline = (Graphics2D) g.create();
        gOutline.setColor(outlineColor);
        gOutline.setStroke(new BasicStroke(OUTLINE_WIDTH));

        int scaleWidth = (int) getScaledWidth();
        int scaledHeight = (int) getScaledHeight();
        int drawX = (int) (x - origin.x * scaleWidth);
        int drawY = (int) (y - origin.y * scaleWidth);

        gOutline.drawRect(drawX, drawY, scaleWidth, scaledHeight);
        gOutline.dispose();
    }

    private Color getOutlineColor() {
        if (isRejecting()) return REJECTION_COLOR;
        if (selected) return SELECTION_COLOR;
        if (isHovered()) return HOVER_COLOR;
        return null;
    }

    private int getShakeOffset() {
        if (!isRejecting()) return 0;

        double decay = rejectionTimer / REJECTION_DURATION;
        double elapsed = REJECTION_DURATION - rejectionTimer;
        return (int) Math.round(Math.sin(elapsed * SHAKE_FREQUENCY) * SHAKE_AMPLITUDE * decay);
    }
}
