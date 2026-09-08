package quirkle.game.debug;

import quirkle.engine.EngineConfig;
import quirkle.game.gamePlay.Game;
import quirkle.game.gamePlay.player.Player;

import java.util.List;

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

    public static boolean hasGame() {
        return game != null;
    }

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

    private static Game requireGame() {
        if (game == null) {
            throw new IllegalStateException("no game running; start a game first");
        }
        return game;
    }

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
