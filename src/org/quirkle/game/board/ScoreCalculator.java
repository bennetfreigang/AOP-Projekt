package org.quirkle.game.board;

import org.quirkle.game.tiles.Tile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToIntFunction;

/** Scores tile placements on a {@link Board}. */
public class ScoreCalculator {

    private static final int QWIRKLE_LINE_LENGTH = 6;
    private static final int QWIRKLE_BONUS = 6;

    /**
     * Scores the board's pending tiles: every row and column formed or extended by them counts
     * once, worth one point per tile in that line, plus a {@value QWIRKLE_BONUS}-point bonus for
     * a completed six-tile line. A pending tile sitting at a crossing point scores both its row
     * and its column.
     *
     * @return points earned for the board's pending tiles
     * @throws IllegalStateException if more than one pending tile is placed and they are not
     *         aligned in a single row or column
     */
    public static int calculatePendingTilesScore(Map<Position, Tile> placedTiles, Map<Position, Tile> pendingTiles) {
        if (pendingTiles.size() > 1) {
            requireSingleLineAlignment(pendingTiles.keySet());
        }

        Map<Position, Tile> allTiles = mergeTiles(placedTiles, pendingTiles);
        List<Integer> formedLineLengths = findFormedLineLengths(allTiles, pendingTiles);

        if (formedLineLengths.isEmpty()) {
            // First move of the game: a lone tile with no neighbors in either direction still scores.
            return pendingTiles.size();
        }
        return sumWithQwirkleBonus(formedLineLengths);
    }

    /** @throws IllegalStateException unless every position shares one x or one y coordinate. */
    private static void requireSingleLineAlignment(Set<Position> pendingPositions) {
        boolean sameX = pendingPositions.stream().map(Position::x).distinct().count() == 1;
        boolean sameY = pendingPositions.stream().map(Position::y).distinct().count() == 1;
        if (!sameX && !sameY) {
            throw new IllegalStateException("Pending tiles are not aligned in a single row or column.");
        }
    }

    /** @return the length of every row/column that the pending tiles form or extend, one entry per line. */
    private static List<Integer> findFormedLineLengths(Map<Position, Tile> allTiles, Map<Position, Tile> pendingTiles) {
        List<Integer> lineLengths = new ArrayList<>();
        for (LineOrientation orientation : LineOrientation.values()) {
            addLineLengthsForOrientation(allTiles, pendingTiles, orientation, lineLengths);
        }
        return lineLengths;
    }

    /** Appends the length of each line, in {@code orientation}, that the pending tiles form or extend. */
    private static void addLineLengthsForOrientation(Map<Position, Tile> allTiles, Map<Position, Tile> pendingTiles, LineOrientation orientation, List<Integer> lineLengths) {
        for (int lineKey : distinctLineKeys(pendingTiles.keySet(), orientation)) {
            int lineLength = calculateLineLength(allTiles, pendingTiles, orientation, lineKey);
            if (lineLength >= 2) {
                lineLengths.add(lineLength);
            }
        }
    }

    /** @return the sum of {@code lineLengths}, with a {@value QWIRKLE_BONUS}-point bonus added per completed six-tile line. */
    private static int sumWithQwirkleBonus(List<Integer> lineLengths) {
        int score = 0;
        for (int lineLength : lineLengths) {
            score += lineLength;
            if (lineLength == QWIRKLE_LINE_LENGTH) {
                score += QWIRKLE_BONUS;
            }
        }
        return score;
    }

    /** @return the distinct line-identifying coordinates (e.g. the y-values shared by tiles on a {@link LineOrientation#HORIZONTAL} line) touched by {@code positions}. */
    private static Set<Integer> distinctLineKeys(Set<Position> positions, LineOrientation orientation) {
        Set<Integer> lineKeys = new HashSet<>();
        for (Position position : positions) {
            lineKeys.add(orientation.fixedCoordinate.applyAsInt(position));
        }
        return lineKeys;
    }

    /** @return length of the contiguous streak, along {@code orientation}, formed by the pending tiles on {@code lineKey} together with their in-line neighbors. */
    private static int calculateLineLength(Map<Position, Tile> allTiles, Map<Position, Tile> pendingTiles, LineOrientation orientation, int lineKey) {
        List<Position> pendingOnLine = pendingTilesOnLine(pendingTiles.keySet(), orientation, lineKey);

        Position innermostStart = earliestPendingPosition(pendingOnLine, orientation);
        Position innermostEnd = latestPendingPosition(pendingOnLine, orientation);

        Position lineStart = walkToStreakEnd(allTiles, innermostStart, orientation.backwardDirection);
        Position lineEnd = walkToStreakEnd(allTiles, innermostEnd, orientation.forwardDirection);

        return orientation.extentCoordinate.applyAsInt(lineEnd) - orientation.extentCoordinate.applyAsInt(lineStart) + 1;
    }

    /** @return the pending positions that lie on the line identified by {@code lineKey} in {@code orientation}. */
    private static List<Position> pendingTilesOnLine(Set<Position> pendingPositions, LineOrientation orientation, int lineKey) {
        List<Position> pendingOnLine = new ArrayList<>();
        for (Position position : pendingPositions) {
            if (orientation.fixedCoordinate.applyAsInt(position) == lineKey) {
                pendingOnLine.add(position);
            }
        }
        return pendingOnLine;
    }

    /** @return the pending position closest to the line's backward end. */
    private static Position earliestPendingPosition(List<Position> pendingOnLine, LineOrientation orientation) {
        return pendingOnLine.stream().min(Comparator.comparingInt(orientation.extentCoordinate)).orElseThrow();
    }

    /** @return the pending position closest to the line's forward end. */
    private static Position latestPendingPosition(List<Position> pendingOnLine, LineOrientation orientation) {
        return pendingOnLine.stream().max(Comparator.comparingInt(orientation.extentCoordinate)).orElseThrow();
    }

    /** @return a new map containing all entries from both {@code first} and {@code second}. */
    private static Map<Position, Tile> mergeTiles(Map<Position, Tile> first, Map<Position, Tile> second) {
        Map<Position, Tile> merged = new HashMap<>(first);
        merged.putAll(second);
        return merged;
    }

    /** @return the outermost position reachable from {@code start} by repeatedly stepping {@code direction} through {@code lineTiles}. */
    private static Position walkToStreakEnd(Map<Position, Tile> lineTiles, Position start, Direction direction) {
        Position current = start;
        while (lineTiles.containsKey(current.neighbor(direction))) {
            current = current.neighbor(direction);
        }
        return current;
    }

    /**
     * Orientation a line can run in, with the data needed to find tiles on that line and measure
     * its length.
     * <p>
     * {@code fixedCoordinate} is the coordinate shared by every tile on the line (used to group
     * pending tiles by the line they belong to); {@code extentCoordinate} is the coordinate that
     * varies along the line (used to walk it and measure its length).
     */
    private enum LineOrientation {
        HORIZONTAL(Position::y, Position::x, Direction.WEST, Direction.EAST),
        VERTICAL(Position::x, Position::y, Direction.NORTH, Direction.SOUTH);

        private final ToIntFunction<Position> fixedCoordinate;
        private final ToIntFunction<Position> extentCoordinate;
        private final Direction backwardDirection;
        private final Direction forwardDirection;

        LineOrientation(ToIntFunction<Position> fixedCoordinate, ToIntFunction<Position> extentCoordinate,
                         Direction backwardDirection, Direction forwardDirection) {
            this.fixedCoordinate = fixedCoordinate;
            this.extentCoordinate = extentCoordinate;
            this.backwardDirection = backwardDirection;
            this.forwardDirection = forwardDirection;
        }
    }
}
