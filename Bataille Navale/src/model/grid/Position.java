package model.grid;

import model.enums.Orientation;

public class Position {
    private int x;
    private int y;
    private Orientation orientation;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position(int x, int y, Orientation orientation) {
        this.x = x;
        this.y = y;
        this.orientation = orientation;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;

    }

    public Orientation getOrientation() {
        return orientation;
    }
}