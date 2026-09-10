package quirkle.game.debug;

import quirkle.game.gamePlay.Game;
import quirkle.game.gamePlay.board.Board;
import quirkle.game.gamePlay.board.Position;
import quirkle.game.gamePlay.player.Player;
import quirkle.game.gamePlay.tiles.Tile;
import quirkle.game.gamePlay.tiles.TileColor;
import quirkle.game.gamePlay.tiles.TileSymbol;

import java.util.List;

/**
 * Reads and formats the game's own types on the debug console.
 *
 * @note Sits between {@link DebugConsole}, which knows only lines and numbers, and
 *       {@link DebugMode}, which only ever wants a tile, a position or a player. Keeping the two
 *       apart leaves the console reusable and makes every debug action ask for a tile the same
 *       way, so the prompts stay recognizable no matter which action is running.
 */
public final class DebugPrompts {

    /** Drawn in place of a tile that is not there. */
    private static final String NO_TILE = "-";

    private DebugPrompts() {}

    /**
     * Asks for a tile as its two-letter code, e.g. {@code "RQ"} for a red square - the same
     * shorthand {@link #shortFormat} prints for a rack, so what gets typed matches what was just
     * shown on screen instead of stepping through a color menu and then a symbol menu.
     *
     * @param what what the tile is being asked for, used as the prompt's heading
     * @note Builds a new tile rather than looking one up: the debug mode is allowed to conjure a
     *       tile that is in no bag and on no rack, which is the point of being able to set a
     *       position up by hand.
     */
    public static Tile readTile(String what) {
        DebugConsole.println(what + ":");
        DebugConsole.println("  colors: " + colorLegend());
        DebugConsole.println("  symbols: " + symbolLegend());

        while (true) {
            String input = DebugConsole.readLine("  tile (e.g. RQ)").toUpperCase();

            if (input.length() == 2) {
                TileColor color = colorFromLetter(input.charAt(0));
                TileSymbol symbol = symbolFromLetter(input.charAt(1));
                if (color != null && symbol != null) return new Tile(color, symbol);
            }

            DebugConsole.println("  '" + input + "' is not a color+symbol code, e.g. RQ.");
        }
    }

    /** Column/row width every {@link #formatBoard} cell is padded to. */
    private static final int BOARD_CELL_WIDTH = 4;

    /**
     * @return the board as a grid of {@link #shortFormat} codes, x across the top and y down the
     *         side, padded one cell past the outermost placed tile on every side so an empty
     *         neighbor's coordinates are visible too
     * @note Read through {@link Board#getTileAt}, which favors a pending tile over a committed
     *       one at the same position - handing in a board built from tiles entered earlier in the
     *       same batch (see {@link DebugMode#placeTiles()}) is what makes those show up as well.
     */
    public static String formatBoard(Board board) {
        int minX = board.minX() - 1;
        int maxX = board.maxX() + 1;
        int minY = board.minY() - 1;
        int maxY = board.maxY() + 1;

        StringBuilder out = new StringBuilder(boardCell(""));
        for (int x = minX; x <= maxX; x++) {
            out.append(boardCell(String.valueOf(x)));
        }

        for (int y = minY; y <= maxY; y++) {
            out.append(System.lineSeparator()).append(boardCell(String.valueOf(y)));
            for (int x = minX; x <= maxX; x++) {
                out.append(boardCell(shortFormat(board.getTileAt(new Position(x, y)))));
            }
        }

        return out.toString();
    }

    private static String boardCell(String text) {
        return String.format("%" + BOARD_CELL_WIDTH + "s", text);
    }

    /**
     * Asks for the two coordinates of a board cell.
     *
     * @param what what the position is being asked for, used as the prompt's heading
     * @note Unbounded on purpose: the board is a sparse grid without edges, so any pair of
     *       coordinates names a cell that could hold a tile.
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
     * @note Returns the index rather than the player, because that is what the game's own
     *       setters take; the caller that wants the player looks it up.
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
     * @return the tile as two letters, e.g. {@code "RC"} for a red circle
     * @note For a rack, where six tiles share one line. Colors are told apart by their initial,
     *       symbols are not - {@code SQUARE} and {@code STAR} share one - so the symbol's letter
     *       is assigned rather than taken.
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

    /** @return every color's letter and name, e.g. {@code "R=RED  Y=YELLOW  ..."}. */
    private static String colorLegend() {
        StringBuilder legend = new StringBuilder();
        for (TileColor color : TileColor.values()) {
            if (legend.length() > 0) legend.append("  ");
            legend.append(color.name().charAt(0)).append("=").append(color.name());
        }
        return legend.toString();
    }

    /** @return every symbol's letter and name, under the same letters as {@link #symbolLetter}. */
    private static String symbolLegend() {
        StringBuilder legend = new StringBuilder();
        for (TileSymbol symbol : TileSymbol.values()) {
            if (legend.length() > 0) legend.append("  ");
            legend.append(symbolLetter(symbol)).append("=").append(symbol.name());
        }
        return legend.toString();
    }

    /** @return the color {@code letter} names, or {@code null} if it names none. */
    private static TileColor colorFromLetter(char letter) {
        for (TileColor color : TileColor.values()) {
            if (color.name().charAt(0) == letter) return color;
        }
        return null;
    }

    /** @return the symbol {@code letter} names under {@link #symbolLetter}, or {@code null}. */
    private static TileSymbol symbolFromLetter(char letter) {
        for (TileSymbol symbol : TileSymbol.values()) {
            if (symbolLetter(symbol) == letter) return symbol;
        }
        return null;
    }
}
