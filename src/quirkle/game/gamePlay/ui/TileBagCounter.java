package quirkle.game.gamePlay.ui;

import quirkle.game.gamePlay.tiles.TileBag;

import java.awt.Graphics2D;

public class TileBagCounter extends PanelEntity {

    private static final int LABEL_OFFSET_Y = 74;
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
