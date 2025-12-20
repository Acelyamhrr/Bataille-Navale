package model.grid;

import model.enums.Orientation;

import java.util.Objects;

public class Position {
    private int _x;
    private int _y;
    private Orientation _orientation;

    public Position(int x, int y) {
        this._x = x;
        this._y = y;
        this._orientation = Orientation.NONE;
    }

    public Position(int x, int y, Orientation orientation) {
        this._x = x;
        this._y = y;
        this._orientation = orientation;
    }

    public int getX() {
        return _x;
    }

    public int getY() {
        return _y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position p = (Position) o;
        return _x == p._x && _y == p._y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_x, _y);
    }

    public Orientation getOrientation() {
        return _orientation;
    }
}