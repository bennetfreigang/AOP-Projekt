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
     * Removes {@code tile} from the player's rack.
     *
     * @throws IllegalStateException if the player does not hold that exact tile
     */
    public void removeTile(Tile tile) {
        if (!hand.remove(tile)) {
            throw new IllegalStateException("Cannot remove tile; " + name + " does not hold it.");
        }
    }
    private void setScore(int score) {
        this.score = score;
    }
}
