package org.quirkle.game;

public class Player {
    private String name;
    private int score;

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

    // TODO: only one public setScore function or increaseScore and resetScore

    private void setScore(int score) {
        this.score = score;
    }

    public void increaseScore(int points) {
        setScore(this.score + points);
    }

    public void resetScore() {
        setScore(0);
    }
}
