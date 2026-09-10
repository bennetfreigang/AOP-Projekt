package quirkle.game.debug;

import java.util.Arrays;
import java.util.List;

public enum DebugAction {

    PLACE_TILES(Group.BOARD, "Place tiles", DebugMode::placeTiles),

    SET_HAND_TILE(Group.HAND, "Set hand tile", DebugMode::setHandTile),

    CLEAR_BAG(Group.BAG, "Clear bag", DebugMode::clearBag),
    STACK_TILE_ON_BAG(Group.BAG, "Stack next tile", DebugMode::stackTileOnBag),

    PRINT_SCORES(Group.TURN, "Print scores", DebugMode::printScores),
    SET_STARTING_PLAYER(Group.TURN, "Set starting player", DebugMode::setCurrentPlayer);

    /** What part of the game an action reaches into; one section of the debug panel each. */
    public enum Group {
        BOARD("Board"),
        HAND("Hands"),
        BAG("Tile bag"),
        TURN("Turns");

        private final String label;

        Group(String label) {
            this.label = label;
        }

        /** @return the section's heading. */
        public String getLabel() {
            return label;
        }
    }

    private final Group group;
    private final String label;
    private final Runnable action;

    DebugAction(Group group, String label, Runnable action) {
        this.group = group;
        this.label = label;
        this.action = action;
    }

    public Group getGroup() {
        return group;
    }

    /** @return the button's caption. */
    public String getLabel() {
        return label;
    }

    /**
     * Runs the action.
     *
     * @note Safe to call from a button without catching anything: every action wraps itself, so a
     *       rack slot that does not exist ends as a line on the console rather than a broken frame.
     */
    public void run() {
        action.run();
    }

    /** @return the actions in {@code group}, in the order the panel should list them. */
    public static List<DebugAction> of(Group group) {
        return Arrays.stream(values()).filter(action -> action.group == group).toList();
    }
}
