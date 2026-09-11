package quirkle.game.gamePlay.player;

import quirkle.game.gamePlay.tiles.Tile;
import quirkle.game.gamePlay.tiles.TileBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {
    public static  final int RACK_SIZE = 6;

    private final String name;
    private int score;

    /** Points scored on this player's most recent completed turn */
    private int lastRoundScore;

    private final List<Tile> rack = new ArrayList<>();

    public Player(String name) {
        this.name = name;
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void increaseScore(int points) {
        setScore(this.score + points);
    }

    public int getLastRoundScore() {
        return lastRoundScore;
    }

    public void setLastRoundScore(int lastRoundScore) {
        this.lastRoundScore = lastRoundScore;
    }

    public void resetScore() {
        setScore(0);
    }

    public List<Tile> getRack() {
        return Collections.unmodifiableList(rack);
    }

    /**
     * Draws from {@code tileBag} until the rack holds {@value #RACK_SIZE} tiles
     */
    public void refillRack(TileBag tileBag) {
        rack.addAll(tileBag.drawTiles(RACK_SIZE - rack.size()));
    }

    /** @return if this exact tile instance is on the player's rack */
    public boolean hasTile(Tile tile) {
        return rack.contains(tile);
    }

    /**
     * Puts {@code tile} onto the player's rack.
     *
     * @throws IllegalStateException if the rack already holds {@value #RACK_SIZE} tiles
     */
    public void addTile(Tile tile) {
        if (isRackFull()) {
            throw new IllegalStateException("Cannot add tile; " + name + "'s rack is full.");
        }
        rack.add(tile);
    }

    /**
     * Removes {@code tile} from the player's rack.
     *
     * @throws IllegalStateException if the player does not hold that exact tile
     */
    public void removeTile(Tile tile) {
        if (!rack.remove(tile)) {
            throw new IllegalStateException("Cannot remove tile; " + name + " does not hold it.");
        }
    }

    /** @return if the rack is full, so a tile can only go on it by replacing another. */
    public boolean isRackFull() {
        return rack.size() >= RACK_SIZE;
    }

    /**
     * replaces tile on rack slot {@code slotIndex} with {@code tile}
     *
     * @return the tile that was replaced
     * @throws IndexOutOfBoundsException if the rack has no such slot
     */
    public Tile replaceTile(int slotIndex, Tile tile) {
        if (slotIndex < 0 || slotIndex >= rack.size()) {
            throw new IndexOutOfBoundsException(
                    "No rack slot " + slotIndex + "; " + name + " holds " + rack.size() + " tiles.");
        }
        return rack.set(slotIndex, tile);
    }

    /**
     * Takes every tile off the rack.
     *
     * @return the tiles that were on it, in rack order
     */
    public List<Tile> clearRack() {
        List<Tile> removed = new ArrayList<>(rack);
        rack.clear();
        return removed;
    }

    private void setScore(int score) {
        this.score = score;
    }
}
