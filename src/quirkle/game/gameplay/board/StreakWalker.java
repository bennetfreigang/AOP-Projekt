package quirkle.game.gameplay.board;

import java.util.Map;

import quirkle.game.gameplay.tiles.Tile;

/** Walks contiguous streaks of occupied positions on the board along a single direction. */
final class StreakWalker {

    private StreakWalker() {}

    /** @return the outermost position reachable from {@code start} by repeatedly stepping {@code direction} through {@code tiles}. */
    static Position walkToStreakEnd(Map<Position, Tile> tiles, Position start, Direction direction) {
        Position current = start;
        while (tiles.containsKey(current.neighbor(direction))) {
            current = current.neighbor(direction);
        }
        return current;
    }
}
