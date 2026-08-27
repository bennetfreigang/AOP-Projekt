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
import java.util.HashMap;
import java.util.Map;

public class GamePlayScene extends Scene {

    // Data
    private final Board board;
    private final BoardCamera camera;
    private final Map<Position, TileEntity> tileEntities = new HashMap<>();

    private Tile nextTile;

    // Zoom
    private static final double ZOOM_FACTOR = 1.0;

    // Drag
    private int lastMouseX;
    private int lastMouseY;
    private boolean isDragging =  false;

    public GamePlayScene() {
        this.board = new Board();
        this.camera = new BoardCamera(getCenterX(), getCenterY(), 32);
        this.nextTile = new Tile(TileColor.RED, TileSymbol.CIRCLE); // Platzhalter bis Hand/TileBag angebunden ist
    }

    @Override
    public void onTick(double dt) {
        handleZoomInput();
        handleDragInput();
        handleClickInput();
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

    private void handleClickInput() {
        if (nextTile == null || !InputManager.isMouseClicked()) return;

        Position position = camera.screenToBoard(InputManager.getMouseX(), InputManager.getMouseY());

        try {
            board.placeTile(position, nextTile);
            board.commitPendingTiles();
        } catch (IllegalStateException e) {
            // Feld belegt oder Platzierung verstößt gegen die Qwirkle-Regeln -> Klick wird ignoriert
            System.out.println(e.getMessage());
        }
    }

    private void handleZoomInput() {
        double scroll = InputManager.getScrollDelta();
        if (scroll == 0) return;

        camera.zoomAt(-scroll * ZOOM_FACTOR, InputManager.getMouseX(), InputManager.getMouseY());
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
