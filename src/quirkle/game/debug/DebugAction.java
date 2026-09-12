package quirkle.game.debug;

import java.util.Arrays;
import java.util.List;

/**
 * The actions the debug panel offers, each a caption plus the {@link DebugMode} call behind it.
 *
 * @note Declaration order is the order the panel lists them in, per {@link Group}.
 */
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

        /** @param label the section's heading in the panel */
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

    /** @param action the {@link DebugMode} entry point this button triggers */
    DebugAction(Group group, String label, Runnable action) {
        this.group = group;
        this.label = label;
        this.action = action;
    }

    /** @return the panel section this action belongs to. */
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
     * @note Safe to call from a button without catching anything; {@link DebugMode} turns every
     *       failure into a console line rather than letting it out.
     */
    public void run() {
        action.run();
    }

    /** @return the actions in {@code group}, in the order the panel should list them. */
    public static List<DebugAction> of(Group group) {
        return Arrays.stream(values()).filter(action -> action.group == group).toList();
    }
}
