package model.grid;

import java.util.HashMap;
import java.util.Map;

public class Island {
    private int size;
    private Position position;
    private Map<Position, Square> squares;

    public Island(Position position, int size) {
        this.position = position;
        this.size = size;
        this.squares = new HashMap<Position, Square>();
    }

    public void addSquare(Position position, Square square) {
        this.squares.put(position, square);
    }

    public Square getSquare(Position position) {
        return this.squares.get(position);
    }

    public boolean contains(Position position) {
        return squares.containsKey(position);
    }
}