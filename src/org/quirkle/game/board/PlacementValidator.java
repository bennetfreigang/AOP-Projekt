package org.quirkle.game.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quirkle.game.tiles.Tile;
import org.quirkle.game.tiles.TileColor;
import org.quirkle.game.tiles.TileSymbol;

/** Validates whether tiles may legally be placed onto a Qwirkle board, per the game's placement rules. */
class PlacementValidator {

    /**
     * @return {@code true} if every tile in {@code pendingTiles} could legally be committed onto {@code placedTiles}:
     *         all pending positions lie in one common row or column, are unoccupied, connect to the existing board
     *         (unless it is still empty), and every line they touch is a valid Qwirkle line.
     */
    static boolean isPendingTilePlacementPossible(Map<Position, Tile> placedTiles, Map<Position, Tile> pendingTiles) {
        if (!isPendingTilesInLine(pendingTiles.keySet())) {
            return false;
        }
        if (!isConnectedToBoard(placedTiles, pendingTiles)) {
            return false;
        }

        Map<Position, Tile> allTiles = new HashMap<>(placedTiles);
        allTiles.putAll(pendingTiles);

        for (Map.Entry<Position, Tile> entry : pendingTiles.entrySet()) {
            if (!isPositionFree(placedTiles, entry.getKey())) {
                return false;
            }
            if (!formsValidLines(allTiles, entry.getKey())) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return {@code true} if {@code tile} could be added to {@code pendingTiles} at {@code position} — i.e.
     *         {@code position} is not already occupied and the resulting pending tiles would still be a legal placement.
     */
    static boolean isTilePlacementPossible(Map<Position, Tile> placedTiles, Map<Position, Tile> pendingTiles, Position position, Tile tile) {
        if (placedTiles.containsKey(position) || pendingTiles.containsKey(position)) {
            return false;
        }

        Map<Position, Tile> candidatePendingTiles = new HashMap<>(pendingTiles);
        candidatePendingTiles.put(position, tile);
        return isPendingTilePlacementPossible(placedTiles, candidatePendingTiles);
    }

    // --- Rule: pending tiles share one common row or column --------------------

    /** @return {@code true} if every position in {@code positions} shares the same row or the same column. */
    private static boolean isPendingTilesInLine(Set<Position> positions) {
        boolean sameRow = positions.stream().map(Position::y).distinct().count() == 1;
        boolean sameColumn = positions.stream().map(Position::x).distinct().count() == 1;
        return sameRow || sameColumn;
    }

    // --- Rule: pending tiles connect to the existing board ----------------------

    /** @return {@code true} if {@code placedTiles} is empty (first move) or some position in {@code pendingTiles} touches a tile in {@code placedTiles}. */
    private static boolean isConnectedToBoard(Map<Position, Tile> placedTiles, Map<Position, Tile> pendingTiles) {
        if (placedTiles.isEmpty()) {
            return true;
        }
        for (Position position : pendingTiles.keySet()) {
            if (tileHasNeighbor(placedTiles, position)) {
                return true;
            }
        }
        return false;
    }

    /** @return {@code true} if {@code position} has at least one occupied neighbor in {@code tiles}. */
    private static boolean tileHasNeighbor(Map<Position, Tile> tiles, Position position) {
        for (Direction direction : Direction.values()) {
            if (tiles.containsKey(position.neighbor(direction))) {
                return true;
            }
        }
        return false;
    }

    // --- Rule: every pending position is still free -----------------------------

    /** @return {@code true} if {@code position} is free in {@code placedTiles}. */
    private static boolean isPositionFree(Map<Position, Tile> placedTiles, Position position) {
        return !placedTiles.containsKey(position);
    }

    // --- Rule: every line a pending tile touches is a valid Qwirkle line --------

    /** @return {@code true} if both the horizontal and the vertical line through {@code position} are valid Qwirkle lines. */
    private static boolean formsValidLines(Map<Position, Tile> tiles, Position position) {
        return isValidLine(collectLine(tiles, position, Direction.WEST, Direction.EAST))
                && isValidLine(collectLine(tiles, position, Direction.NORTH, Direction.SOUTH));
    }

    /** @return every tile contiguously connected to {@code position} along the axis given by {@code negative}/{@code positive}, {@code position}'s own tile included. */
    private static List<Tile> collectLine(Map<Position, Tile> tiles, Position position, Direction negative, Direction positive) {
        List<Tile> line = new ArrayList<>();
        collectInDirection(tiles, position, negative, line);
        line.add(tiles.get(position));
        collectInDirection(tiles, position, positive, line);
        return line;
    }

    /** Recursively appends every tile found by walking {@code direction} from {@code position} onto {@code line}; never doubles back. */
    private static void collectInDirection(Map<Position, Tile> tiles, Position position, Direction direction, List<Tile> line) {
        Position neighborPosition = position.neighbor(direction);
        Tile neighbor = tiles.get(neighborPosition);
        if (neighbor == null) {
            return;
        }
        line.add(neighbor);
        collectInDirection(tiles, neighborPosition, direction, line);
    }

    /**
     * @return {@code true} if {@code line} has at most one tile, or all its tiles share one color with pairwise
     *         distinct symbols, or all share one symbol with pairwise distinct colors.
     */
    private static boolean isValidLine(List<Tile> line) {
        if (line.size() <= 1) {
            return true;
        }

        Set<TileColor> colors = new HashSet<>();
        Set<TileSymbol> symbols = new HashSet<>();
        for (Tile tile : line) {
            colors.add(tile.getColor());
            symbols.add(tile.getSymbol());
        }

        boolean sameColorDistinctSymbols = colors.size() == 1 && symbols.size() == line.size();
        boolean sameSymbolDistinctColors = symbols.size() == 1 && colors.size() == line.size();
        return sameColorDistinctSymbols || sameSymbolDistinctColors;
    }
}
