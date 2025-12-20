package model.grid;

public class Island {
    private static int _SIZE = 4;
    private Position _position;

    public Island(Position position) {
        this._position = position;
    }

    public boolean contains(Position position) {
        if(position.getX() < this._position.getX() || position.getX() >= this._position.getX() + _SIZE) return false;
        if(position.getY() < this._position.getY() || position.getY() >= this._position.getY() + _SIZE) return false;
        return true;
    }

    Position getPosition() {
        return _position;
    }

    int getSize() {
        return _SIZE;
    }
}