package quirkle.game.gamePlay.tiles;

import quirkle.engine.Entity;
import quirkle.engine.InputManager;
import quirkle.game.gamePlay.ui.UiTheme;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Base entity for anything that draws a single {@link Tile}.
 *
 * @note Holds no position of its own; where a tile lives is up to the subclass
 *       ({@link BoardTileEntity} on the board, {@code HandTileEntity} on the rack).
 * @note Every tile occupies a square box of {@link #getTileSize()} pixels, no matter what the
 *       texture's aspect ratio is. Outline, hit test and layout all read that box, so tiles stay
 *       the same size as the grid cells they sit on.
 */
public abstract class TileEntity extends Entity {
    /** Stroke width of the outlines subclasses draw around the tile box. */
    protected static final float OUTLINE_WIDTH = 3f;

    private final Tile tile;

    /** Edge length of the square this tile occupies, in pixels. */
    private double tileSize;

    protected TileEntity(Tile tile) {
        this.tile = tile;
        this.origin = OriginPresets.CENTER;
        setSprite(buildSpritePath(tile));
    }

    public Tile getTile() { return tile; }

    public double getTileSize() { return tileSize; }

    /**
     * Fixes the tile to a square of {@code tileSize} pixels and fits the texture inside it,
     * inset by {@link UiTheme#TILE_PADDING_RATIO} so the artwork never touches the outline.
     *
     * @note The longest edge of the texture determines the factor, so the artwork keeps its aspect
     *       ratio and never spills out of the box. Assets differ in size (248x219 up to 263x252),
     *       which is why the scale cannot be derived from the width alone.
     */
    public void setTileSize(double tileSize) {
        this.tileSize = tileSize;

        double artworkSize = tileSize * (1.0 - 2.0 * UiTheme.TILE_PADDING_RATIO);
        this.scale = artworkSize / Math.max(width, height);
    }

    /** @return the left edge of the tile box, which is independent of the texture's aspect ratio. */
    protected int getBoxX() { return (int) (x - origin.x * tileSize); }

    /** @return the top edge of the tile box. */
    protected int getBoxY() { return (int) (y - origin.y * tileSize); }

    /**
     * Draws a square outline along the tile box.
     *
     * @note Shared by every subclass so all outlines end up identical in size; only the color says
     *       what the outline means (pending, selected, hovered, rejected).
     * @implNote Inset by half the stroke width, and one pixel short of the box, so the line stays
     *           inside the cell instead of bleeding over the grid.
     */
    protected void drawOutline(Graphics2D g, Color color) {
        int inset = (int) (OUTLINE_WIDTH / 2f);
        int size = (int) tileSize - 2 * inset - 1;

        Graphics2D gOutline = (Graphics2D) g.create();
        gOutline.setColor(color);
        gOutline.setStroke(new BasicStroke(OUTLINE_WIDTH));
        gOutline.drawRect(getBoxX() + inset, getBoxY() + inset, size, size);
        gOutline.dispose();
    }

    /**
     * @return whether the mouse is inside the tile box
     * @note Overrides the sprite based hit test of {@link Entity}, so a flat texture like the
     *       hexagon is as easy to click as a tall one. Tiles are never rotated, hence no inverse
     *       rotation of the mouse here.
     */
    @Override
    public boolean isHovered() {
        double mouseX = InputManager.getMouseX();
        double mouseY = InputManager.getMouseY();

        int left = getBoxX();
        int top = getBoxY();

        return mouseX >= left && mouseX <= left + tileSize
            && mouseY >= top && mouseY <= top + tileSize;
    }

    /**
     * @return the texture identifier for {@code tile}, e.g. {@code "tiles/hexagon/hexagon_red"}
     * @note Folder and file names are lowercase throughout, so the path also resolves on
     *       case-sensitive file systems and from inside a jar.
     */
    private String buildSpritePath(Tile tile) {
        String shape = tile.getSymbol().name().toLowerCase();   // "hexagon"
        String color = tile.getColor().name().toLowerCase();    // "red"
        return "tiles/" + shape + "/" + shape + "_" + color;
    }
}
