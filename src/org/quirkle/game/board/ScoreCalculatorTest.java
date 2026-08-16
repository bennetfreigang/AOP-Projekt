package org.quirkle.game.board;

import org.quirkle.game.tiles.Tile;
import org.quirkle.game.tiles.TileColor;
import org.quirkle.game.tiles.TileSymbol;

import java.util.HashMap;
import java.util.Map;

/**
 * Standalone tests for {@link ScoreCalculator}. No test framework required.
 *
 * <p>Run with: {@code javac -d bin @sources.txt && java -cp bin org.quirkle.game.board.ScoreCalculatorTest}
 */
public class ScoreCalculatorTest {
    private static int failureCount = 0;

    public static void main(String[] args) {
        testSinglePendingTileWithoutNeighbors();
        testPendingTilesFormVerticalLine();
        testPendingTilesFormHorizontalLine();
        testPendingTileExtendsExistingLine();
        testGapInPlacedTilesBreaksTheStreak();
        testUnalignedPendingTilesThrow();
        testMultiplePendingTilesInSameLineAreCountedOnce();
        testPendingTileAtCrossingScoresBothLines();
        testMultiplePendingTilesEachFormOwnSecondaryLine();
        testCompletingSixTileLineScoresQwirkleBonus();
        testCrossingPendingTileCanCompleteAQwirkleOnOneAxis();
        testMultiplePendingTilesExtendExistingRowAndColumn();

        if (failureCount == 0) {
            System.out.println("All tests passed.");
        } else {
            System.out.println(failureCount + " test(s) failed.");
            System.exit(1);
        }
    }

    private static void testSinglePendingTileWithoutNeighbors() {
        Board board = new Board(tilesAt(), tilesAt(at(0, 0)));

        assertScore("Single tile without neighbors (first move)", 1, board);
    }

    private static void testPendingTilesFormVerticalLine() {
        Board board = new Board(tilesAt(), tilesAt(at(2, 1), at(2, 2), at(2, 3)));

        assertScore("Three new tiles form a column (same x)", 3, board);
    }

    private static void testPendingTilesFormHorizontalLine() {
        Board board = new Board(tilesAt(), tilesAt(at(1, 5), at(2, 5), at(3, 5)));

        assertScore("Three new tiles form a row (same y)", 3, board);
    }

    private static void testPendingTileExtendsExistingLine() {
        Board board = new Board(tilesAt(at(2, 3), at(2, 4)), tilesAt(at(2, 5)));

        assertScore("New tile extends an existing column", 3, board);
    }

    private static void testGapInPlacedTilesBreaksTheStreak() {
        Board board = new Board(
                tilesAt(at(2, 1) /* through a gap, not adjacent */, at(2, 4) /* adjacent to the pending tile */),
                tilesAt(at(2, 5)));

        assertScore("A gap breaks the contiguous streak", 2, board);
    }

    private static void testUnalignedPendingTilesThrow() {
        Board board = new Board(tilesAt(), tilesAt(at(1, 1), at(2, 2)));

        String testName = "Diagonal tiles without a shared row/column throw IllegalStateException";
        try {
            ScoreCalculator.calculatePendingTilesScore(board.getPlacedTiles(), board.getPendingTiles());
            fail(testName + " -> no exception thrown");
        } catch (IllegalStateException expected) {
            pass(testName);
        }
    }

    /** Step 1: several new tiles belonging to the same line must not have that line counted more than once. */
    private static void testMultiplePendingTilesInSameLineAreCountedOnce() {
        Board board = new Board(tilesAt(at(2, 0)), tilesAt(at(2, 1), at(2, 2)));

        // One column of 3 tiles (2,0)-(2,2) -> 3 points, not e.g. counted twice.
        assertScore("Multiple new tiles in the same column are counted once", 3, board);
    }

    /** Step 3: a single new tile at a crossing point forms two separate lines. */
    private static void testPendingTileAtCrossingScoresBothLines() {
        Board board = new Board(
                tilesAt(at(0, 5), at(1, 5), at(2, 2), at(2, 3), at(2, 4)),
                tilesAt(at(2, 5)));

        // Horizontal row (0,5)-(2,5): 3 points. Vertical column (2,2)-(2,5): 4 points. Total: 7.
        assertScore("A crossing tile scores both its row and its column", 7, board);
    }

    /** Step 4: several tiles placed in one line can each additionally form their own secondary line. */
    private static void testMultiplePendingTilesEachFormOwnSecondaryLine() {
        Board board = new Board(
                tilesAt(at(1, 4) /* secondary line for (1,5) */, at(2, 6) /* secondary line for (2,5) */),
                tilesAt(at(1, 5), at(2, 5), at(3, 5)));

        // Main row (1,5)-(3,5): 3 points. Secondary line (1,4)-(1,5): 2 points. Secondary line (2,5)-(2,6): 2 points. Total: 7.
        assertScore("Main row plus secondary lines from multiple new tiles", 7, board);
    }

    /** Step 2: a line completed to six tiles additionally earns the 6-point Qwirkle bonus. */
    private static void testCompletingSixTileLineScoresQwirkleBonus() {
        Board board = new Board(
                tilesAt(at(2, 1), at(2, 2), at(2, 3), at(2, 4), at(2, 5)),
                tilesAt(at(2, 6)));

        // Column (2,1)-(2,6): 6 tiles -> 6 + 6 bonus = 12 points.
        assertScore("A completed six-tile line earns the Qwirkle bonus", 12, board);
    }

    /** Combination of a crossing point (Step 3) and the Qwirkle bonus (Step 2) on just one of the two axes. */
    private static void testCrossingPendingTileCanCompleteAQwirkleOnOneAxis() {
        Board board = new Board(
                tilesAt(at(0, 5), at(1, 5), at(2, 0), at(2, 1), at(2, 2), at(2, 3), at(2, 4)),
                tilesAt(at(2, 5)));

        // Horizontal row (0,5)-(2,5): 3 points. Vertical column (2,0)-(2,5): 6 tiles -> 6 + 6 bonus = 12. Total: 15.
        assertScore("A crossing tile simultaneously completes a Qwirkle on one axis", 15, board);
    }

    /** Step 4: several new tiles form the main row while also extending both an existing row and an existing column. */
    private static void testMultiplePendingTilesExtendExistingRowAndColumn() {
        Board board = new Board(
                tilesAt(at(0, 5) /* existing row, left of the new tiles */, at(2, 3) /* existing column, above (2,5) */, at(2, 4)),
                tilesAt(at(1, 5), at(2, 5), at(3, 5)));

        // Row (0,5)-(3,5): 4 points. Column (2,3)-(2,5): 3 points. Total: 7.
        assertScore("Multiple new tiles simultaneously extend a row and a column", 7, board);
    }

    private static void assertScore(String testName, int expectedScore, Board board) {
        int actualScore = ScoreCalculator.calculatePendingTilesScore(board.getPlacedTiles(), board.getPendingTiles());
        if (actualScore == expectedScore) {
            pass(testName);
        } else {
            fail(testName + " -> expected " + expectedScore + ", got " + actualScore);
        }
    }

    /** @return a mutable map with the same {@link #tile()} placed at every given position. */
    private static Map<Position, Tile> tilesAt(Position... positions) {
        Map<Position, Tile> tiles = new HashMap<>();
        for (Position position : positions) {
            tiles.put(position, tile());
        }
        return tiles;
    }

    private static Position at(int x, int y) {
        return new Position(x, y);
    }

    private static Tile tile() {
        return new Tile(TileColor.RED, TileSymbol.CIRCLE);
    }

    private static void pass(String testName) {
        System.out.println("[OK]   " + testName);
    }

    private static void fail(String message) {
        failureCount++;
        System.out.println("[FAIL] " + message);
    }
}
