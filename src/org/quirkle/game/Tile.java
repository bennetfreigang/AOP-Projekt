package org.quirkle.game;

public class Tile {
    private String color;
    private String symbol;

    public Tile(String color, String symbol) {
        this.color = color;
        this.symbol = symbol;
    }

    public String getColor() {
        return color;
    }

    public String getSymbol() {
        return symbol;
    }
}
