package org.quirkle.game.tiles;

/** A single Quirkle tile with a color and a symbol. */
public class Tile {
    private TileColor color;
    private TileSymbol symbol;

    public Tile(TileColor color, TileSymbol symbol) {
        this.color = color;
        this.symbol = symbol;
    }

    /** @return the tile's color. */
    public TileColor getColor() {
        return color;
    }

    /** @return the tile's symbol. */
    public TileSymbol getSymbol() {
        return symbol;
    }
}
