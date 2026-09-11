package quirkle.game.gamePlay.board;

import java.awt.*;

/**
 * Maps board coordinates to screen pixels, with panning and zooming.
 */
public class BoardCamera {
    double centerX;
    double centerY;

    double offsetX;
    double offsetY;

    double baseTileSize;
    private double zoom = 2.0;

    private static final double MIN_ZOOM = 1.0;
    private static final double MAX_ZOOM = 4.0;

    /**
     * @param centerX      screen x board origin {@code (0, 0)} sits on before panning
     * @param centerY      screen y board origin {@code (0, 0)} sits on before panning
     * @param baseTileSize cell size in pixels at zoom {@code 1}
     */
    public BoardCamera(double centerX, double centerY, double baseTileSize) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.baseTileSize = baseTileSize;
    }

    public double getZoom() {
        return zoom;
    }

    /** Adds {@code delta} to the zoom, clamped to {@value #MIN_ZOOM}..{@value #MAX_ZOOM}. */
    public void zoomBy(double delta) {
        zoom = Math.clamp(zoom + delta, MIN_ZOOM, MAX_ZOOM);
    }

    /** Shifts the board by {@code dx}/{@code dy} screen pixels */
    public void pan(double dx, double dy) {
        offsetX += dx;
        offsetY += dy;
    }

    /** @return the on-screen edge length of one cell at the current zoom */
    public double getTileSize() {
        return baseTileSize * zoom;
    }

    /** @return the screen point the center of cell {@code position} sits on */
    public Point boardToScreen(Position position) {
        double tileSize = getTileSize();
        int screenX = (int)(centerX + offsetX + position.x() * tileSize);
        int screenY = (int)(centerY + offsetY + position.y() * tileSize);
        return new Point(screenX, screenY);
    }

    /** @return the cell of the screen coordinates */
    public Position screenToBoard(int screenX, int screenY) {
        double tileSize = getTileSize();
        int boardX = (int) Math.round((screenX - centerX - offsetX) /  tileSize);
        int boardY = (int) Math.round((screenY - centerY - offsetY) /  tileSize);
        return new Position(boardX, boardY);
    }
}
