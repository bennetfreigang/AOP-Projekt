package quirkle.game.gameplay.tiles;

import quirkle.game.gameplay.board.Position;

import java.awt.Color;
import java.awt.Graphics2D;

/** A tile sitting on a fixed board {@link Position}; its screen position follows the board camera. */
public class BoardTileEntity extends TileEntity {
    private static final Color PENDING_COLOR = new Color(255, 193, 7);

    /** Pulse speed of the pending outline in radians per second. */
    private static final double PULSE_FREQUENCY = 5.0;
    private static final double MIN_PULSE_ALPHA = 0.45;

    private final Position position;

    /** Whether this tile is only staged for the current turn and not committed yet. */
    private boolean pending = false;

    /** Seconds the tile has been pending, used to drive the outline pulse. */
    private double pendingTime = 0.0;

    public BoardTileEntity(Tile tile, Position position) {
        super(tile);
        this.position = position;
    }

    public Position getPosition() { return position; }

    public boolean isPending() { return pending; }

    /** @note Resets the pulse when the tile changes state, so a freshly placed tile starts bright. */
    public void setPending(boolean pending) {
        if (this.pending != pending) pendingTime = 0.0;
        this.pending = pending;
    }

    @Override
    public void onTick(double dt) {
        if (pending) pendingTime += dt;
    }

    @Override
    public void onRender(Graphics2D g) {
        if (!pending) return;

        drawOutline(g, getPulsedOutlineColor());
    }

    /** @return the outline color, its alpha pulsing between {@value #MIN_PULSE_ALPHA} and full. */
    private Color getPulsedOutlineColor() {
        double wave = (Math.cos(pendingTime * PULSE_FREQUENCY) + 1.0) / 2.0;   // 1.0 -> 0.0 -> 1.0
        double alpha = MIN_PULSE_ALPHA + wave * (1.0 - MIN_PULSE_ALPHA);

        return new Color(PENDING_COLOR.getRed(), PENDING_COLOR.getGreen(), PENDING_COLOR.getBlue(),
                (int) Math.round(alpha * 255));
    }
}