package model.board;

import java.util.Map;

import model.tiles.Tile;

// used in PlacementValidator and ScoreCalculator
final class StreakWalker {

    private StreakWalker() {}

    /** @return the last occupied position reached by stepping {@code direction} from {@code start}. */
    static Position walkToStreakEnd(Map<Position, Tile> tiles, Position start, Direction direction) {
        Position current = start;
        while (tiles.containsKey(current.neighbor(direction))) {
            current = current.neighbor(direction);
        }
        return current;
    }
}
