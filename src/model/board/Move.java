package model.board;

import model.tiles.Tile;

import java.util.Map;

public record Move(Map<Position, Tile>tiles, int score) {
    public Move {
        tiles = Map.copyOf(tiles);
    }
}
