package quirkle.game.gamePlay.tiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;

/** A shuffled supply of Qwirkle tiles that players draw from over the course of a game. */
public class TileBag {

    /** Number of tiles created for each color/symbol combination. */
    private static final int COPIES_PER_TILE = 3;

    /** Creates a bag containing one full tile set, shuffled using the default source of randomness. */
    public TileBag() {
        generateStartTiles();
    }

    /** Creates a bag containing one full tile set, shuffled using {@code random} instead of the default source of randomness. */
    public TileBag(RandomGenerator random) {
        generateStartTiles(random);
    }

    /** Tiles remaining in the bag, in draw order; the next tile drawn is at index 0. */
    private List<Tile> tiles = new ArrayList<>(TileSymbol.values().length * TileColor.values().length * COPIES_PER_TILE);

    /** Fills {@link #tiles} with a full tile set and shuffles it using the default source of randomness. */
    private void generateStartTiles() {
        createFullTileSet();
        shuffle();
    }

    /** Fills {@link #tiles} with a full tile set and shuffles it using {@code random}. */
    private void generateStartTiles(RandomGenerator random) {
        createFullTileSet();
        shuffle(random);
    }

    /** Adds {@value COPIES_PER_TILE} tiles of every color/symbol combination to {@link #tiles}. */
    private void createFullTileSet() {
        for (TileColor color : TileColor.values()) {
            for (TileSymbol symbol : TileSymbol.values()) {
                for (int i = 0; i < COPIES_PER_TILE; i++) {
                    tiles.add(new Tile(color, symbol));
                }
            }
        }
    }

    /** Shuffles {@link #tiles} using the default source of randomness. */
    private void shuffle() {
        Collections.shuffle(tiles);
    }

    /** Shuffles {@link #tiles} using {@code random}. */
    private void shuffle(RandomGenerator random) {
        Collections.shuffle(tiles, random);
    }

    /**
     * Draws {@code count} tiles from the bag, removing them.
     *
     * @return the drawn tiles, in draw order
     * @apiNote Returns fewer than {@code count} tiles, never throws, if the bag holds fewer tiles than requested.
     */
    public List<Tile> drawTiles(int count) {
        List<Tile> drawnTiles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (!tiles.isEmpty()) {
                drawnTiles.add(tiles.removeFirst());
            }
        }
        return drawnTiles;
    }

    /** @return the number of tiles remaining in the bag. */
    public int getSize() {
        return tiles.size();
    }
}
