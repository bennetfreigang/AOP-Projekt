package quirkle.game.gamePlay.board;

import quirkle.game.gamePlay.tiles.Tile;
import quirkle.game.gamePlay.tiles.TileColor;
import quirkle.game.gamePlay.tiles.TileSymbol;
import quirkle.testing.Test;

import java.util.HashMap;
import java.util.Map;

import static quirkle.testing.Assertions.assertEquals;

public class PlacementValidatorTest {

    // --- Gemeinsame Zeile oder Spalte ---------------------------------------

    @Test
    void testPendingTilesFormCommonRow() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.GREEN, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(1, 0, TileColor.GREEN, TileSymbol.HEXAGON),
                at(2, 0, TileColor.GREEN, TileSymbol.CROSS));

        assertPlacementPossible("Neue Steine bilden eine gemeinsame Zeile", true, placed, pending);
    }

    @Test
    void testPendingTilesFormCommonColumn() {
        Map<Position, Tile> placed = tilesOf(at(3, 3, TileColor.BLUE, TileSymbol.HEXAGON));
        Map<Position, Tile> pending = tilesOf(
                at(3, 4, TileColor.BLUE, TileSymbol.CROSS),
                at(3, 5, TileColor.BLUE, TileSymbol.TRIANGLE));

        assertPlacementPossible("Neue Steine bilden eine gemeinsame Spalte", true, placed, pending);
    }

    @Test
    void testPendingTilesNotAligned() {
        Map<Position, Tile> placed = tilesOf();
        Map<Position, Tile> pending = tilesOf(
                at(0, 0, TileColor.RED, TileSymbol.CIRCLE),
                at(1, 1, TileColor.RED, TileSymbol.SQUARE));

        assertPlacementPossible("Diagonal liegende Steine ohne gemeinsame Zeile/Spalte sind ungueltig", false, placed, pending);
    }

    // --- Angrenzen an bereits vorhandene Steine -----------------------------

    @Test
    void testFirstMoveNeedsNoNeighbors() {
        Map<Position, Tile> placed = tilesOf();
        Map<Position, Tile> pending = tilesOf(
                at(0, 0, TileColor.RED, TileSymbol.CIRCLE),
                at(1, 0, TileColor.RED, TileSymbol.SQUARE));

        assertPlacementPossible("Der erste Zug auf einem leeren Brett braucht keine Nachbarn", true, placed, pending);
    }

    @Test
    void testPendingTileAdjacentToExistingTile() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(at(1, 0, TileColor.RED, TileSymbol.SQUARE));

        assertPlacementPossible("Ein neuer Stein, der an einen vorhandenen Stein angrenzt, ist gueltig", true, placed, pending);
    }

    @Test
    void testPendingTilesNotAdjacentToAnyExistingTile() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(5, 5, TileColor.RED, TileSymbol.SQUARE),
                at(6, 5, TileColor.RED, TileSymbol.DIAMOND));

        assertPlacementPossible("Neue Steine, die keinen vorhandenen Stein beruehren, sind ungueltig", false, placed, pending);
    }

    // --- Gueltige Reihe: Farbe/Symbol-Muster --------------------------------

    @Test
    void testRowWithConstantColorAndDistinctSymbols() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(1, 0, TileColor.RED, TileSymbol.SQUARE),
                at(2, 0, TileColor.RED, TileSymbol.DIAMOND));

        assertPlacementPossible("Gleiche Farbe mit unterschiedlichen Symbolen ist eine gueltige Reihe", true, placed, pending);
    }

    @Test
    void testRowWithConstantSymbolAndDistinctColors() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(1, 0, TileColor.BLUE, TileSymbol.CIRCLE),
                at(2, 0, TileColor.GREEN, TileSymbol.CIRCLE));

        assertPlacementPossible("Gleiches Symbol mit unterschiedlichen Farben ist eine gueltige Reihe", true, placed, pending);
    }

    @Test
    void testRowWithDuplicateSymbol() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(at(1, 0, TileColor.RED, TileSymbol.CIRCLE));

        assertPlacementPossible("Ein doppelt vorkommendes Symbol in der Reihe ist ungueltig", false, placed, pending);
    }

    /** Symbol CIRCLE kommt an Position 0 und 2 doppelt vor, obwohl beide Nachbarpaare fuer sich passen. */
    @Test
    void testRowWithDuplicateSymbolAtADistance() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(1, 0, TileColor.RED, TileSymbol.SQUARE),
                at(2, 0, TileColor.RED, TileSymbol.CIRCLE));

        assertPlacementPossible("Ein doppelt vorkommendes Symbol in der Reihe ist auch bei nicht direkt benachbarten Steinen ungueltig", false, placed, pending);
    }

    /** Farbe RED kommt an Position 0 und 2 doppelt vor, obwohl beide Nachbarpaare fuer sich passen. */
    @Test
    void testRowWithDuplicateColor() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(1, 0, TileColor.BLUE, TileSymbol.CIRCLE),
                at(2, 0, TileColor.RED, TileSymbol.CIRCLE));

        assertPlacementPossible("Eine doppelt vorkommende Farbe in der Reihe ist ungueltig", false, placed, pending);
    }

    /** Weder Farbe (RED,RED,BLUE) noch Symbol (CIRCLE,SQUARE,SQUARE) sind ueber die ganze Reihe konstant. */
    @Test
    void testRowMixingColorAndSymbolInconsistently() {
        Map<Position, Tile> placed = tilesOf(at(0, 0, TileColor.RED, TileSymbol.CIRCLE));
        Map<Position, Tile> pending = tilesOf(
                at(1, 0, TileColor.RED, TileSymbol.SQUARE),
                at(2, 0, TileColor.BLUE, TileSymbol.SQUARE));

        assertPlacementPossible("Eine Reihe muss durchgehend die gleiche Farbe ODER das gleiche Symbol haben", false, placed, pending);
    }

    // --- Gueltige Reihe: maximal sechs Steine -------------------------------

    @Test
    void testRowOfExactlySixTilesIsValid() {
        Map<Position, Tile> placed = tilesOf(
                at(0, 0, TileColor.RED, TileSymbol.CIRCLE),
                at(1, 0, TileColor.RED, TileSymbol.SQUARE),
                at(2, 0, TileColor.RED, TileSymbol.DIAMOND),
                at(3, 0, TileColor.RED, TileSymbol.HEXAGON),
                at(4, 0, TileColor.RED, TileSymbol.TRIANGLE));
        Map<Position, Tile> pending = tilesOf(at(5, 0, TileColor.RED, TileSymbol.CROSS));

        assertPlacementPossible("Eine vollstaendige Reihe aus genau sechs Steinen ist gueltig", true, placed, pending);
    }

    @Test
    void testRowOfSevenTilesIsInvalid() {
        Map<Position, Tile> placed = tilesOf(
                at(0, 0, TileColor.RED, TileSymbol.CIRCLE),
                at(1, 0, TileColor.RED, TileSymbol.SQUARE),
                at(2, 0, TileColor.RED, TileSymbol.DIAMOND),
                at(3, 0, TileColor.RED, TileSymbol.HEXAGON),
                at(4, 0, TileColor.RED, TileSymbol.TRIANGLE));
        Map<Position, Tile> pending = tilesOf(
                at(5, 0, TileColor.RED, TileSymbol.CROSS),
                at(6, 0, TileColor.RED, TileSymbol.CIRCLE));

        assertPlacementPossible("Eine Reihe mit mehr als sechs Steinen ist ungueltig", false, placed, pending);
    }

    // --- Hilfsmethoden -------------------------------------------------------

    private static void assertPlacementPossible(String testName, boolean expected, Map<Position, Tile> placedTiles, Map<Position, Tile> pendingTiles) {
        boolean actual = PlacementValidator.isPendingTilePlacementPossible(placedTiles, pendingTiles);
        assertEquals(expected, actual, testName);
    }

    private record Placement(Position position, Tile tile) {}

    private static Placement at(int x, int y, TileColor color, TileSymbol symbol) {
        return new Placement(new Position(x, y), new Tile(color, symbol));
    }

    /** @return a mutable map built from the given position/tile pairs. */
    private static Map<Position, Tile> tilesOf(Placement... placements) {
        Map<Position, Tile> tiles = new HashMap<>();
        for (Placement placement : placements) {
            tiles.put(placement.position(), placement.tile());
        }
        return tiles;
    }
}
