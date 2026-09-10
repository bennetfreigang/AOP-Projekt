package quirkle.game.gameplay.tiles;

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
        this(RandomGenerator.getDefault());
    }

    /** Creates a bag containing one full tile set, shuffled using {@code random} instead of the default source of randomness. */
    public TileBag(RandomGenerator random) {
        this.random = random;
        generateStartTiles();
    }

    /** Tiles remaining in the bag, in draw order; the next tile drawn is at index 0. */
    private final List<Tile> tiles = new ArrayList<>(TileSymbol.values().length * TileColor.values().length * COPIES_PER_TILE);

    /**
     * Every shuffle of this bag draws from here.
     *
     * @note Kept rather than only used once, so that {@link #reset()} shuffles the way the bag was
     *       created: a bag built on a seeded generator stays reproducible across a reset.
     */
    private final RandomGenerator random;

    /** Fills {@link #tiles} with a full tile set and shuffles it. */
    private void generateStartTiles() {
        createFullTileSet();
        shuffle();
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

    /** Shuffles {@link #tiles} using this bag's source of randomness. */
    private void shuffle() {
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

    /**
     * Puts {@code tile} on top of the stack, so it is the next one drawn.
     */
    public void putOnTop(Tile tile) {
        tiles.addFirst(tile);
    }

    /**
     * Puts {@code newTiles} on top of the stack, keeping their order: the first element of the
     * list is the next tile drawn.
     */
    public void putOnTop(List<Tile> newTiles) {
        for (int i = newTiles.size() - 1; i >= 0; i--) {
            tiles.addFirst(newTiles.get(i));
        }
    }

    /** Empties the bag. */
    public void clear() {
        tiles.clear();
    }

    /** Refills the bag with a full, freshly shuffled tile set. */
    public void reset() {
        tiles.clear();
        generateStartTiles();
    }

    /**
     * @return a copy of the next {@code count} tiles in draw order, without removing them
     * @apiNote Returns fewer tiles, never throws, if the bag holds fewer than {@code count};
     *          a count below zero yields an empty list.
     */
    public List<Tile> peek(int count) {
        int available = Math.max(0, Math.min(count, tiles.size()));
        return List.copyOf(tiles.subList(0, available));
    }

    /** @return the number of tiles remaining in the bag. */
    public int getSize() {
        return tiles.size();
    }
}
