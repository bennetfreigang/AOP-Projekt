package quirkle.game.debug;

import quirkle.engine.EngineConfig;
import quirkle.game.gameplay.Game;
import quirkle.game.gameplay.board.Board;
import quirkle.game.gameplay.board.PlacementResult;
import quirkle.game.gameplay.board.Position;
import quirkle.game.gameplay.player.Player;
import quirkle.game.gameplay.tiles.Tile;
import quirkle.game.gameplay.tiles.TileBag;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The debug actions themselves: each one reads its input from {@link DebugConsole} and applies it
 * to the attached {@link Game}.
 *
 * @note Holds the running game statically, so the panel's buttons can reach it without being
 *       handed one; {@link #attach} and {@link #detach} bracket a game's lifetime.
 * @note Actions bypass the rules on purpose - a tile can be conjured, the bag emptied, the turn
 *       handed on - so nothing here is reachable from normal play.
 */
public final class DebugMode {

    private static Game game;

    private DebugMode() {}

    /** Points the debug actions at {@code currentGame}, replacing any game attached before. */
    public static void attach(Game currentGame) {
        game = currentGame;
        EngineConfig.message("attached to a game", DebugMode.class.getSimpleName(), EngineConfig.messageType.INFO);
    }

    /** Drops the attached game, leaving every action to abort until the next {@link #attach}. */
    public static void detach() {
        game = null;
        EngineConfig.message("detached", DebugMode.class.getSimpleName(), EngineConfig.messageType.INFO);
    }

    /** @return whether an action would find a game to work on; the panel greys its buttons out by this. */
    public static boolean hasGame() {
        return game != null;
    }

    /**
     * Places a whole move of freely chosen tiles on the board, entered cell by cell.
     *
     * @note Shows the board after every tile and re-asks for a cell that was already entered, so a
     *       multi-tile move can be built up and corrected before anything is staged.
     * @note The move is validated as a whole and its score printed; an illegal one is reported and
     *       dropped rather than placed.
     */
    public static void placeTiles() {
        run("PLACE TILES", () -> {
            Board board = requireGame().getBoard();

            int count = DebugConsole.readInt("  how many tiles", 1, Player.HAND_SIZE);

            Map<Position, Tile> move = new LinkedHashMap<>();
            for (int i = 0; i < count; i++) {
                println("tile " + (i + 1) + " of " + count + ":");
                println(DebugPrompts.formatBoard(new Board(board.getPlacedTiles(), move)));

                Position position = DebugPrompts.readPosition("  cell");
                Tile tile = DebugPrompts.readTile("  tile for " + describe(position));

                if (move.put(position, tile) != null) {
                    println("  replaced the tile entered for " + describe(position) + " earlier");
                    i--;
                }
            }

            Board testBoard = new Board(board.getPlacedTiles(), move);
            PlacementResult result = testBoard.checkPendingPlacement();

            DebugConsole.printSeparator();
            if (!result.isLegal()) {
                println("illegal move: " + result.getDescription());
                return;
            }
            println("legal move   -   " + testBoard.getPendingScore() + " points");

            board.placeTiles(move);
        });
    }

    /**
     * Puts a tile of the tester's choosing onto a rack.
     *
     * @note Replaces rather than adds while the rack holds tiles, so the hand keeps its size; the
     *       tile that made way is dropped, not returned to the bag. An empty rack is filled instead.
     */
    public static void setHandTile() {
        run("SET HAND TILE", () -> {
            Game current = requireGame();

            Player player = current.getPlayers().get(DebugPrompts.readPlayerIndex(current, "rack"));

            if (player.getHand().isEmpty()) {
                int count = DebugConsole.readInt("  how many tiles", 1, Player.HAND_SIZE);

                for (int i = 0; i < count; i++) {
                    println("tile " + (i + 1) + " of " + count + ":");
                    println(player.getName() + ": " + DebugPrompts.formatHand(player.getHand()));

                    Tile tile = DebugPrompts.readTile("new tile for the empty rack");
                    player.addTile(tile);
                    println("added " + DebugPrompts.format(tile));
                }
                return;
            }

            println(player.getName() + ": " + DebugPrompts.formatHand(player.getHand()));
            int count = DebugConsole.readInt("  how many slots to change", 1, player.getHand().size());

            for (int i = 0; i < count; i++) {
                println("slot " + (i + 1) + " of " + count + ":");
                println(player.getName() + ": " + DebugPrompts.formatHand(player.getHand()));

                int slot = DebugConsole.readInt("  slot", 0, player.getHand().size() - 1);
                Tile tile = DebugPrompts.readTile("tile for slot " + slot);

                Tile replaced = player.replaceTile(slot, tile);
                println("slot " + slot + ": " + DebugPrompts.format(replaced) + " -> " + DebugPrompts.format(tile));
            }
        });
    }

    /**
     * Puts a tile of the tester's choosing on top of the bag, so it is the next one drawn.
     *
     * @note The only way to steer what a player draws at the end of their turn. With
     *       {@link #clearBag()} it also composes a draw pile from scratch: empty, then stack in
     *       reverse order.
     */
    public static void stackTileOnBag() {
        run("STACK TILE ON BAG", () -> {
            TileBag bag = requireGame().getTileBag();

            Tile tile = DebugPrompts.readTile("next tile for the bag");
            bag.putOnTop(tile);

            println("next draw: " + DebugPrompts.format(tile) + "   (" + bag.getSize() + " tiles in the bag)");
        });
    }

    /**
     * Empties the bag.
     *
     * @note Asks for confirmation first. An empty bag is how the endgame is tested: refilling a
     *       rack then leaves it short.
     */
    public static void clearBag() {
        run("CLEAR BAG", () -> {
            TileBag bag = requireGame().getTileBag();

            if (!DebugConsole.readYesNo("throw away the " + bag.getSize() + " tiles left in the bag?")) {
                println("kept the bag as it is");
                return;
            }

            bag.clear();
            println("bag emptied");
        });
    }

    /** Prints the scoreboard: score, last turn's points and rack size for every player. */
    public static void printScores() {
        run("SCORES", () -> {
            Game current = requireGame();

            println("Turn " + current.getTurnNumber() + "   -   tiles left in bag: " + current.getTileBag().getSize());
            DebugConsole.printSeparator();
            println(String.format("%-2s %-14s %7s %11s %6s", "", "PLAYER", "SCORE", "LAST TURN", "HAND"));

            List<Player> players = current.getPlayers();
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                String marker = i == current.getCurrentPlayerIndex() ? ">" : " ";

                println(String.format("%-2s %-14s %7d %11d %6d",
                        marker,
                        player.getName(),
                        player.getScore(),
                        player.getLastRoundScore(),
                        player.getHand().size()));
            }
        });
    }

    /**
     * Hands the turn to any player - before the first move, that is who starts.
     *
     * @note Scores nothing and leaves the turn number alone, which is what makes it the way to
     *       reach another player's rack mid-turn.
     */
    public static void setCurrentPlayer() {
        run("SET STARTING PLAYER", () -> {
            Game current = requireGame();

            current.setCurrentPlayerIndex(DebugPrompts.readPlayerIndex(current, "player to move"));
            println(current.getCurrentPlayer().getName() + " is to move");
        });
    }

    /** @return {@code position} as {@code "(x, y)"}. */
    private static String describe(Position position) {
        return "(" + position.x() + ", " + position.y() + ")";
    }

    /**
     * @return the attached game
     * @throws IllegalStateException if none is attached; {@link #run} turns it into a console line
     */
    private static Game requireGame() {
        if (game == null) {
            throw new IllegalStateException("no game running; start a game first");
        }
        return game;
    }

    /**
     * Runs one action between its heading and a blank line, and reports rather than propagates
     * whatever it throws.
     *
     * @note Actions run off a button in a live frame, where an escaping exception would take the
     *       game down over something as ordinary as a rack slot that does not exist.
     */
    private static void run(String title, Runnable action) {
        DebugConsole.printHeading(title);
        try {
            action.run();
        } catch (RuntimeException e) {
            println("aborted: " + e.getMessage());
        }
        DebugConsole.printBlank();
    }

    private static void println(String line) {
        DebugConsole.println(line);
    }
}
