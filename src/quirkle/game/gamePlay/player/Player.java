package quirkle.game.gamePlay.player;

import quirkle.game.gamePlay.tiles.Tile;
import quirkle.game.gamePlay.tiles.TileBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A Quirkle player with a name and a running score. */
public class Player {
    public static  final int HAND_SIZE = 6;

    private final String name;
    private int score;

    /** Points scored on this player's most recent completed turn; {@code 0} before their first. */
    private int lastRoundScore;

    private final List<Tile> hand = new ArrayList<>();

    public Player(String name) {
        this.name = name;
        this.score = 0;
    }

    /** @return the player's name. */
    public String getName() {
        return name;
    }

    /** @return the player's current score. */
    public int getScore() {
        return score;
    }

    /** Adds {@code points} to the player's score. */
    public void increaseScore(int points) {
        setScore(this.score + points);
    }

    /** @return the points this player scored on their most recent completed turn. */
    public int getLastRoundScore() {
        return lastRoundScore;
    }

    /** Records what this player scored on the turn they just finished. */
    public void setLastRoundScore(int lastRoundScore) {
        this.lastRoundScore = lastRoundScore;
    }

    /** Resets the player's score to zero. */
    public void resetScore() {
        setScore(0);
    }

    /** @return the player's tiles, in rack order.*/
    public List<Tile> getHand() {
        return Collections.unmodifiableList(hand);
    }

    /**
     * Draws from {@code tileBag} until the hand holds {@value #HAND_SIZE} tiles.
     *
     * @apiNote Refills only partially, and never throws, if the bag runs out of tiles.
     */
    public void refillHand(TileBag tileBag) {
        hand.addAll(tileBag.drawTiles(HAND_SIZE - hand.size()));
    }

    /** @return {@code true} if this exact tile is on the player's rack. */
    public boolean hasTile(Tile tile) {
        return hand.contains(tile);
    }

    /**
     * Puts {@code tile} onto the player's rack.
     *
     * @note Used to hand a staged tile back when its placement is taken back; drawing from the
     *       bag goes through {@link #refillHand} instead.
     * @throws IllegalStateException if the rack already holds {@value #HAND_SIZE} tiles
     */
    public void addTile(Tile tile) {
        if (hand.size() >= HAND_SIZE) {
            throw new IllegalStateException("Cannot add tile; " + name + "'s rack is full.");
        }
        hand.add(tile);
    }

    /**
     * Removes {@code tile} from the player's rack.
     *
     * @throws IllegalStateException if the player does not hold that exact tile
     */
    public void removeTile(Tile tile) {
        if (!hand.remove(tile)) {
            throw new IllegalStateException("Cannot remove tile; " + name + " does not hold it.");
        }
    }

    /** @return whether the rack is full, so a tile can only go on it by replacing another. */
    public boolean isHandFull() {
        return hand.size() >= HAND_SIZE;
    }

    /**
     * Puts {@code tile} into rack slot {@code slotIndex}, in place of what stood there.
     *
     * @return the tile that was replaced
     * @throws IndexOutOfBoundsException if the rack has no such slot
     */
    public Tile replaceTile(int slotIndex, Tile tile) {
        if (slotIndex < 0 || slotIndex >= hand.size()) {
            throw new IndexOutOfBoundsException(
                    "No rack slot " + slotIndex + "; " + name + " holds " + hand.size() + " tiles.");
        }
        return hand.set(slotIndex, tile);
    }

    /**
     * Takes every tile off the rack.
     *
     * @return the tiles that were on it, in rack order
     * @note Returns them rather than dropping them, so the caller can decide where they go -
     *       back into the bag, or nowhere.
     */
    public List<Tile> clearHand() {
        List<Tile> removed = new ArrayList<>(hand);
        hand.clear();
        return removed;
    }

    private void setScore(int score) {
        this.score = score;
    }
}
