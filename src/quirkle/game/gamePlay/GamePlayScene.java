package quirkle.game.gamePlay;

import quirkle.engine.InputManager;
import quirkle.engine.Scene;
import quirkle.game.debug.DebugMode;
import quirkle.game.debug.scenes.DebugModeScene;
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
    private boolean isDragBlocked = false;

    // Handover
    private final Handover handover = new Handover();
    private Player presentedPlayer;

    // UI
    private final BoardView boardView;
    private final BoardFrame boardFrame;
    private final TurnIndicator turnIndicator;
    private final PlayerCardColumn playerCards;
    private final TileBagCounter tileBagCounter;
    private final SideButtonBar sideButtons;
    private final SideButton endTurnButton;
    private DebugModeScene debugModeScene;

    // TODO: use this init for the real game
    public GamePlayScene(List<Player> players) {
        Rectangle viewport = UiTheme.boardViewport();

        this.game = new Game(new Board(), new TileBag(), players);
        this.camera = new BoardCamera(viewport.getCenterX(), viewport.getCenterY(), UiTheme.BOARD_BASE_CELL_SIZE);

        this.boardView = new BoardView(camera, viewport);
        this.boardFrame = new BoardFrame();
        this.tileRack = new TileRack((int) viewport.getCenterX(), bandCenterY(viewport), handover);
        this.turnIndicator = new TurnIndicator(game, (int) viewport.getCenterX(), handover);
        this.playerCards = new PlayerCardColumn(game, handover);
        this.tileBagCounter = new TileBagCounter(game.getTileBag());
        this.sideButtons = new SideButtonBar(getWidth() - UiTheme.SIDE_BUTTON_MARGIN_RIGHT, this::endTurn, this::openDebugMode);
        this.endTurnButton = sideButtons.getEndTurnButton();

        addEntities(boardView, boardFrame, turnIndicator, tileRack, playerCards, tileBagCounter, sideButtons);

        DebugMode.attach(this.game);
    }

    public GamePlayScene() {
        Rectangle viewport = UiTheme.boardViewport();

        this.game = new Game(new Board(), new TileBag(), List.of(new Player("Player 1"), new Player("Player 2")));
        this.camera = new BoardCamera(viewport.getCenterX(), viewport.getCenterY(), UiTheme.BOARD_BASE_CELL_SIZE);

        this.boardView = new BoardView(camera, viewport);
        this.boardFrame = new BoardFrame();
        this.tileRack = new TileRack((int) viewport.getCenterX(), bandCenterY(viewport), handover);
        this.turnIndicator = new TurnIndicator(game, (int) viewport.getCenterX(), handover);
        this.playerCards = new PlayerCardColumn(game, handover);
        this.tileBagCounter = new TileBagCounter(game.getTileBag());
        this.sideButtons = new SideButtonBar(getWidth() - UiTheme.SIDE_BUTTON_MARGIN_RIGHT, this::endTurn, this::openDebugMode);
        this.endTurnButton = sideButtons.getEndTurnButton();

        addEntities(boardView, boardFrame, turnIndicator, tileRack, playerCards, tileBagCounter, sideButtons);

        DebugMode.attach(this.game);
    }

    @Override
    public void onDestroy() {
        DebugMode.detach();
    }

    private int bandCenterY(Rectangle viewport) {
        int bandTop = viewport.y + viewport.height;
        return bandTop + (getHeight() - bandTop) / 2;
    }

    @Override
    public void onRender(Graphics2D g) {
        g.setColor(UiTheme.BACKGROUND);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void render(Graphics2D g) {
        super.render(g);
        if (debugModeScene != null) debugModeScene.render(g);
    }

    @Override
    public void onTick(double dt) {
        if (debugModeScene != null) {
            // The board underneath stays exactly as it was while the panel is open.
            debugModeScene.update(dt);
            return;
        }

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

    private void startHandoverIfTurnPassed() {
        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer == presentedPlayer) return;
        if (handover.isRunning()) return;

        boolean isFirstFrame = presentedPlayer == null;
        presentedPlayer = currentPlayer;

        // Nothing to hand over from at the start of the game; the HUD is simply there.
        if (!isFirstFrame) handover.start();
    }

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

    private void handleZoomInput() {
        double scroll = InputManager.getScrollDelta();
        if (scroll == 0) return;
        if (!boardView.contains(InputManager.getMouseX(), InputManager.getMouseY())) return;

        camera.zoomBy(-scroll * ZOOM_FACTOR);
    }

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

    private void openDebugMode() {
        if (debugModeScene != null) return;

        debugModeScene = new DebugModeScene();
        debugModeScene.setOnClose(this::closeDebugMode);
    }

    private void closeDebugMode() {
        if (debugModeScene == null) return;

        debugModeScene.destroy();
        debugModeScene = null;
    }

    private void endTurn() {
        try {
            Player player = game.getCurrentPlayer();
            int points = game.endTurn();
            tileRack.clearSelection();
            System.out.println(player.getName() + " erhält " + points + " Punkte.");

            DebugMode.printScores();
        } catch (IllegalStateException e) {
            // Zug ist noch nicht abschließbar (z.B. kein Stein gelegt) -> Eingabe wird ignoriert
            System.out.println(e.getMessage());
        }
    }
}
