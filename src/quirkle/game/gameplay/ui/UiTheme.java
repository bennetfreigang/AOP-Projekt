package quirkle.game.gameplay.ui;

import quirkle.engine.AssetManager;
import quirkle.engine.EngineConfig;

import java.awt.Color;
import java.awt.Rectangle;

public final class UiTheme {

    private UiTheme() {}

    // Render layers, applied to Entity#renderOrder

    public static final int LAYER_GRID = -100;
    public static final int LAYER_TILE = 0;
    public static final int LAYER_FRAME = 50;
    public static final int LAYER_HUD = 100;

    // Fonts

    public static final String FONT = "poly_regular";
    /** Brush font for the two big numbers: the score on the card and the count in the bag */
    public static final String FONT_SCORE = "higher_jump";

    public static final float FONT_SIZE_BUTTON = 24f;
    public static final float FONT_SIZE_BAG_COUNT = 72f;
    public static final float FONT_SIZE_CARD_SCORE = 64f;
    public static final float FONT_SIZE_TURN_CURRENT = 54f;
    public static final float FONT_SIZE_TURN_NEXT = 32f;

    // Colors

    public static final Color BACKGROUND = new Color(8, 8, 10);
    public static final Color TEXT = new Color(245, 245, 245);
    public static final Color TEXT_DIMMED = new Color(245, 245, 245, 120);
    public static final Color GOLD = new Color(198, 154, 58);

    public static final Color GRID_LINE = new Color(255, 255, 255, 34);
    public static final Color GRID_MARKER = new Color(255, 255, 255, 60);
    public static final Color GRID_CURSOR = new Color(255, 255, 255, 140);

    public static final Color PLACEHOLDER_FILL = new Color(255, 255, 255, 22);
    public static final Color PLACEHOLDER_BORDER = new Color(255, 255, 255, 60);

    // Nine-slice frame assets

    public static final int CONTAINER_SOURCE_INSET = 130;

    public static final int PANEL_BORDER = 14;

    public static final float CURSOR_STROKE = 3f;

    public static final String SPRITE_GOLDEN_FRAME = "gameplay/ui/goldenframe";
    public static final String SPRITE_PLAYER_CARD = "gameplay/ui/player";
    public static final String FRAME_SLOT_STRIP = "gameplay/ui/cardframes";

    public static final String FRAME_BOARD = "gameplay/ui/frame";

    // Layout: the frame the board is seen through

    private static final double FRAME_SOURCE_WIDTH = 1920.0;
    private static final double FRAME_SOURCE_HEIGHT = 1080.0;

    private static final double FRAME_OPENING_LEFT = 223.0;
    private static final double FRAME_OPENING_TOP = 195.0;
    private static final double FRAME_OPENING_RIGHT = 1686.0;
    private static final double FRAME_OPENING_BOTTOM = 889.0;

    public static Rectangle boardViewport() {
        double scaleX = EngineConfig.WINDOW_WIDTH / FRAME_SOURCE_WIDTH;
        double scaleY = EngineConfig.WINDOW_HEIGHT / FRAME_SOURCE_HEIGHT;

        int left = (int) Math.round(FRAME_OPENING_LEFT * scaleX);
        int top = (int) Math.round(FRAME_OPENING_TOP * scaleY);
        int right = (int) Math.round(FRAME_OPENING_RIGHT * scaleX);
        int bottom = (int) Math.round(FRAME_OPENING_BOTTOM * scaleY);

        return new Rectangle(left, top, right - left, bottom - top);
    }

    // Layout: the turn order along the top edge

    public static final int TURN_TOP = 44;
    public static final int TURN_MARGIN_X = 170;

    /** Gap behind the current player's name, before the ones still to come */
    public static final int TURN_CURRENT_GAP = 52;
    public static final int TURN_NAME_GAP = 30;

    public static int turnHiddenTop() {
        return -(int) (TURN_TOP + 2 * FONT_SIZE_TURN_CURRENT);
    }

    // Layout: the board itself
    public static final int BOARD_BASE_CELL_SIZE = 64;

    // Layout: tiles, both on the board and in the rack
    public static final double TILE_PADDING_RATIO = 0.09;

    // Layout: the active player's card in the bottom left corner

    public static final int CARD_LEFT = 40;
    public static final int CARD_BOTTOM_MARGIN = 40;
    public static final int CARD_WIDTH = 210;
    public static final int CARD_HEIGHT = 300;
    public static final int CARD_SCORE_CENTER_Y = 190;

    public static int cardTop() {
        return EngineConfig.WINDOW_HEIGHT - CARD_BOTTOM_MARGIN - CARD_HEIGHT;
    }

    /** @return the top edge the card is parked at while it is off screen */
    public static int cardHiddenTop() {
        return EngineConfig.WINDOW_HEIGHT + 20;
    }

    // Layout: tile bag counter, bottom right

    public static final int BAG_SIZE = 217;
    public static final int BAG_MARGIN = 148;

    public static int bagCenterX() {
        return EngineConfig.WINDOW_WIDTH - BAG_MARGIN;
    }

    public static int bagCenterY() {
        return EngineConfig.WINDOW_HEIGHT - BAG_MARGIN;
    }

    // Layout: the active player's rack along the bottom

    public static final int RACK_TILE_SIZE = 108;
    public static final int RACK_TILE_SPACING = 142;

    /** Height of the cardframes backdrop; kept apart from the tile size so one can grow alone */
    public static final int RACK_FRAME_HEIGHT = 168;

    public static final double HANDOVER_PHASE_SECONDS = 0.2;

    public static int rackHiddenCenterY() {
        return EngineConfig.WINDOW_HEIGHT + RACK_TILE_SIZE;
    }

    // Layout: side buttons

    public static final int SIDE_BUTTON_WIDTH = 270;
    public static final int SIDE_BUTTON_HEIGHT = 90;
    public static final int GLYPH_SIZE = 44;

    public static String text(String key) {
        return AssetManager.getMessage(key);
    }
}
