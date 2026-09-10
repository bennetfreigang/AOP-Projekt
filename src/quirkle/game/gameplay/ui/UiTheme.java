package quirkle.game.gameplay.ui;

import quirkle.engine.AssetManager;
import quirkle.engine.EngineConfig;

import java.awt.Color;
import java.awt.Rectangle;

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
    /**
     * The frame the board is seen through, drawn over the board but under the HUD.
     *
     * @note Sitting below {@link #LAYER_HUD} is what lets a panel lie on the frame's border
     *       rather than being covered by it, which is how the rack meets the bottom edge.
     */
    public static final int LAYER_FRAME = 50;
    /** HUD panels, drawn on top of everything on the board. */
    public static final int LAYER_HUD = 100;

    // Fonts

    /** The only font shipped in {@code assets/fonts}. */
    public static final String FONT = "poly_regular";

    public static final float FONT_SIZE_CARD = 19f;
    /** @note Sized for the narrowed side buttons; "SETTINGS" has to fit inside 270px. */
    public static final float FONT_SIZE_BUTTON = 24f;
    /** The active player's name in the banner above the frame. */
    public static final float FONT_SIZE_TURN_NAME = 34f;
    /** The active player's score, the banner's middle line. */
    public static final float FONT_SIZE_TURN_SCORE = 22f;
    /** Turn number and prompt, the banner's last line. */
    public static final float FONT_SIZE_TURN_STATUS = 20f;
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

    public static final String FRAME_PLAYER_CARD = "gameplay/ui/container_02";
    public static final String FRAME_SLOT = "gameplay/ui/container_03";
    /** @note container_02 and _03 are the two frames that stay even once scaled down this far. */
    public static final String FRAME_CURSOR = "gameplay/ui/container_02";
    public static final String SPRITE_GOLDEN_FRAME = "gameplay/ui/goldenframe";

    /** Full-window overlay with a transparent opening; the board is seen through it. */
    public static final String FRAME_BOARD = "gameplay/ui/frame";

    // Layout: the frame the board is seen through

    /**
     * Size of the {@link #FRAME_BOARD} asset. Needed to express its opening as a fraction of the
     * window rather than as fixed pixels.
     *
     * @note The asset is 16:9, same as the window {@link EngineConfig} sets up, so scaling it to
     *       fill the window never distorts it.
     */
    private static final double FRAME_SOURCE_WIDTH = 4608.0;
    private static final double FRAME_SOURCE_HEIGHT = 2592.0;

    /**
     * The frame's transparent opening, in source pixels, measured off the asset's alpha channel.
     *
     * @note The opening is an exact rectangle: every pixel inside it has alpha 0, every pixel
     *       outside it alpha 255. That is why a plain rectangular clip reproduces it exactly.
     */
    private static final double FRAME_OPENING_LEFT = 716.0;
    private static final double FRAME_OPENING_TOP = 436.0;
    private static final double FRAME_OPENING_RIGHT = 3891.0;
    private static final double FRAME_OPENING_BOTTOM = 2157.0;

    /**
     * @return the part of the window the board is visible in, in screen pixels
     * @note At 1920x1080 this is (298, 182) to (1621, 899), which leaves 298px of gutter on the
     *       left, 299px on the right and roughly 181px above and below for the HUD to live in.
     *       Derived from the asset rather than hard-coded, so changing the window size through
     *       {@link EngineConfig#setSize(int, int)} moves the HUD with it.
     */
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
    /**
     * @note Grown from 112px to carry the score as a line of its own. Its lower edge lands 22px
     *       clear of the frame's opening, which starts 182px down.
     */
    public static final int TURN_PANEL_HEIGHT = 136;
    /** Top edge of the banner; the band above the frame's opening is 182px tall. */
    public static final int TURN_PANEL_TOP = 24;

    /**
     * Top edges of the banner's three lines, measured from the panel's own top edge.
     *
     * @note Given one by one rather than as a padding plus a shared line height: the three lines
     *       are set at three different sizes, so no single line height sits right under all of
     *       them. The first clears {@link #PANEL_BORDER} so the brush stroke does not cut it.
     */
    public static final int TURN_NAME_TOP = 16;
    public static final int TURN_SCORE_TOP = 62;
    public static final int TURN_STATUS_TOP = 92;

    /**
     * @return the top edge the banner is parked at while it is off screen
     * @note Its full height above the window plus a margin, so the brush frame clears the edge
     *       rather than resting against it.
     */
    public static int turnHiddenTop() {
        return -(TURN_PANEL_HEIGHT + 20);
    }

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
    /**
     * Top edge a card is heading for once its player takes their turn.
     *
     * @note Clear of the window rather than one slot up: slot -1 would leave the card's lower
     *       edge hanging 12px into view, since a card is taller than the gap between two slots.
     */
    public static final int CARD_EXIT_TOP = -(CARD_HEIGHT + 20);
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

    /**
     * @note The rack is the row of slots and nothing else: no panel around it, no labels. Its
     *       bounds follow from the two metrics below rather than being given here, which is what
     *       keeps its hit area down to the tiles the player can actually click.
     */
    public static final int RACK_TILE_SIZE = 108;
    /** Distance between the centers of two rack slots. */
    public static final int RACK_TILE_SPACING = 142;

    /**
     * How long one half of a turn handover takes, in seconds.
     *
     * @note The whole thing therefore runs 0.4s: long enough to read as one player leaving and
     *       the next arriving, short enough not to sit between two turns. Everything the HUD
     *       animates on a handover is timed off this one value through {@link Handover}, which is
     *       what keeps the rack, the banner and the cards moving as one.
     */
    public static final double HANDOVER_PHASE_SECONDS = 0.2;

    /**
     * @return the vertical center the rack is parked at while it is off screen
     * @note A full slot below the window's lower edge, so neither a tile nor the brush socket
     *       around it is left peeking over the bottom during the handover.
     */
    public static int rackHiddenCenterY() {
        return EngineConfig.WINDOW_HEIGHT + RACK_TILE_SIZE;
    }

    // Layout: side button bar on the right

    /**
     * @note Narrowed from the mockup's 402px: the frame leaves a 299px gutter to the right of
     *       its opening, and a wider button would reach across the board.
     */
    public static final int SIDE_BUTTON_WIDTH = 270;
    public static final int SIDE_BUTTON_HEIGHT = 90;
    /** Distance from the buttons' right edge to the right of the window. */
    public static final int SIDE_BUTTON_MARGIN_RIGHT = 14;
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
