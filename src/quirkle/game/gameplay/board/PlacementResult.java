package quirkle.game.gameplay.board;

/**
 * The outcome of checking a placement: either legal, or the one rule that rejected it.
 */
public enum PlacementResult {
    LEGAL("legal"),
    POSITION_OCCUPIED("the position already holds a tile"),
    NOT_IN_ONE_LINE("the tiles placed this turn are not all in one row or column"),
    LINE_HAS_GAP("the tiles placed this turn leave a gap in their line"),
    NOT_CONNECTED_TO_BOARD("the tiles do not touch any tile on the board"),
    INVALID_LINE("a line would repeat a tile, or mix colors and symbols");

    private final String description;

    PlacementResult(String description) {
        this.description = description;
    }

    public boolean isLegal() {
        return this == LEGAL;
    }

    /** @return a sentence naming the rule, for an error message or a debug readout. */
    public String getDescription() {
        return description;
    }
}