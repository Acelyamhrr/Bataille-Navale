package model.grid;

import model.enums.Orientation;

import java.util.Objects;

public class Position {
    private int x;
    private int y;
    private Orientation orientation;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
        this.orientation = Orientation.NONE;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position p = (Position) o;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public Orientation getOrientation() {
        return orientation;
    }
}