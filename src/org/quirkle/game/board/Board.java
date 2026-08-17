package org.quirkle.game.board;

import java.util.HashMap;
import java.util.Map;

import org.quirkle.game.tiles.Tile;

/** Sparse-grid game board, split into committed ({@link #placedTiles}) and not-yet-committed ({@link #pendingTiles}) tiles. */
public class Board {
    /** Tiles committed to the board. */
    private Map<Position, Tile> placedTiles;

    /** Tiles placed this turn but not yet committed. */
    private Map<Position, Tile> pendingTiles;

    public Board() {
        this.placedTiles = new HashMap<>();
        this.pendingTiles = new HashMap<>();
    }

    public Board(Map<Position, Tile> placedTiles, Map<Position, Tile> pendingTiles) {
        this.placedTiles = placedTiles;
        this.pendingTiles = pendingTiles;
    }

    public Map<Position, Tile> getPlacedTiles() {
        return placedTiles;
    }

    public Map<Position, Tile> getPendingTiles() {
        return pendingTiles;
    }

    /**
     * @return smallest occupied x-coordinate.
     * @apiNote Returns {@code 0} if the board is empty or if every placed tile has x &gt;= 0.
     */
    public int minX() {
        int min = placedTiles.keySet().iterator().next().x();
        for (Position position : placedTiles.keySet()) {
            if (position.x() < min) {
                min = position.x();
            }
        }
        return min;
    }

    /** @return largest occupied x-coordinate. @apiNote Returns {@code 0} if the board is empty or if every placed tile has x &lt;= 0. */
    public int maxX() {
        int max = placedTiles.keySet().iterator().next().x();
        for (Position position : placedTiles.keySet()) {
            if (position.x() > max) {
                max = position.x();
            }
        }
        return max;
    }

    /** @return smallest occupied y-coordinate. @apiNote Returns {@code 0} if the board is empty or if every placed tile has y &gt;= 0. */
    public int minY() {
        int min = placedTiles.keySet().iterator().next().y();
        for (Position position : placedTiles.keySet()) {
            if (position.y() < min) {
                min = position.y();
            }
        }
        return min;
    }

    /** @return largest occupied y-coordinate. @apiNote Returns {@code 0} if the board is empty or if every placed tile has y &lt;= 0. */
    public int maxY() {
        int max = placedTiles.keySet().iterator().next().y();
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
        if (!PlacementValidator.isTilePlacementPossible(placedTiles, pendingTiles, position, tile)) {
            throw new IllegalStateException("Cannot place tile; placement is not possible.");
        }

        pendingTiles.put(position, tile);
    }

    /**
     * Commits all pending tiles and returns the points scored.
     *
     * @throws IllegalStateException if a pending tile's position is already occupied.
     */
    public int commitPendingTiles() {
        if (!isPendingTilesPlacementPossible()) {
            throw new IllegalStateException("Cannot commit pending tiles; placement is not possible.");
        }

        int points = ScoreCalculator.calculatePendingTilesScore(placedTiles, pendingTiles);
        placedTiles.putAll(pendingTiles);
        pendingTiles.clear();
        return points;
    }

    /** @return {@code true} if {@link #pendingTiles} can be legally committed onto {@link #placedTiles}. */
    private boolean isPendingTilesPlacementPossible() {
        return PlacementValidator.isPendingTilePlacementPossible(placedTiles, pendingTiles);
    }
}
