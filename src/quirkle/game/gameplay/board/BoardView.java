package quirkle.game.gameplay.board;

import quirkle.engine.Entity;
import quirkle.game.gameplay.tiles.BoardTileEntity;
import quirkle.game.gameplay.tiles.Tile;
import quirkle.game.gameplay.ui.UiTheme;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Everything that lives in board space - the grid and the tiles standing on it - drawn through
 * the opening of {@link quirkle.game.gameplay.ui.BoardFrame}.
 *
 * @note Owns its children rather than registering them as scene entities, the same way
 *       {@code TileRack} and {@code SideButtonBar} do. Here that is not just tidiness: a tile's
 *       texture is drawn by {@link Entity#render(Graphics2D)} itself, not inside the tile's
 *       {@code onRender}, so a clip a tile set on its own would come too late to cut it off. The
 *       clip has to be applied one level up, which is exactly this class.
 * @implNote Keeps the entities in insertion order so the draw order is reproducible. Board tiles
 *           never overlap, so the order does not currently change what is seen.
 */
public class BoardView extends Entity {

    private final BoardCamera camera;
    private final BoardGrid grid;

    /** The window rectangle the board shows through; everything outside it is clipped away. */
    private final Rectangle viewport;

    private final Map<Position, BoardTileEntity> tileEntities = new LinkedHashMap<>();

    public BoardView(BoardCamera camera, Rectangle viewport) {
        this.camera = camera;
        this.viewport = viewport;
        this.renderOrder = UiTheme.LAYER_GRID;

        this.grid = new BoardGrid(camera, viewport);
        this.grid.create();
    }

    /**
     * @return whether ({@code screenX}, {@code screenY}) points at a part of the board that is
     *         actually visible
     * @note The scene gates every board interaction on this. Without it a click on the frame's
     *       border would place a tile on the cell hidden behind it.
     */
    public boolean contains(int screenX, int screenY) {
        return viewport.contains(screenX, screenY);
    }

    /** @see BoardGrid#setCursorVisible(boolean) */
    public void setCursorVisible(boolean cursorVisible) {
        grid.setCursorVisible(cursorVisible);
    }

    /**
     * Brings the drawn tiles in line with {@code board} and moves them under the camera.
     *
     * @note Cheap enough to call every tick; it only allocates when a tile actually appears.
     */
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
    }

    @Override
    public void onRender(Graphics2D g) {
        Graphics2D gBoard = (Graphics2D) g.create();
        gBoard.clip(viewport);

        grid.render(gBoard);
        for (BoardTileEntity tileEntity : tileEntities.values()) {
            tileEntity.render(gBoard);
        }

        gBoard.dispose();
    }

    @Override
    public void onDestroy() {
        grid.destroy();

        for (BoardTileEntity tileEntity : tileEntities.values()) {
            tileEntity.destroy();
        }
        tileEntities.clear();
    }

    /** Puts every tile on the cell the camera currently maps its position to. */
    private void layoutTiles(Board board) {
        double tileSize = camera.getTileSize();
        Map<Position, Tile> pendingTiles = board.getPendingTiles();

        for (BoardTileEntity tileEntity : tileEntities.values()) {
            Point screenPos = camera.boardToScreen(tileEntity.getPosition());
            tileEntity.x = screenPos.x;
            tileEntity.y = screenPos.y;
            tileEntity.setTileSize(tileSize);
            tileEntity.setPending(pendingTiles.containsKey(tileEntity.getPosition()));
        }
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

    /** Drops the entities of tiles that went back to the rack, so no tile is left drawn on an empty cell. */
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
