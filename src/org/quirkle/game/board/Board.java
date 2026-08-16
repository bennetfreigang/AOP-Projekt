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
     * Commits all pending tiles and returns the points scored.
     *
     * @throws IllegalStateException if a pending tile's position is already occupied.
     * @apiNote Behavior when {@link #pendingTiles} is empty is not yet decided.
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

    /**
     * @return {@code true} if every pending tile's position is free on {@link #placedTiles}.
     * @apiNote Behavior when {@link #pendingTiles} is empty is not yet decided.
     */
    private boolean isPendingTilesPlacementPossible() {
        for (Position position : pendingTiles.keySet()) {
            if (!PlacementValidator.isTilePlacementPossible(placedTiles, position)) {
                return false;
            }
        }

        return true;
    }
}
