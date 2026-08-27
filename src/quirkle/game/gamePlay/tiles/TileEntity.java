package quirkle.game.gamePlay.tiles;

import quirkle.engine.Entity;
import quirkle.game.gamePlay.board.Position;

public class TileEntity extends Entity {
    private final Tile tile;
    private final Position position;

    public TileEntity(Tile tile, Position position) {
        this.tile = tile;
        this.position = position;
        this.origin = OriginPresets.CENTER;
        setSprite(buildSpritePath(tile));
    }

    public Tile getTile() { return tile; }
    public Position getPosition() { return position; }

    private String buildSpritePath(Tile tile) {
        String symbol = tile.getSymbol().name();               // "HEXAGON"
        String shapeFolder = symbol.charAt(0) + symbol.substring(1).toLowerCase(); // "Hexagon"
        String color = tile.getColor().name().toLowerCase();    // "red"
        String shape = symbol.toLowerCase();                     // "hexagon"
        return "tiles/" + shapeFolder + "/" + color + "_" + shape;
    }
}
