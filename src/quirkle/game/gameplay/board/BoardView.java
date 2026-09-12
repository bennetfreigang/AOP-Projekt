package quirkle.game.gameplay.board;

import quirkle.engine.EngineConfig;
import quirkle.engine.Entity;
import quirkle.game.gameplay.tiles.BoardTileEntity;
import quirkle.game.gameplay.tiles.Tile;
import quirkle.game.gameplay.ui.UiTheme;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedHashMap;
import java.util.Map;

public class BoardView extends Entity {

    private final BoardCamera camera;
    private final BoardGrid grid;

    private final Rectangle viewport;

    private final Map<Position, BoardTileEntity> tileEntities = new LinkedHashMap<>();

    public BoardView(BoardCamera camera, Rectangle viewport) {
        this.camera = camera;
        this.viewport = viewport;
        this.renderOrder = UiTheme.LAYER_GRID;

        // Board space covers the whole window; the frame lying over it is what leaves only the
        // opening visible. The viewport stays the frame's opening, since a click outside it is
        // not a move even though the cell under it is drawn.
        this.grid = new BoardGrid(camera, windowBounds());
        this.grid.create();
    }

    private static Rectangle windowBounds() {
        return new Rectangle(0, 0, EngineConfig.WINDOW_WIDTH, EngineConfig.WINDOW_HEIGHT);
    }

    public boolean contains(int screenX, int screenY) {
        return viewport.contains(screenX, screenY);
    }

    public void setCursorVisible(boolean cursorVisible) {
        grid.setCursorVisible(cursorVisible);
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
    }

    @Override
    public void onRender(Graphics2D g) {
        // No clip: the board is drawn across the window and the frame masks it, so the opening's
        // ragged brush edge cuts it instead of a straight rectangle.
        grid.render(g);

        for (BoardTileEntity tileEntity : tileEntities.values()) {
            tileEntity.render(g);
        }
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
