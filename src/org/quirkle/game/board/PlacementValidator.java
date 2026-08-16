package org.quirkle.game.board;

import java.util.Map;

import org.quirkle.game.tiles.Tile;

public class PlacementValidator {

    /** @return {@code true} if {@code position} is free in {@code tileMap}. */
    public static boolean isTilePlacementPossible(Map<Position, Tile> tileMap, Position position) {
        return tileMap.get(position) == null;
    }
}
