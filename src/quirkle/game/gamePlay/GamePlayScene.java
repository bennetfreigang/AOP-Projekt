package quirkle.game.gamePlay;

import quirkle.engine.InputManager;
import quirkle.engine.Scene;
import quirkle.game.gamePlay.board.Board;
import quirkle.game.gamePlay.board.BoardCamera;
import quirkle.game.gamePlay.board.Position;
import quirkle.game.gamePlay.tiles.Tile;
import quirkle.game.gamePlay.tiles.TileColor;
import quirkle.game.gamePlay.tiles.TileEntity;
import quirkle.game.gamePlay.tiles.TileSymbol;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class GamePlayScene extends Scene {

    // Data
    private final Board board;
    private final BoardCamera camera;
    private final Map<Position, TileEntity> tileEntities = new HashMap<>();

    // Zoom
    private static final double ZOOM_FACTOR = 1.0;

    // Drag
    private int lastMouseX;
    private int lastMouseY;
    private boolean isDragging =  false;

    public GamePlayScene() {
        this.board = new Board();
        this.camera = new BoardCamera(getCenterX(), getCenterY(), 32);

        board.placeTile(new Position(0, 0), new Tile(TileColor.RED, TileSymbol.CIRCLE));
        board.placeTile(new Position(1, 0), new Tile(TileColor.RED, TileSymbol.SQUARE));
        board.placeTile(new Position(2, 0), new Tile(TileColor.RED, TileSymbol.DIAMOND));
    }

    @Override
    public void onTick(double dt) {
        handleZoomInput(dt);
        handleDragInput();
        syncTiles();

        double tileSize = camera.getTileSize();
        for (TileEntity tileEntity : tileEntities.values()) {
            Point screenPos = camera.boardToScreen(tileEntity.getPosition());
            tileEntity.x = screenPos.x;
            tileEntity.y = screenPos.y;
            tileEntity.scale = tileSize / tileEntity.width;
        }
    }

    private void syncTiles() {
        addMissingEntities(board.getPlacedTiles());
        addMissingEntities(board.getPendingTiles());
    }

    private void addMissingEntities(Map<Position, Tile> tiles) {
        for (Map.Entry<Position, Tile> entry : tiles.entrySet()) {
            Position position = entry.getKey();
            if (tileEntities.containsKey(position)) continue;

            TileEntity tileEntity = new TileEntity(entry.getValue(), position);
            tileEntities.put(position, tileEntity);
            addEntities(tileEntity);
        }
    }

    private void  handleZoomInput(double dt) {
        if (InputManager.isKeyPressed(KeyEvent.VK_UP)) {
            camera.zoomBy(ZOOM_FACTOR);
        }
        if (InputManager.isKeyPressed(KeyEvent.VK_DOWN)) {
            camera.zoomBy(-ZOOM_FACTOR);
        }
    }

    private void handleDragInput() {
        if (InputManager.isRightMousePressed()) {
            if (isDragging) {
                camera.pan(InputManager.getMouseX() - lastMouseX, InputManager.getMouseY() - lastMouseY);
            }
            lastMouseX = InputManager.getMouseX();
            lastMouseY = InputManager.getMouseY();
            isDragging = true;
        } else {
            isDragging = false;
        }
    }
}
