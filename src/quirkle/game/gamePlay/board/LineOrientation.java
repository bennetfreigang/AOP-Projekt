package quirkle.game.gamePlay.board;

import java.util.function.ToIntFunction;

/**
 * Orientation a line can run in, with the data needed to find tiles on that line and measure
 * its length.
 * <p>
 * {@code fixedCoordinate} is the coordinate shared by every tile on the line (used to group
 * pending tiles by the line they belong to); {@code extentCoordinate} is the coordinate that
 * varies along the line (used to walk it and measure its length).
 */
public enum LineOrientation {
    HORIZONTAL(Position::y, Position::x, Direction.WEST, Direction.EAST),
    VERTICAL(Position::x, Position::y, Direction.NORTH, Direction.SOUTH);

    final ToIntFunction<Position> fixedCoordinate;
    final ToIntFunction<Position> extentCoordinate;
    final Direction backwardDirection;
    final Direction forwardDirection;

    LineOrientation(ToIntFunction<Position> fixedCoordinate, ToIntFunction<Position> extentCoordinate,
                    Direction backwardDirection, Direction forwardDirection) {
        this.fixedCoordinate = fixedCoordinate;
        this.extentCoordinate = extentCoordinate;
        this.backwardDirection = backwardDirection;
        this.forwardDirection = forwardDirection;
    }
}