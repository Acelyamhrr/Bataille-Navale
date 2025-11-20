package model.grid;

import java.util.ArrayList;

public class Island {
    private int[] position;
    private int size;
    private ArrayList<Square> squares;

    public Island(int x, int y, int size, ArrayList<Square> squares) {
        this.size = size;
        this.position = new int[] {x, y};
        this.squares = squares;
    }

    public boolean contains(Square s){
        return squares.contains(s);
    }
}
