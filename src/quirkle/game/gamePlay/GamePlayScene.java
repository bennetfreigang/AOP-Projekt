package quirkle.game.gamePlay;

import quirkle.engine.InputManager;
import quirkle.engine.Scene;
import quirkle.game.gamePlay.board.Board;
import quirkle.game.gamePlay.board.BoardCamera;
import quirkle.game.gamePlay.board.BoardGrid;
import quirkle.game.gamePlay.board.Position;
import quirkle.game.gamePlay.hand.TileRack;
import quirkle.game.gamePlay.player.Player;
import quirkle.game.gamePlay.tiles.*;
import quirkle.game.gamePlay.ui.PlayerCardColumn;
import quirkle.game.gamePlay.ui.SideButton;
import quirkle.game.gamePlay.ui.SideButtonBar;
import quirkle.game.gamePlay.ui.TileBagCounter;
import quirkle.game.gamePlay.ui.UiTheme;

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

    // UI
    private final BoardGrid boardGrid;
    private final PlayerCardColumn playerCards;
    private final TileBagCounter tileBagCounter;
    private final SideButtonBar sideButtons;
    private final SideButton endTurnButton;

    public GamePlayScene() {
        this.game = new Game(new Board(), new TileBag(), List.of(new Player("Player 1"), new Player("Player 2")));
        this.camera = new BoardCamera(getCenterX(), getCenterY(), UiTheme.BOARD_BASE_CELL_SIZE);

        this.boardGrid = new BoardGrid(camera, getWidth(), getHeight());
        this.tileRack = new TileRack(getCenterX(), getHeight() - UiTheme.RACK_MARGIN_BOTTOM);
        this.playerCards = new PlayerCardColumn(game);
        this.tileBagCounter = new TileBagCounter(game.getTileBag());
        this.sideButtons = new SideButtonBar(getWidth() - UiTheme.SIDE_BUTTON_MARGIN_RIGHT, this::endTurn);
        this.endTurnButton = sideButtons.getEndTurnButton();

        addEntities(boardGrid, tileRack, playerCards, tileBagCounter, sideButtons);
    }

    /**
     * @note Paints the backdrop the HUD assets were drawn for; the brush strokes are white and
     *       the tiles are unfilled outlines, so both only read on a dark ground.
     */
    @Override
    public void onRender(Graphics2D g) {
        g.setColor(UiTheme.BACKGROUND);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void onTick(double dt) {
        handleZoomInput();
        handleDragInput();
        handleClickInput();

        endTurnButton.setEnabled(game.hasPendingTiles());
        tileRack.showPlayer(game.getCurrentPlayer());

        // No placement cursor while the mouse is over the HUD; a click there never reaches a cell.
        boardGrid.setCursorVisible(!tileRack.isHovered() && !sideButtons.isHovered());

        syncTiles();

        double tileSize = camera.getTileSize();
        Map<Position, Tile> pendingTiles = game.getBoard().getPendingTiles();
        for (BoardTileEntity tileEntity : tileEntities.values()) {
            Point screenPos = camera.boardToScreen(tileEntity.getPosition());
            tileEntity.x = screenPos.x;
            tileEntity.y = screenPos.y;
            tileEntity.setTileSize(tileSize);
            tileEntity.setPending(pendingTiles.containsKey(tileEntity.getPosition()));
        }
    }

    private void syncTiles() {
        addMissingEntities(game.getBoard().getPlacedTiles());
        addMissingEntities(game.getBoard().getPendingTiles());
        removeTakenBackEntities();
    }

    /** Drops the entities of tiles that went back to the rack, so no tile is left drawn on an empty cell. */
    private void removeTakenBackEntities() {
        Board board = game.getBoard();

        tileEntities.entrySet().removeIf(entry -> {
            Position position = entry.getKey();
            if (board.getPlacedTiles().containsKey(position)) return false;
            if (board.getPendingTiles().containsKey(position)) return false;

            removeEntities(entry.getValue());
            return true;
        });
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
        if (sideButtons.handleInput()) return; // likewise for the button bar on the right

        if (!InputManager.isMouseClicked()) return;

        Position position = camera.screenToBoard(InputManager.getMouseX(), InputManager.getMouseY());

        // A click on a tile staged this turn takes it back, whether or not a rack tile is selected;
        // placing onto an occupied cell would be rejected anyway.
        if (game.getBoard().getPendingTiles().containsKey(position)) {
            game.takeBackTile(position);
            return;
        }

        Tile selectedTile = tileRack.getSelectedTile();
        if (selectedTile == null) return;

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
