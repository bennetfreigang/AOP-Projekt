package quirkle.game.gamePlay.board;

import java.awt.*;

public class BoardCamera {
    double centerX;
    double centerY;

    double offsetX;
    double offsetY;

    double baseTileSize;
    private double zoom = 2.0;

    private static final double MIN_ZOOM = 1.0;
    private static final double MAX_ZOOM = 4.0;

    public BoardCamera(double centerX, double centerY, double baseTileSize) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.baseTileSize = baseTileSize;
    }

    public double getZoom() {
        return zoom;
    }

    /** Zooms by {@code delta}, keeping the board point currently under ({@code screenX}, {@code screenY}) fixed on screen. */
    public void zoomAt(double delta, double screenX, double screenY) {
        double oldTileSize = getTileSize();
        double worldX = (screenX - centerX - offsetX) / oldTileSize;
        double worldY = (screenY - centerY - offsetY) / oldTileSize;

        zoomBy(delta);

        double newTileSize = getTileSize();
        offsetX = screenX - centerX - worldX * newTileSize;
        offsetY = screenY - centerY - worldY * newTileSize;
    }

    private void zoomBy(double delta) {
        zoom = Math.clamp(zoom + delta, MIN_ZOOM, MAX_ZOOM);
    }

    public void pan(double dx, double dy) {
        offsetX += dx;
        offsetY += dy;
    }

    public double getTileSize() {
        return baseTileSize * zoom;
    }

    public Point boardToScreen(Position position) {
        double tileSize = getTileSize();
        int screenX = (int)(centerX + offsetX + position.x() * tileSize);
        int screenY = (int)(centerY + offsetY + position.y() * tileSize);
        return new Point(screenX, screenY);
    }

    public Position screenToBoard(int screenX, int screenY) {
        double tileSize = getTileSize();
        int boardX = (int) Math.round((screenX - centerX - offsetX) /  tileSize);
        int boardY = (int) Math.round((screenY - centerY - offsetY) /  tileSize);
        return new Position(boardX, boardY);
    }
}
