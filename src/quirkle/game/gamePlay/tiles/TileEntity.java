package quirkle.game.gamePlay.tiles;

import quirkle.engine.Entity;

/**
 * Base entity for anything that draws a single {@link Tile}.
 *
 * @note Holds no position of its own; where a tile lives is up to the subclass
 *       ({@link BoardTileEntity} on the board, {@code HandTileEntity} on the rack).
 */
public abstract class TileEntity extends Entity {
    private final Tile tile;

    protected TileEntity(Tile tile) {
        this.tile = tile;
        this.origin = OriginPresets.CENTER;
        setSprite(buildSpritePath(tile));
    }

    public Tile getTile() { return tile; }

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