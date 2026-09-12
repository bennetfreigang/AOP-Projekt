package quirkle.game.debug;

import quirkle.game.gameplay.Game;
import quirkle.game.gameplay.board.Position;
import quirkle.game.gameplay.player.Player;
import quirkle.game.gameplay.tiles.Tile;
import quirkle.game.gameplay.tiles.TileColor;
import quirkle.game.gameplay.tiles.TileSymbol;

import java.util.List;

/**
 * Reads and formats the game's own types on the debug console.
 *
 * @note Sits between {@link DebugConsole}, which knows only lines and numbers, and
 *       {@link DebugMode}, which wants tiles, positions and players. Every action therefore asks
 *       for a tile the same way.
 */
public final class DebugPrompts {

    /** Drawn in place of a tile that is not there. */
    private static final String NO_TILE = "-";

    private DebugPrompts() {}

    /**
     * Asks for a color and a symbol and builds the tile they name.
     *
     * @param what what the tile is being asked for, used as the prompt's heading
     * @note Builds a new tile rather than looking one up, so the debug mode can conjure one that is
     *       in no bag and on no rack.
     */
    public static Tile readTile(String what) {
        DebugConsole.println(what + ":");
        TileColor color = DebugConsole.readEnum("  color", TileColor.values());
        TileSymbol symbol = DebugConsole.readEnum("  symbol", TileSymbol.values());
        return new Tile(color, symbol);
    }

    /**
     * Asks for the two coordinates of a board cell.
     *
     * @param what what the position is being asked for, used as the prompt's heading
     * @note Unbounded on purpose: the board is a sparse grid without edges.
     */
    public static Position readPosition(String what) {
        DebugConsole.println(what + ":");
        int x = DebugConsole.readInt("  x");
        int y = DebugConsole.readInt("  y");
        return new Position(x, y);
    }

    /**
     * Lists the game's players and asks which one is meant.
     *
     * @return the player's index in {@link Game#getPlayers()}
     * @note The index rather than the player, since that is what the game's own setters take.
     */
    public static int readPlayerIndex(Game game, String what) {
        List<Player> players = game.getPlayers();

        DebugConsole.println(what + ":");
        for (int i = 0; i < players.size(); i++) {
            String marker = i == game.getCurrentPlayerIndex() ? ">" : " ";
            DebugConsole.println("  " + marker + " [" + i + "] " + players.get(i).getName());
        }
        return DebugConsole.readInt("  choice", 0, players.size() - 1);
    }

    /** @return the tile spelled out, e.g. {@code "RED CIRCLE"}, or a dash for {@code null}. */
    public static String format(Tile tile) {
        if (tile == null) return NO_TILE;
        return tile.getColor().name() + " " + tile.getSymbol().name();
    }

    /**
     * @return the tile as two letters, e.g. {@code "RC"} for a red circle, or a dash for {@code null}
     * @note For racks and boards, where tiles share a line. Colors use their initial; symbols
     *       cannot, since {@code SQUARE} and {@code STAR} share one - see {@link #symbolLetter}.
     */
    public static String shortFormat(Tile tile) {
        if (tile == null) return NO_TILE;
        return "" + tile.getColor().name().charAt(0) + symbolLetter(tile.getSymbol());
    }

    /** @return the hand with its slot numbers, as one line, or a note that it is empty. */
    public static String formatHand(List<Tile> hand) {
        if (hand.isEmpty()) return "(empty)";

        StringBuilder line = new StringBuilder();
        for (int slot = 0; slot < hand.size(); slot++) {
            if (slot > 0) line.append("  ");
            line.append("[").append(slot).append("] ").append(shortFormat(hand.get(slot)));
        }
        return line.toString();
    }

    private static char symbolLetter(TileSymbol symbol) {
        return switch (symbol) {
            case CIRCLE -> 'C';
            case SQUARE -> 'Q';
            case DIAMOND -> 'D';
            case HEXAGON -> 'H';
            case STAR -> 'S';
            case CROSS -> 'X';
        };
    }
}
