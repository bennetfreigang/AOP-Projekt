package quirkle.game.gamePlay;

import quirkle.engine.InputManager;
import quirkle.engine.Scene;
import quirkle.game.debug.DebugConsole;
import quirkle.game.debug.DebugMode;
import quirkle.game.gamePlay.board.Board;
import quirkle.game.gamePlay.board.BoardCamera;
import quirkle.game.gamePlay.board.BoardView;
import quirkle.game.gamePlay.board.Position;
import quirkle.game.gamePlay.hand.TileRack;
import quirkle.game.gamePlay.player.Player;
import quirkle.game.gamePlay.tiles.*;
import quirkle.game.gamePlay.ui.BoardFrame;
import quirkle.game.gamePlay.ui.Handover;
import quirkle.game.gamePlay.ui.PlayerCardColumn;
import quirkle.game.gamePlay.ui.SideButton;
import quirkle.game.gamePlay.ui.SideButtonBar;
import quirkle.game.gamePlay.ui.TileBagCounter;
import quirkle.game.gamePlay.ui.TurnIndicator;
import quirkle.game.gamePlay.ui.UiTheme;

import java.awt.*;
import java.util.List;

public class GamePlayScene extends Scene {

    // Data
    private final Game game;
    private final BoardCamera camera;
    private final TileRack tileRack;

    // Zoom
    private static final double ZOOM_FACTOR = 1.0;

    // Drag
    private int lastMouseX;
    private int lastMouseY;
    private boolean isDragging = false;
    /** Set while a right-button press that began off the board is still being held. */
    private boolean isDragBlocked = false;

    // Handover
    /** The clock every HUD element animates a turn change on. */
    private final Handover handover = new Handover();
    /** The player the HUD is presenting, which lags the game while a handover plays out. */
    private Player presentedPlayer;

    // UI
    private final BoardView boardView;
    private final BoardFrame boardFrame;
    private final TurnIndicator turnIndicator;
    private final PlayerCardColumn playerCards;
    private final TileBagCounter tileBagCounter;
    private final SideButtonBar sideButtons;
    private final SideButton endTurnButton;

    public GamePlayScene() {
        // The frame's opening, not the window, is what the board has to fit into.
        Rectangle viewport = UiTheme.boardViewport();

        this.game = new Game(new Board(), new TileBag(), List.of(new Player("Player 1"), new Player("Player 2")));
        this.camera = new BoardCamera(viewport.getCenterX(), viewport.getCenterY(), UiTheme.BOARD_BASE_CELL_SIZE);

        this.boardView = new BoardView(camera, viewport);
        this.boardFrame = new BoardFrame();
        this.tileRack = new TileRack((int) viewport.getCenterX(), bandCenterY(viewport), handover);
        this.turnIndicator = new TurnIndicator(game, (int) viewport.getCenterX(), handover);
        this.playerCards = new PlayerCardColumn(game, handover);
        this.tileBagCounter = new TileBagCounter(game.getTileBag());
        this.sideButtons = new SideButtonBar(getWidth() - UiTheme.SIDE_BUTTON_MARGIN_RIGHT, this::endTurn);
        this.endTurnButton = sideButtons.getEndTurnButton();

        addEntities(boardView, boardFrame, turnIndicator, tileRack, playerCards, tileBagCounter, sideButtons);

        DebugMode.attach(this.game);
    }

    @Override
    public void onDestroy() {
        DebugMode.detach();
    }

    /**
     * @return the vertical center of the band between the frame's opening and the bottom of the
     *         window, which is where the row of rack slots goes
     * @note Now that the rack is a bare row rather than a 215px panel it fits into the band whole,
     *       so it no longer has to reach up onto the board to find room.
     */
    private int bandCenterY(Rectangle viewport) {
        int bandTop = viewport.y + viewport.height;
        return bandTop + (getHeight() - bandTop) / 2;
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
        // Ahead of everything else: the HUD elements read this clock during their own ticks, and
        // they have to see the same frame of it.
        handover.advance(dt);
        startHandoverIfTurnPassed();

        handleZoomInput();
        handleDragInput();
        handleClickInput();

        endTurnButton.setEnabled(game.hasPendingTiles());
        tileRack.showPlayer(game.getCurrentPlayer());

        // No placement cursor unless a click at the pointer would actually land on a cell.
        boardView.setCursorVisible(isPointerOnBoard());

        boardView.sync(game.getBoard());
    }

    /**
     * Kicks off the HUD's handover animation once the turn has moved to another player.
     *
     * @note Detected here rather than in each element: one place decides that a handover is due,
     *       and the elements only decide what to do about it. It also means an element cannot miss
     *       the change while it is busy playing the previous one out.
     */
    private void startHandoverIfTurnPassed() {
        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer == presentedPlayer) return;
        if (handover.isRunning()) return;

        boolean isFirstFrame = presentedPlayer == null;
        presentedPlayer = currentPlayer;

        // Nothing to hand over from at the start of the game; the HUD is simply there.
        if (!isFirstFrame) handover.start();
    }

    /**
     * @return whether the mouse points at a board cell that is both visible through the frame and
     *         not covered by a HUD panel
     * @note The HUD checks are kept even though every panel now sits outside the opening: they
     *       cost nothing and stop a later panel that does reach over the board from silently
     *       placing tiles through itself.
     */
    private boolean isPointerOnBoard() {
        if (!boardView.contains(InputManager.getMouseX(), InputManager.getMouseY())) return false;

        return !tileRack.isHovered() && !sideButtons.isHovered();
    }

    private void handleClickInput() {
        if (tileRack.handleInput()) return; // the rack got the click, don't also place a tile
        if (sideButtons.handleInput()) return; // likewise for the button bar on the right

        if (!InputManager.isMouseClicked()) return;
        // A click on the frame's border points at a cell the player cannot see; it is not a move.
        if (!boardView.contains(InputManager.getMouseX(), InputManager.getMouseY())) return;

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

    /** @note Scrolling over the frame or the HUD leaves the board alone. */
    private void handleZoomInput() {
        double scroll = InputManager.getScrollDelta();
        if (scroll == 0) return;
        if (!boardView.contains(InputManager.getMouseX(), InputManager.getMouseY())) return;

        camera.zoomAt(-scroll * ZOOM_FACTOR, InputManager.getMouseX(), InputManager.getMouseY());
    }

    /**
     * Pans the board while the right button is held.
     *
     * @note A pan has to start inside the frame's opening, but may continue outside it; letting go
     *       of a drag the moment the pointer crosses the frame's edge would make the board stick.
     *       A press that began off the board stays inert until the button is released, so dragging
     *       the frame itself never nudges the board.
     */
    private void handleDragInput() {
        if (!InputManager.isRightMousePressed()) {
            isDragging = false;
            isDragBlocked = false;
            return;
        }

        if (isDragBlocked) return;

        if (isDragging) {
            camera.pan(InputManager.getMouseX() - lastMouseX, InputManager.getMouseY() - lastMouseY);
        } else if (!boardView.contains(InputManager.getMouseX(), InputManager.getMouseY())) {
            isDragBlocked = true;
            return;
        }

        lastMouseX = InputManager.getMouseX();
        lastMouseY = InputManager.getMouseY();
        isDragging = true;
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
