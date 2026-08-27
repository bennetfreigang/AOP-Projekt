package quirkle.game.gamePlay;

import quirkle.game.gamePlay.board.Board;
import quirkle.game.gamePlay.board.Position;
import quirkle.game.gamePlay.player.Player;
import quirkle.game.gamePlay.tiles.Tile;
import quirkle.game.gamePlay.tiles.TileBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {
    private final Board board;
    private final TileBag tileBag;
    private final List<Player> players;
    private int currentPlayerIndex;

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

    public boolean hasPendingTiles() {
        return !board.getPendingTiles().isEmpty();
    }

    public void placeTile(Position position, Tile tile) {
        Player currentPlayer = getCurrentPlayer();
        if (!currentPlayer.hasTile(tile)) {
            throw new IllegalStateException("Cannot place tile; it is not on the current player's rack.");
        }

        board.placeTile(position, tile);
        currentPlayer.removeTile(tile);
    }

    /**
     * Ends the current turn: commits the pending tiles, credits the points, refills the hand
     * and passes the turn on.
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
        currentPlayer.refillHand(tileBag);

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

        return points;
    }

    /** Deals a full hand to every player, in turn order.*/
    private void dealStartHands() {
        for (Player player : players) {
            player.refillHand(tileBag);
        }
    }
}
