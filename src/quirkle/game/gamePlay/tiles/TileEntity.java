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

    private String buildSpritePath(Tile tile) {
        String symbol = tile.getSymbol().name();               // "HEXAGON"
        String shapeFolder = symbol.charAt(0) + symbol.substring(1).toLowerCase(); // "Hexagon"
        String color = tile.getColor().name().toLowerCase();    // "red"
        String shape = symbol.toLowerCase();                     // "hexagon"
        return "tiles/" + shapeFolder + "/" + color + "_" + shape;
    }
}