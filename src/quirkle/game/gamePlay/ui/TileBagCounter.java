package quirkle.game.gamePlay.ui;

import quirkle.game.gamePlay.tiles.TileBag;

import java.awt.Graphics2D;

/**
 * The golden diamond in the bottom left corner, showing how many tiles are left to draw.
 *
 * @note Reads {@link TileBag#getSize()} every frame; the bag itself is never touched.
 * @implNote Draws the diamond as a plain sprite rather than through nine-slice, since the asset
 *           is square and is drawn square - stretching it would break the gold leaf texture.
 */
public class TileBagCounter extends PanelEntity {

    /** Distance from the diamond's center down to the "in bag" caption. */
    private static final int LABEL_OFFSET_Y = 74;
    /** The count sits slightly above the center so count and caption read as one block. */
    private static final int COUNT_OFFSET_Y = -8;

    private final TileBag tileBag;

    public TileBagCounter(TileBag tileBag) {
        this.tileBag = tileBag;
        setBounds(UiTheme.BAG_CENTER_X, UiTheme.BAG_CENTER_Y,
                UiTheme.BAG_SIZE, UiTheme.BAG_SIZE, OriginPresets.CENTER);
    }

    @Override
    public void onRender(Graphics2D g) {
        drawSprite(UiTheme.SPRITE_GOLDEN_FRAME, UiTheme.BAG_SIZE / 210.0,
                x, y, 0.0, OriginPresets.CENTER, g);

        drawText(String.valueOf(tileBag.getSize()), UiTheme.FONT_SIZE_BAG_COUNT, UiTheme.TEXT,
                UiTheme.FONT, x, y + COUNT_OFFSET_Y, 0.0, OriginPresets.CENTER, g);

        drawText(UiTheme.text("in_bag"), UiTheme.FONT_SIZE_BAG_LABEL, UiTheme.TEXT,
                UiTheme.FONT, x, y + LABEL_OFFSET_Y, 0.0, OriginPresets.CENTER, g);
    }
}
