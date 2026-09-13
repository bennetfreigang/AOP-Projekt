package quirkle.game.gameplay.board;

import quirkle.engine.EngineConfig;
import quirkle.engine.Entity;
import quirkle.game.gameplay.tiles.BoardTileEntity;
import quirkle.game.gameplay.tiles.Tile;
import quirkle.game.gameplay.ui.UiTheme;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedHashMap;
import java.util.Map;

public class BoardView extends Entity {

    private final BoardCamera camera;
    private BoardGrid grid;

    private final Rectangle viewport;

    private final Map<Position, BoardTileEntity> tileEntities = new LinkedHashMap<>();

    private BoardTileEntity previewTile;

    public BoardView(BoardCamera camera, Rectangle viewport) {
        this.camera = camera;
        this.viewport = viewport;
        this.renderOrder = UiTheme.LAYER_GRID;
    }

    @Override
    public void onCreate() {
        // Board space covers the whole window; the frame lying over it is what leaves only the
        // opening visible. The viewport stays the frame's opening, since a click outside it is
        // not a move even though the cell under it is drawn.
        grid = new BoardGrid(camera, windowBounds());
        grid.create();
    }

    private static Rectangle windowBounds() {
        return new Rectangle(0, 0, EngineConfig.WINDOW_WIDTH, EngineConfig.WINDOW_HEIGHT);
    }

    public boolean contains(int screenX, int screenY) {
        return viewport.contains(screenX, screenY);
    }

    /**
     * Shows {@code tile} on {@code position} as a translucent ghost, so the player can see where a
     * click would put it. A {@code null} tile clears the preview.
     *
     * @note A real {@link BoardTileEntity} rather than a sprite drawn by hand, so the ghost is
     *       sized and padded exactly like the tile it stands in for.
     */
    public void setPlacementPreview(Tile tile, Position position) {
        if (tile == null || position == null) {
            previewTile = null;
            return;
        }

        // The pointer rests inside one cell for many frames; only a real move rebuilds the ghost.
        if (previewTile != null && previewTile.getTile() == tile
                && previewTile.getPosition().equals(position)) {
            return;
        }

        previewTile = new BoardTileEntity(tile, position);
        previewTile.create();
        layoutTile(previewTile, camera.getTileSize());
    }

    public void sync(Board board) {
        addMissingEntities(board.getPlacedTiles());
        addMissingEntities(board.getPendingTiles());
        removeTakenBackEntities(board);
        layoutTiles(board);
    }

    @Override
    public void onTick(double dt) {
        grid.update(dt);

        for (BoardTileEntity tileEntity : tileEntities.values()) {
            tileEntity.update(dt);
        }

        // Keeps the ghost on its cell while the board is panned and zoomed under it.
        if (previewTile != null) layoutTile(previewTile, camera.getTileSize());
    }

    @Override
    public void onRender(Graphics2D g) {
        // No clip: the board is drawn across the window and the frame masks it, so the opening's
        // ragged brush edge cuts it instead of a straight rectangle.
        grid.render(g);

        for (BoardTileEntity tileEntity : tileEntities.values()) {
            tileEntity.render(g);
        }

        drawPlacementPreview(g);
    }

    private void drawPlacementPreview(Graphics2D g) {
        if (previewTile == null) return;

        Graphics2D gPreview = (Graphics2D) g.create();
        gPreview.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                UiTheme.PLACEMENT_PREVIEW_OPACITY));
        previewTile.render(gPreview);
        gPreview.dispose();
    }

    @Override
    public void onDestroy() {
        grid.destroy();

        for (BoardTileEntity tileEntity : tileEntities.values()) {
            tileEntity.destroy();
        }
        tileEntities.clear();
    }

    private void layoutTiles(Board board) {
        double tileSize = camera.getTileSize();
        Map<Position, Tile> pendingTiles = board.getPendingTiles();

        for (BoardTileEntity tileEntity : tileEntities.values()) {
            layoutTile(tileEntity, tileSize);
            tileEntity.setPending(pendingTiles.containsKey(tileEntity.getPosition()));
        }
    }

    private void layoutTile(BoardTileEntity tileEntity, double tileSize) {
        Point screenPos = camera.boardToScreen(tileEntity.getPosition());
        tileEntity.x = screenPos.x;
        tileEntity.y = screenPos.y;
        tileEntity.setTileSize(tileSize);
    }

    private void addMissingEntities(Map<Position, Tile> tiles) {
        for (Map.Entry<Position, Tile> entry : tiles.entrySet()) {
            Position position = entry.getKey();
            if (tileEntities.containsKey(position)) continue;

            BoardTileEntity tileEntity = new BoardTileEntity(entry.getValue(), position);
            tileEntity.create();
            tileEntities.put(position, tileEntity);
        }
    }

    private void removeTakenBackEntities(Board board) {
        tileEntities.entrySet().removeIf(entry -> {
            Position position = entry.getKey();
            if (board.getPlacedTiles().containsKey(position)) return false;
            if (board.getPendingTiles().containsKey(position)) return false;

            entry.getValue().destroy();
            return true;
        });
    }
}
