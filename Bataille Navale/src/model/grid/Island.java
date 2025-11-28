package model.grid;

import java.util.HashMap;
import java.util.Map;

public class Island {
    private static int SIZE = 4;
    private Position position;
    private Map<Position, Square> squares;

    public Island(Position position) {
        this.position = position;
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