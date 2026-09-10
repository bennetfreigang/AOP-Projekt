package quirkle.game.debug;

import quirkle.engine.EngineConfig;
import quirkle.game.gamePlay.Game;
import quirkle.game.gamePlay.board.Board;
import quirkle.game.gamePlay.board.PlacementResult;
import quirkle.game.gamePlay.board.Position;
import quirkle.game.gamePlay.player.Player;
import quirkle.game.gamePlay.tiles.Tile;
import quirkle.game.gamePlay.tiles.TileBag;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DebugMode {

    private static Game game;

    private DebugMode() {}

    public static void attach(Game currentGame) {
        game = currentGame;
        EngineConfig.message("attached to a game", DebugMode.class.getSimpleName(), EngineConfig.messageType.INFO);
    }

    public static void detach() {
        game = null;
        EngineConfig.message("detached", DebugMode.class.getSimpleName(), EngineConfig.messageType.INFO);
    }

    /** @return whether an action would find a game to work on; what the UI greys its buttons out by. */
    public static boolean hasGame() {
        return game != null;
    }

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
     * @note Replaces rather than adds while the rack still holds tiles, so the hand keeps the size
     *       the rules give it; the tile that made way is dropped rather than returned to the bag,
     *       since it was never drawn from anywhere the debug mode could put it back into.
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
     * @note The way to decide what a player draws at the end of their turn, which is otherwise the
     *       one part of a turn nothing can steer. Together with {@link #clearBag()} it is also how
     *       a draw pile is composed from scratch: empty it, then stack the tiles in reverse order.
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
     * @note Where composing a draw pile by hand starts, and what the end of the game is tested
     *       with: once the bag is empty, refilling a rack leaves it short.
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
     * @note Scores nothing and does not advance the turn number; it only changes who is to move,
     *       which is what makes it the way to reach another player's rack mid-turn.
     */
    public static void setCurrentPlayer() {
        run("SET STARTING PLAYER", () -> {
            Game current = requireGame();

            current.setCurrentPlayerIndex(DebugPrompts.readPlayerIndex(current, "player to move"));
            println(current.getCurrentPlayer().getName() + " is to move");
        });
    }

    private static String describe(Position position) {
        return "(" + position.x() + ", " + position.y() + ")";
    }

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
     * @note An action is reached from a button in a running game: letting an exception out would
     *       take the frame it was clicked in down with it, over something as ordinary as a rack
     *       slot that does not exist. The message is what the tester came for anyway.
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
