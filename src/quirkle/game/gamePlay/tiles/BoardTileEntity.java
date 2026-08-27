package quirkle.game.gamePlay.tiles;

import quirkle.game.gamePlay.board.Position;

/** A tile sitting on a fixed board {@link Position}; its screen position follows the board camera. */
public class BoardTileEntity extends TileEntity {
    private final Position position;

    public BoardTileEntity(Tile tile, Position position) {
        super(tile);
        this.position = position;
    }

    public Position getPosition() { return position; }
}