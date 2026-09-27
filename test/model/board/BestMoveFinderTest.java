package model.board;

import model.tiles.Tile;
import model.tiles.TileColor;
import model.tiles.TileSymbol;
import testing.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static testing.Assertions.assertEquals;
import static testing.Assertions.assertTrue;

public class BestMoveFinderTest {

    @Test
    void testFirstMovePlacesAsManyMatchingTilesAsPossible() {
        List<Tile> hand = List.of(
                tile(TileColor.RED, TileSymbol.CIRCLE),
                tile(TileColor.RED, TileSymbol.STAR),
                tile(TileColor.RED, TileSymbol.CROSS),
                tile(TileColor.BLUE, TileSymbol.SQUARE),
                tile(TileColor.GREEN, TileSymbol.DIAMOND),
                tile(TileColor.YELLOW, TileSymbol.HEXAGON));

        Move move = BestMoveFinder.findBestMove(Map.of(), hand);

        assertEquals(3, move.tiles().size(), "First move uses all three red tiles");
        assertEquals(3, move.score(), "Three tiles in a row score three points");
    }

    @Test
    void testFindsQwirkle() {
        Map<Position, Tile> placed = new HashMap<>();
        placed.put(at(0, 0), tile(TileColor.RED, TileSymbol.CIRCLE));
        placed.put(at(1, 0), tile(TileColor.RED, TileSymbol.SQUARE));
        placed.put(at(2, 0), tile(TileColor.RED, TileSymbol.DIAMOND));
        placed.put(at(3, 0), tile(TileColor.RED, TileSymbol.HEXAGON));
        placed.put(at(4, 0), tile(TileColor.RED, TileSymbol.STAR));

        List<Tile> hand = List.of(tile(TileColor.RED, TileSymbol.CROSS), tile(TileColor.BLUE, TileSymbol.CIRCLE));

        Move move = BestMoveFinder.findBestMove(placed, hand);

        assertEquals(12, move.score(), "Completing the red row is a Qwirkle: 6 + 6 bonus");
    }

    @Test
    void testMultiTileMoveBeatsSingleTile() {
        Map<Position, Tile> placed = new HashMap<>();
        placed.put(at(0, 0), tile(TileColor.BLUE, TileSymbol.CIRCLE));

        List<Tile> hand = List.of(
                tile(TileColor.BLUE, TileSymbol.STAR),
                tile(TileColor.BLUE, TileSymbol.CROSS),
                tile(TileColor.BLUE, TileSymbol.SQUARE));

        Move move = BestMoveFinder.findBestMove(placed, hand);

        // best: all three in a column next to the circle, the middle one also forms a row with it
        // column of three: 3 points, row with the circle: 2 points -> 5 (a plain row of four gives only 4)
        assertEquals(3, move.tiles().size(), "All three blue tiles are laid");
        assertEquals(5, move.score(), "Column of three plus the crossing row");
    }

    @Test
    void testReturnsNullWithoutLegalMove() {
        Map<Position, Tile> placed = Map.of(at(0, 0), tile(TileColor.RED, TileSymbol.CIRCLE));
        List<Tile> hand = List.of(tile(TileColor.BLUE, TileSymbol.STAR), tile(TileColor.RED, TileSymbol.CIRCLE));

        assertEquals(null, BestMoveFinder.findBestMove(placed, hand), "No tile fits next to the red circle");
    }

    @Test
    void testBestMoveIsLegalAndScoredLikeTheBoard() {
        Map<Position, Tile> placed = new HashMap<>();
        placed.put(at(0, 0), tile(TileColor.RED, TileSymbol.CIRCLE));
        placed.put(at(1, 0), tile(TileColor.RED, TileSymbol.SQUARE));
        placed.put(at(1, 1), tile(TileColor.GREEN, TileSymbol.SQUARE));

        List<Tile> hand = List.of(
                tile(TileColor.GREEN, TileSymbol.CIRCLE),
                tile(TileColor.GREEN, TileSymbol.STAR),
                tile(TileColor.YELLOW, TileSymbol.SQUARE),
                tile(TileColor.RED, TileSymbol.STAR));

        Move move = BestMoveFinder.findBestMove(placed, hand);
        Board board = new Board(placed, move.tiles());

        assertTrue(board.isPendingPlacementLegal(), "Suggested move is legal");
        assertEquals(board.getPendingScore(), move.score(), "Suggested score matches the board's score");
    }

    private static Position at(int x, int y) {
        return new Position(x, y);
    }

    private static Tile tile(TileColor color, TileSymbol symbol) {
        return new Tile(color, symbol);
    }
}
