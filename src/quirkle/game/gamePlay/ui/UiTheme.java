package quirkle.game.gamePlay.ui;

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

    public static final String FONT = "DEBUG_Poly-Regular";

    public static final float FONT_SIZE_CARD = 19f;
    public static final float FONT_SIZE_BUTTON = 24f;
    public static final float FONT_SIZE_TURN_NAME = 34f;
    public static final float FONT_SIZE_TURN_SCORE = 22f;
    public static final float FONT_SIZE_TURN_STATUS = 20f;
    public static final float FONT_SIZE_BAG_COUNT = 72f;
    public static final float FONT_SIZE_BAG_LABEL = 20f;

    // Colors

    public static final Color BACKGROUND = new Color(8, 8, 10);
    public static final Color TEXT = new Color(245, 245, 245);
    public static final Color TEXT_DIMMED = new Color(245, 245, 245, 120);
    public static final Color GOLD = new Color(198, 154, 58);

    public static final Color GRID_LINE = new Color(255, 255, 255, 34);
    public static final Color GRID_MARKER = new Color(255, 255, 255, 60);

    public static final Color SLOT_FILL = new Color(255, 255, 255, 14);

    public static final Color PLACEHOLDER_FILL = new Color(255, 255, 255, 22);
    public static final Color PLACEHOLDER_BORDER = new Color(255, 255, 255, 60);

    // Nine-slice frame assets

    public static final int CONTAINER_SOURCE_INSET = 130;

    public static final int PANEL_BORDER = 14;

    public static final int SLOT_BORDER = 6;

    public static final int CURSOR_BORDER = 8;

    public static final String FRAME_PLAYER_CARD = "Ui/container_02";
    public static final String FRAME_SLOT = "Ui/container_03";
    public static final String FRAME_CURSOR = "Ui/container_02";
    public static final String SPRITE_GOLDEN_FRAME = "Ui/goldenframe";

    public static final String FRAME_BOARD = "Ui/frame";

    // Layout: the frame the board is seen through

    private static final double FRAME_SOURCE_WIDTH = 4608.0;
    private static final double FRAME_SOURCE_HEIGHT = 2592.0;

    private static final double FRAME_OPENING_LEFT = 716.0;
    private static final double FRAME_OPENING_TOP = 436.0;
    private static final double FRAME_OPENING_RIGHT = 3891.0;
    private static final double FRAME_OPENING_BOTTOM = 2157.0;

    public static Rectangle boardViewport() {
        double scaleX = EngineConfig.WINDOW_WIDTH / FRAME_SOURCE_WIDTH;
        double scaleY = EngineConfig.WINDOW_HEIGHT / FRAME_SOURCE_HEIGHT;

        int left = (int) Math.round(FRAME_OPENING_LEFT * scaleX);
        int top = (int) Math.round(FRAME_OPENING_TOP * scaleY);
        int right = (int) Math.round(FRAME_OPENING_RIGHT * scaleX);
        int bottom = (int) Math.round(FRAME_OPENING_BOTTOM * scaleY);

        return new Rectangle(left, top, right - left, bottom - top);
    }

    // Layout: the turn banner, centered in the band above the frame

    public static final int TURN_PANEL_WIDTH = 560;
    public static final int TURN_PANEL_HEIGHT = 136;
    public static final int TURN_PANEL_TOP = 24;

    public static final int TURN_NAME_TOP = 16;
    public static final int TURN_SCORE_TOP = 62;
    public static final int TURN_STATUS_TOP = 92;

    public static int turnHiddenTop() {
        return -(TURN_PANEL_HEIGHT + 20);
    }

    // Layout: the board itself
    public static final int BOARD_BASE_CELL_SIZE = 64;

    // Layout: tiles, both on the board and in the rack
    public static final double TILE_PADDING_RATIO = 0.09;

    // Layout: player cards, stacked down the left edge

    public static final int CARD_LEFT = 40;
    public static final int CARD_TOP = 60;
    public static final int CARD_WIDTH = 231;
    public static final int CARD_HEIGHT = 160;
    public static final int CARD_SPACING = 208;
    public static final int CARD_EXIT_TOP = -(CARD_HEIGHT + 20);
    public static final int CARD_PADDING_X = 22;
    public static final int CARD_PADDING_Y = 20;
    public static final int CARD_LINE_HEIGHT = 28;

    // Layout: tile bag counter, bottom left

    public static final int BAG_SIZE = 217;
    public static final int BAG_CENTER_X = 148;
    public static final int BAG_CENTER_Y = 951;

    // Layout: the active player's rack along the bottom

    public static final int RACK_TILE_SIZE = 108;
    public static final int RACK_TILE_SPACING = 142;

    public static final double HANDOVER_PHASE_SECONDS = 0.2;

    public static int rackHiddenCenterY() {
        return EngineConfig.WINDOW_HEIGHT + RACK_TILE_SIZE;
    }

    // Layout: side button bar on the right

    public static final int SIDE_BUTTON_WIDTH = 270;
    public static final int SIDE_BUTTON_HEIGHT = 90;
    public static final int SIDE_BUTTON_MARGIN_RIGHT = 14;
    public static final int GLYPH_SIZE = 44;
    public static final int SIDE_BUTTON_SETTINGS_Y = 74;
    public static final int SIDE_BUTTON_DEBUG_Y = 507;
    public static final int SIDE_BUTTON_END_TURN_Y = 939;

    public static String text(String key) {
        return AssetManager.getMessage(key);
    }
}
