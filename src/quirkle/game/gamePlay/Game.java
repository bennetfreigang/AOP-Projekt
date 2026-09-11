package quirkle.game.gamePlay;

import quirkle.game.gamePlay.board.Board;
import quirkle.game.gamePlay.board.Position;
import quirkle.game.gamePlay.player.Player;
import quirkle.game.gamePlay.tiles.Tile;
import quirkle.game.gamePlay.tiles.TileBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Game {
    private final Board board;
    private final TileBag tileBag;
    private final List<Player> players;
    private int currentPlayerIndex;

    /**
     * The turn being played right now, counted from one across all players.
     *
     * @note Counts turns, not rounds
     */
    private int turnNumber = 1;

    /**
     * Starts a match and deals a full hand to every player.
     *
     * @throws IllegalArgumentException if {@code players} is empty
     */
    public Game(Board board, TileBag tileBag, List<Player> players) {
        if (players.isEmpty()) {
            throw new IllegalArgumentException("Cannot start a game without players.");
        }

        this.board = board;
        this.tileBag = tileBag;
        this.players = new ArrayList<>(players);
        this.currentPlayerIndex = 0;

        dealStartHands();
    }

    public Board getBoard() {
        return board;
    }

    public TileBag getTileBag() {
        return tileBag;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * @return the players in the order they will play from here on
     */
    public List<Player> getTurnOrder() {
        List<Player> turnOrder = new ArrayList<>(players.size());
        for (int i = 0; i < players.size(); i++) {
            turnOrder.add(players.get((currentPlayerIndex + i) % players.size()));
        }
        return turnOrder;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public boolean hasPendingTiles() {
        return !board.getPendingTiles().isEmpty();
    }

    /**
     * Stages {@code tile} at {@code position} and takes it off the current player's rack.
     *
     * @throws IllegalStateException if the current player does not hold {@code tile}, or if the
     *         board rejects the placement
     */
    public void placeTile(Position position, Tile tile) {
        Player currentPlayer = getCurrentPlayer();
        if (!currentPlayer.hasTile(tile)) {
            throw new IllegalStateException("Cannot place tile; it is not on the current player's rack.");
        }

        board.placeTile(position, tile);
        currentPlayer.removeTile(tile);
    }

    /**
     * Takes the tile staged at {@code position} back into the current player's hand.
     *
     * @note If the remaining tiles form an invalid move all tiles go back with it
     * @throws IllegalStateException if no tile was staged at {@code position} this turn
     */
    public void takeBackTile(Position position) {
        Tile tile = board.removePendingTile(position);
        if (tile == null) {
            throw new IllegalStateException("Cannot take back tile; no tile was placed there this turn.");
        }

        Player currentPlayer = getCurrentPlayer();
        currentPlayer.addTile(tile);

        if (board.checkPendingPlacement().isLegal()) return;

        takeBackAllTiles();
    }

    /**
     * Takes every tile staged this turn back into the current player's hand.
     */
    public void takeBackAllTiles() {
        Player currentPlayer = getCurrentPlayer();

        for (Tile staged : board.removeAllPendingTiles()) {
            currentPlayer.addTile(staged);
        }
    }

    /**
     * commits the pending tiles, credits the points, refills the hand and passes the turn on
     *
     * @return the points scored this turn
     * @throws IllegalStateException if no tile was placed this turn
     */
    public int endTurn() {
        if (!hasPendingTiles()) {
            throw new IllegalStateException("Cannot end turn, no tile was placed.");
        }

        Player currentPlayer = getCurrentPlayer();
        int points = board.commitPendingTiles();

        currentPlayer.increaseScore(points);
        currentPlayer.setLastRoundScore(points);
        currentPlayer.refillRack(tileBag);

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        turnNumber++;

        return points;
    }

    /**
     * Makes the player at {@code index} the one to move.
     *
     * @throws IndexOutOfBoundsException if the game has no player at {@code index}
     */
    public void setCurrentPlayerIndex(int index) {
        if (index < 0 || index >= players.size()) {
            throw new IndexOutOfBoundsException("No player at index " + index + "; the game has " + players.size() + ".");
        }

        if (index == currentPlayerIndex) return;

        takeBackAllTiles();
        currentPlayerIndex = index;
    }

    /** Deals a full hand to every player, in turn order. */
    private void dealStartHands() {
        for (Player player : players) {
            player.refillRack(tileBag);
        }
    }
}
