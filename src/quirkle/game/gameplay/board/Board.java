package quirkle.game.gameplay.board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import quirkle.game.gameplay.tiles.Tile;

/** Sparse-grid game board, split into committed ({@link #placedTiles}) and not-yet-committed ({@link #pendingTiles}) tiles. */
public class Board {
    /** Tiles committed to the board. */
    private final Map<Position, Tile> placedTiles;

    /** Tiles placed this turn but not yet committed. */
    private final Map<Position, Tile> pendingTiles;

    public Board() {
        this.placedTiles = new HashMap<>();
        this.pendingTiles = new HashMap<>();
    }

    /**
     * Creates a board that already holds tiles, for setting a position up in one go.
     *
     * @note Copies both maps rather than keeping them: the board is the only thing allowed to
     *       change its own contents, and an immutable map handed in here would otherwise make
     *       the next placement fail.
     */
    public Board(Map<Position, Tile> placedTiles, Map<Position, Tile> pendingTiles) {
        this.placedTiles = new HashMap<>(placedTiles);
        this.pendingTiles = new HashMap<>(pendingTiles);
    }

    /**
     * @return the committed tiles, as a read-only view
     * @note A view, not a copy: it keeps up with the board, but writing to it throws. Tiles reach
     *       the board through {@link #placeTile} and {@link #commitPendingTiles}, which is what
     *       keeps every tile on it a legal one.
     */
    public Map<Position, Tile> getPlacedTiles() {
        return Collections.unmodifiableMap(placedTiles);
    }

    /** @return the tiles staged this turn, as a read-only view. @see #getPlacedTiles() */
    public Map<Position, Tile> getPendingTiles() {
        return Collections.unmodifiableMap(pendingTiles);
    }

    /**
     * @return the tile on {@code position}, or {@code null} if the cell is empty
     * @note A tile staged this turn wins over a committed one, which cannot happen while the
     *       rules are followed but keeps the answer defined if a position ever holds both.
     */
    public Tile getTileAt(Position position) {
        Tile pendingTile = pendingTiles.get(position);
        return pendingTile != null ? pendingTile : placedTiles.get(position);
    }

    /** @return whether {@code position} holds a tile, committed or staged. */
    public boolean isOccupied(Position position) {
        return getTileAt(position) != null;
    }

    /**
     * @return smallest x-coordinate holding a committed tile
     * @apiNote Returns {@code 0} if the board is empty or if every placed tile has x &gt;= 0: the
     *          extent is measured from the origin outwards, so it always contains {@code (0, 0)}.
     */
    public int minX() {
        int min = 0;
        for (Position position : placedTiles.keySet()) {
            if (position.x() < min) {
                min = position.x();
            }
        }
        return min;
    }

    /**
     * @return largest x-coordinate holding a committed tile
     * @apiNote Returns {@code 0} if the board is empty or if every placed tile has x &lt;= 0.
     * @see #minX()
     */
    public int maxX() {
        int max = 0;
        for (Position position : placedTiles.keySet()) {
            if (position.x() > max) {
                max = position.x();
            }
        }
        return max;
    }

    /**
     * @return smallest y-coordinate holding a committed tile
     * @apiNote Returns {@code 0} if the board is empty or if every placed tile has y &gt;= 0.
     * @see #minX()
     */
    public int minY() {
        int min = 0;
        for (Position position : placedTiles.keySet()) {
            if (position.y() < min) {
                min = position.y();
            }
        }
        return min;
    }

    /**
     * @return largest y-coordinate holding a committed tile
     * @apiNote Returns {@code 0} if the board is empty or if every placed tile has y &lt;= 0.
     * @see #minX()
     */
    public int maxY() {
        int max = 0;
        for (Position position : placedTiles.keySet()) {
            if (position.y() > max) {
                max = position.y();
            }
        }
        return max;
    }

    /** Clears both placed and pending tiles. */
    public void clear() {
        this.placedTiles.clear();
        this.pendingTiles.clear();
    }

    /**
     * Stages {@code tile} at {@code position} as a pending tile for the current turn.
     *
     * @throws IllegalStateException if {@code position} is already occupied by a placed or pending tile,
     *         or if placing {@code tile} there would make the pending tiles illegal.
     */
    public void placeTile(Position position, Tile tile) {
        PlacementResult result = checkPlacement(position, tile);

        if (!result.isLegal()) {
            throw new IllegalStateException("Cannot place tile; " + result.getDescription() + ".");
        }

        pendingTiles.put(position, tile);
    }

    public void placeTiles(Map<Position, Tile> tiles) {
        Map<Position, Tile> candidate = new HashMap<>(pendingTiles);
        candidate.putAll(tiles);

        PlacementResult result = PlacementValidator.checkPendingTilePlacement(placedTiles, candidate);
        if (!result.isLegal()) throw new IllegalStateException("Cannot place tiles; " + result.getDescription() + ".");

        pendingTiles.putAll(tiles);
    }

    /**
     * Takes the pending tile at {@code position} off the board.
     *
     * @return the tile that stood there, or {@code null} if the position held no pending tile
     * @note Committed tiles are untouched; only the current turn's placements can be taken back.
     */
    public Tile removePendingTile(Position position) {
        return pendingTiles.remove(position);
    }

    /**
     * Takes every pending tile off the board.
     *
     * @return the tiles that were staged, in no particular order
     */
    public List<Tile> removeAllPendingTiles() {
        List<Tile> removed = new ArrayList<>(pendingTiles.values());
        pendingTiles.clear();
        return removed;
    }

    /**
     * @return whether the tiles staged so far could be committed as they stand
     * @note Nothing staged counts as legal: an empty turn is not a broken one.
     */
    public boolean isPendingPlacementLegal() {
        return checkPendingPlacement().isLegal();
    }

    /**
     * Commits all pending tiles and returns the points scored.
     *
     * @throws IllegalStateException if a pending tile's position is already occupied.
     */
    public int commitPendingTiles() {
        PlacementResult result = PlacementValidator.checkPendingTilePlacement(placedTiles, pendingTiles);

        if (!result.isLegal()) {
            throw new IllegalStateException("Cannot commit pending tiles; " + result.getDescription() + ".");
        }

        int points = ScoreCalculator.calculatePendingTilesScore(placedTiles, pendingTiles);
        placedTiles.putAll(pendingTiles);
        pendingTiles.clear();
        return points;
    }

    /**
     * Checks whether {@code tile} could be staged at {@code position} right now, without staging it.
     *
     * @return {@link PlacementResult#LEGAL}, or the rule that would reject the placement
     * @note The question {@link #placeTile} answers by throwing, asked without changing anything -
     *       which is what lets a caller say why a move is impossible before it is attempted.
     */
    public PlacementResult checkPlacement(Position position, Tile tile) {
        return PlacementValidator.checkTilePlacement(placedTiles, pendingTiles, position, tile);
    }

    /**
     * @return {@link PlacementResult#LEGAL} if the tiles staged so far could be committed, or the
     *         rule that stops them
     * @note Nothing staged counts as legal, for the same reason as in {@link #isPendingPlacementLegal()}.
     */
    public PlacementResult checkPendingPlacement() {
        if (pendingTiles.isEmpty()) return PlacementResult.LEGAL;
        return PlacementValidator.checkPendingTilePlacement(placedTiles, pendingTiles);
    }

    /**
     * @return the points the staged tiles would earn if the turn ended now, {@code 0} if nothing
     *         is staged
     * @note Scores without committing, so a move can be held against an expected score while it
     *       is still on the board to look at.
     */
    public int getPendingScore() {
        if (pendingTiles.isEmpty()) return 0;
        return ScoreCalculator.calculatePendingTilesScore(placedTiles, pendingTiles);
    }
}
