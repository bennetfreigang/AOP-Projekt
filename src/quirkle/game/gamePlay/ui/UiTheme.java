package quirkle.game.gamePlay.ui;

import quirkle.engine.AssetManager;

import java.awt.Color;

/**
 * Single source of truth for the gameplay HUD's look: colors, fonts, asset identifiers,
 * nine-slice insets and the layout metrics taken from the mockup.
 *
 * @note Every metric below is given for the 1920x1080 window {@link quirkle.engine.EngineConfig}
 *       sets up. Values that the mockup anchors to an edge (the button bar on the right, the
 *       rack at the bottom) are stored as margins, so the HUD follows the window rather than
 *       sitting at fixed coordinates.
 */
public final class UiTheme {

    private UiTheme() {}

    // Render layers, applied to Entity#renderOrder

    /** Board grid, drawn underneath the tiles. */
    public static final int LAYER_GRID = -100;
    /** Board and hand tiles; matches Entity's default so untouched entities keep working. */
    public static final int LAYER_TILE = 0;
    /** HUD panels, drawn on top of everything on the board. */
    public static final int LAYER_HUD = 100;

    // Fonts

    /** The only font shipped in {@code assets/fonts}. */
    public static final String FONT = "DEBUG_Poly-Regular";

    public static final float FONT_SIZE_CARD = 19f;
    /** The active player's name, the rack's headline. */
    public static final float FONT_SIZE_RACK_NAME = 26f;
    public static final float FONT_SIZE_RACK = 22f;
    /** Secondary rack line, currently what was scored last round. */
    public static final float FONT_SIZE_RACK_SMALL = 18f;
    public static final float FONT_SIZE_BUTTON = 30f;
    public static final float FONT_SIZE_BAG_COUNT = 72f;
    public static final float FONT_SIZE_BAG_LABEL = 20f;

    // Colors

    public static final Color BACKGROUND = new Color(8, 8, 10);
    public static final Color TEXT = new Color(245, 245, 245);
    public static final Color TEXT_DIMMED = new Color(245, 245, 245, 120);
    public static final Color GOLD = new Color(198, 154, 58);

    /** Faint grid lines between the board cells. */
    public static final Color GRID_LINE = new Color(255, 255, 255, 34);
    /** Small diamond markers sitting on the grid intersections. */
    public static final Color GRID_MARKER = new Color(255, 255, 255, 60);

    /** Fill of a rack slot, drawn behind the tile that sits in it. */
    public static final Color SLOT_FILL = new Color(255, 255, 255, 14);

    /** Placeholder body for a side button whose artwork has not been delivered yet. */
    public static final Color PLACEHOLDER_FILL = new Color(255, 255, 255, 22);
    public static final Color PLACEHOLDER_BORDER = new Color(255, 255, 255, 60);

    // Nine-slice frame assets

    /**
     * How much of a {@code container_0X} frame (1123x1118) is brush stroke rather than fill.
     * Measured off the assets: a dark margin of up to 50px, then the stroke out to about 110px.
     */
    public static final int CONTAINER_SOURCE_INSET = 130;

    /** On-screen stroke thickness for the large panels: player cards and the rack. */
    public static final int PANEL_BORDER = 14;

    /** On-screen stroke thickness for the small squares of the rack. */
    public static final int SLOT_BORDER = 6;

    /** The placement cursor is stroked heavier, so it reads against the grid underneath it. */
    public static final int CURSOR_BORDER = 8;

    public static final String FRAME_PLAYER_CARD = "Ui/container_02";
    public static final String FRAME_RACK = "Ui/container_05";
    public static final String FRAME_SLOT = "Ui/container_03";
    /** @note container_02 and _03 are the two frames that stay even once scaled down this far. */
    public static final String FRAME_CURSOR = "Ui/container_02";
    public static final String SPRITE_GOLDEN_FRAME = "Ui/goldenframe";

    // Layout: the board itself

    /**
     * Cell size at zoom 1. The mockup's cells are about a seventh of the window wide, which the
     * camera's default zoom of 2 turns this into.
     */
    public static final int BOARD_BASE_CELL_SIZE = 64;

    // Layout: tiles, both on the board and in the rack

    /**
     * Gap between a tile's artwork and its outline, as a fraction of the tile box per side.
     *
     * @note Applies to the texture only. The box itself stays exactly one cell wide, so outlines
     *       and hit areas are identical for every shape no matter what this is set to.
     */
    public static final double TILE_PADDING_RATIO = 0.09;

    // Layout: player cards, stacked down the left edge

    public static final int CARD_LEFT = 40;
    /** Top edge of the first card; the stack starts near the window's top left corner. */
    public static final int CARD_TOP = 60;
    public static final int CARD_WIDTH = 231;
    public static final int CARD_HEIGHT = 160;
    /** Distance between the top edges of two stacked cards. */
    public static final int CARD_SPACING = 208;
    /** Inset from the card's side edges; clears {@link #PANEL_BORDER} so text is not overdrawn. */
    public static final int CARD_PADDING_X = 22;
    public static final int CARD_PADDING_Y = 20;
    public static final int CARD_LINE_HEIGHT = 28;

    // Layout: tile bag counter, bottom left

    public static final int BAG_SIZE = 217;
    /**
     * Center of the diamond, tucked into the window's bottom left corner.
     *
     * @note Half of {@link #BAG_SIZE} plus a margin: the diamond keeps 40px to the left edge and
     *       20px to the bottom, clear of the rack, which starts at x 436.
     */
    public static final int BAG_CENTER_X = 148;
    public static final int BAG_CENTER_Y = 951;

    // Layout: the active player's rack along the bottom

    public static final int RACK_WIDTH = 1048;
    public static final int RACK_HEIGHT = 215;
    /** Distance from the rack's lower edge to the bottom of the window. */
    public static final int RACK_MARGIN_BOTTOM = 55;
    /** Inset from the rack's side edges; clears {@link #PANEL_BORDER} so text is not overdrawn. */
    public static final int RACK_PADDING_X = 30;
    public static final int RACK_PADDING_Y = 14;
    public static final int RACK_TILE_SIZE = 108;
    /** Distance between the centers of two rack slots. */
    public static final int RACK_TILE_SPACING = 142;
    /**
     * Vertical center of the row of slots, measured from the rack's top edge.
     *
     * @note Sits below the panel's middle: the label band above the slots needs more room than
     *       the margin below them.
     */
    public static final int RACK_SLOT_CENTER_Y = 135;
    /** Baseline distance between the two stacked score lines on the right. */
    public static final int RACK_LINE_HEIGHT = 26;

    // Layout: side button bar on the right

    public static final int SIDE_BUTTON_WIDTH = 402;
    public static final int SIDE_BUTTON_HEIGHT = 90;
    /** Distance from the buttons' right edge to the right of the window. */
    public static final int SIDE_BUTTON_MARGIN_RIGHT = 12;
    /** Edge length of the square a {@link ButtonGlyph} is drawn inside. */
    public static final int GLYPH_SIZE = 44;
    /**
     * Vertical center of the settings button, at the top of the bar.
     *
     * @note The two slots between this and {@link #SIDE_BUTTON_END_TURN_Y} are the mockup's
     *       positions for the new game and take back buttons, which the bar no longer shows.
     */
    public static final int SIDE_BUTTON_SETTINGS_Y = 74;
    /** Vertical center of the end turn button, at the bottom of the bar. */
    public static final int SIDE_BUTTON_END_TURN_Y = 939;

    /**
     * @return the localized string for {@code key}, taken from the active language package
     * @see AssetManager#getMessage(String)
     */
    public static String text(String key) {
        return AssetManager.getMessage(key);
    }
}
