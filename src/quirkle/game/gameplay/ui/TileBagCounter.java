package quirkle.game.gameplay.ui;

import quirkle.game.gameplay.tiles.TileBag;

import java.awt.Graphics2D;

public class TileBagCounter extends PanelEntity {

    private static final int COUNT_OFFSET_Y = -8;

    private final TileBag tileBag;

    public TileBagCounter(TileBag tileBag) {
        this.tileBag = tileBag;
        setBounds(UiTheme.bagCenterX(), UiTheme.bagCenterY(),
                UiTheme.BAG_SIZE, UiTheme.BAG_SIZE, OriginPresets.CENTER);
    }

    @Override
    public void onRender(Graphics2D g) {
        drawSprite(UiTheme.SPRITE_GOLDEN_FRAME, UiTheme.BAG_SIZE / 210.0,
                x, y, 0.0, OriginPresets.CENTER, g);

        drawText(String.valueOf(tileBag.getSize()), UiTheme.FONT_SIZE_BAG_COUNT, UiTheme.TEXT,
                UiTheme.FONT_SCORE, x, y + COUNT_OFFSET_Y, 0.0, OriginPresets.CENTER, g);
    }
}
