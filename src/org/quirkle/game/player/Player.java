package org.quirkle.game.player;

/** A Quirkle player with a name and a running score. */
public class Player {
    private final String name;
    private int score;

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

    private void setScore(int score) {
        this.score = score;
    }
}
