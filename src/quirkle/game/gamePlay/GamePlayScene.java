package quirkle.game.gamePlay;

import quirkle.engine.InputManager;
import quirkle.engine.Scene;
import quirkle.game.gamePlay.board.Board;
import quirkle.game.gamePlay.board.BoardCamera;
import quirkle.game.gamePlay.board.Position;
import quirkle.game.gamePlay.hand.TileRack;
import quirkle.game.gamePlay.player.Player;
import quirkle.game.gamePlay.tiles.*;
import quirkle.game.gamePlay.ui.EndTurnButton;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePlayScene extends Scene {

    // Data
    private final Game game;
    private final BoardCamera camera;
    private final TileRack tileRack;
    private final Map<Position, BoardTileEntity> tileEntities = new HashMap<>();

    // Zoom
    private static final double ZOOM_FACTOR = 1.0;

    // Drag
    private int lastMouseX;
    private int lastMouseY;
    private boolean isDragging =  false;

    // HUD
    private static final int RACK_MARGIN_BOTTOM = 32;
    private static final int BUTTON_MARGIN = 32;

    // UI
    private final EndTurnButton endTurnButton;

    public GamePlayScene() {
        this.game = new Game(new Board(), new TileBag(), List.of(new Player("Player 1"), new Player("Player 2")));
        this.camera = new BoardCamera(getCenterX(), getCenterY(), 32);
        this.tileRack = new TileRack(getCenterX(), getHeight() - RACK_MARGIN_BOTTOM);
        this.endTurnButton = new EndTurnButton(getWidth() - BUTTON_MARGIN, getHeight() - BUTTON_MARGIN);

        addEntities(tileRack, endTurnButton);
    }

    @Override
    public void onTick(double dt) {
        handleZoomInput();
        handleDragInput();
        handleClickInput();

        endTurnButton.setEnabled(game.hasPendingTiles());
        tileRack.showPlayer(game.getCurrentPlayer());
        syncTiles();

        double tileSize = camera.getTileSize();
        for (BoardTileEntity tileEntity : tileEntities.values()) {
            Point screenPos = camera.boardToScreen(tileEntity.getPosition());
            tileEntity.x = screenPos.x;
            tileEntity.y = screenPos.y;
            tileEntity.scale = tileSize / tileEntity.width;
        }
    }

    private void syncTiles() {
        addMissingEntities(game.getBoard().getPlacedTiles());
        addMissingEntities(game.getBoard().getPendingTiles());
    }

    private void addMissingEntities(Map<Position, Tile> tiles) {
        for (Map.Entry<Position, Tile> entry : tiles.entrySet()) {
            Position position = entry.getKey();
            if (tileEntities.containsKey(position)) continue;

            BoardTileEntity tileEntity = new BoardTileEntity(entry.getValue(), position);
            tileEntities.put(position, tileEntity);
            addEntities(tileEntity);
        }
    }

    private void handleClickInput() {
        if (tileRack.handleInput()) return; // the rack got the click, don't also place a tile

        if (endTurnButton.isClicked()) {
            if (endTurnButton.isClicked()) endTurn();
            return;
        }

        Tile selectedTile = tileRack.getSelectedTile();
        if (selectedTile == null || !InputManager.isMouseClicked()) return;

        Position position = camera.screenToBoard(InputManager.getMouseX(), InputManager.getMouseY());

        try {
            game.placeTile(position, selectedTile);
            tileRack.clearSelection();
        } catch (IllegalStateException e) {
            // Feld belegt oder Platzierung verstößt gegen die Qwirkle-Regeln -> Klick wird ignoriert
            tileRack.rejectSelection();
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

    private void endTurn() {
        try {
            Player player = game.getCurrentPlayer();
            int points = game.endTurn();
            tileRack.clearSelection();
            System.out.println(player.getName() + " erhält " + points + " Punkte.");
        } catch (IllegalStateException e) {
            // Zug ist noch nicht abschließbar (z.B. kein Stein gelegt) -> Eingabe wird ignoriert
            System.out.println(e.getMessage());
        }
    }
}
