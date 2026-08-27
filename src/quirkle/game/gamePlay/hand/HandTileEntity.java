package quirkle.game.gamePlay.hand;

import quirkle.game.gamePlay.tiles.Tile;
import quirkle.game.gamePlay.tiles.TileEntity;

import java.awt.*;

public class HandTileEntity extends TileEntity {
    private static final Color SELECTION_COLOR = new Color(255, 193, 7);
    private static final Color HOVER_COLOR = new Color(255, 255, 255, 140);
    private static final float OUTLINE_WIDTH = 3f;

    private boolean selected = false;

    public HandTileEntity(Tile tile) {
        super(tile);
    }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    @Override
    public void onRender(Graphics2D g) {
        if (!selected && !isHovered()) return;

        Graphics2D gOutline = (Graphics2D) g.create();
        gOutline.setColor(selected ? SELECTION_COLOR : HOVER_COLOR);
        gOutline.setStroke(new BasicStroke(OUTLINE_WIDTH));

        int scaleWidth = (int) getScaledWidth();
        int scaledHeight = (int) getScaledHeight();
        int drawX = (int) (x - origin.x * scaleWidth);
        int drawY = (int) (y - origin.y * scaleWidth);

        gOutline.drawRect(drawX, drawY, scaleWidth, scaledHeight);
        gOutline.dispose();
    }
}
