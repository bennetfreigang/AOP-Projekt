package quirkle.game.endgame.scenes;

import quirkle.engine.AssetManager;
import quirkle.engine.Entity;
import quirkle.engine.Scene;
import quirkle.engine.SceneManager;
import quirkle.game.gameplay.Game;
import quirkle.game.gameplay.board.Board;
import quirkle.game.gameplay.player.Player;
import quirkle.game.gameplay.tiles.TileBag;
import quirkle.game.startmenu.scenes.StartMenuScene;
import quirkle.game.util.RectangularButton;
import quirkle.game.util.SimpleSpriteEntity;
import quirkle.game.util.SimpleTextEntity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Comparator;
import java.util.List;

/**
 * The screen shown once the game has run out: the winner on a brush stroke, the rest of the field
 * beneath them, and the way back to the menu.
 */
public class EndGameScene extends Scene {

    private static final String BACKGROUND = "gameplay/ui/vignette";
    private static final String BRUSH = "gameplay/ui/brush";

    private static final String DISPLAY_FONT = "higher_jump";
    private static final String LIST_FONT = "poly_regular";

    private static final Color WINNER_COLOR = Color.WHITE;
    private static final Color WINNER_SCORE_COLOR = new Color(214, 170, 74);
    private static final Color LIST_COLOR = new Color(245, 245, 245, 170);

    /**
     * Painted under the vignette, which carries no color of its own.
     *
     * @note vignette.png is pure black at varying alpha - a mask, not a picture. Over nothing it
     *       would be a black screen; over this it darkens towards the edges and leaves the middle,
     *       where the brush sits, the lightest part of the screen.
     */
    private static final Color BACKDROP = new Color(88, 88, 92);

    private static final float WINNER_FONT_SIZE = 116f;
    private static final float WINNER_SCORE_FONT_SIZE = 62f;
    private static final float LIST_FONT_SIZE = 40f;

    /** Drawn width of the brush; its height follows from the sprite's own proportions */
    private static final int BRUSH_WIDTH = 1120;

    private static final int BRUSH_CENTER_Y = 400;

    /** The name sits a little above the brush's middle, the score below it */
    private static final int NAME_RISE = 34;
    private static final int SCORE_DROP = 150;

    private static final int LIST_TOP = 700;
    private static final int LIST_LINE_HEIGHT = 62;

    /** Half the space kept between a listed name and its score, either side of the centre */
    private static final int LIST_COLUMN_GAP = 30;

    private static final int BUTTON_GAP = 120;

    private final RectangularButton returnButton;

// --- DEBUG ---
//    public EndGameScene() {
//        this(debugGame());
//    }
//
//    /** @return a game that looks finished: players with scores on it, nothing on the board */
//    private static Game debugGame() {
//        return new Game(new Board(), new TileBag(), List.of(
//                scored("Bennet", 141),
//                scored("Alex", 137),
//                scored("Jarosch", 112),
//                scored("Kris", 98)));
//    }
//
//    private static Player scored(String name, int score) {
//        Player player = new Player(name);
//        player.increaseScore(score);
//        return player;
//    }

    public EndGameScene(Game game) {
        // Asked rather than worked out here, so the rule for a draw lives in one place only.
        Player winner = game.getWinner();

        addEntities(new SimpleSpriteEntity(BACKGROUND, Entity.OriginPresets.TOP_LEFT));

        addBrush();
        addWinner(winner);

        int listBottom = addOtherPlayers(winner, game.getPlayers());

        this.returnButton = new RectangularButton(AssetManager.getMessage("return"));
        addEntities(returnButton);

        returnButton.x = getCenterX();
        returnButton.y = listBottom + BUTTON_GAP;
    }

    @Override
    public void onRender(Graphics2D g) {
        g.setColor(BACKDROP);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void onTick(double dt) {
        if (returnButton.isClicked()) SceneManager.setScene(new StartMenuScene());
    }

    private void addBrush() {
        SimpleSpriteEntity brush = new SimpleSpriteEntity(BRUSH, Entity.OriginPresets.CENTER);

        // Scaled off its own width so the stroke keeps its proportions whatever the sprite measures.
        brush.scale = BRUSH_WIDTH / (double) AssetManager.getTexture(BRUSH).getWidth();
        brush.x = getCenterX();
        brush.y = BRUSH_CENTER_Y;

        addEntities(brush);
    }

    private void addWinner(Player winner) {
        SimpleTextEntity name = new SimpleTextEntity(winner.getName(), WINNER_FONT_SIZE,
                DISPLAY_FONT, WINNER_COLOR, Entity.OriginPresets.CENTER);

        SimpleTextEntity score = new SimpleTextEntity(String.valueOf(winner.getScore()),
                WINNER_SCORE_FONT_SIZE, DISPLAY_FONT, WINNER_SCORE_COLOR, Entity.OriginPresets.CENTER);

        addEntities(name, score);

        name.x = getCenterX();
        name.y = BRUSH_CENTER_Y - NAME_RISE;

        score.x = getCenterX();
        score.y = BRUSH_CENTER_Y + SCORE_DROP;
    }

    private int addOtherPlayers(Player winner, List<Player> players) {
        List<Player> others = players.stream()
                .filter(player -> player != winner)
                .sorted(Comparator.comparingInt(Player::getScore).reversed())
                .toList();

        int y = LIST_TOP;

        for (Player player : others) {
            addEntities(new PlayerRow(player, getCenterX(), y));
            y += LIST_LINE_HEIGHT;
        }

        return others.isEmpty() ? LIST_TOP : y - LIST_LINE_HEIGHT;
    }

    private static final class PlayerRow extends Entity {

        private final String name;
        private final String score;

        PlayerRow(Player player, int centerX, int centerY) {
            this.name = player.getName();
            this.score = String.valueOf(player.getScore());
            this.x = centerX;
            this.y = centerY;
        }

        @Override
        public void onRender(Graphics2D g) {
            drawText(name, LIST_FONT_SIZE, LIST_COLOR, LIST_FONT,
                    x - LIST_COLUMN_GAP, y, 0.0, OriginPresets.CENTER_RIGHT, g);

            drawText(score, LIST_FONT_SIZE, LIST_COLOR, LIST_FONT,
                    x + LIST_COLUMN_GAP, y, 0.0, OriginPresets.CENTER_LEFT, g);
        }
    }
}
