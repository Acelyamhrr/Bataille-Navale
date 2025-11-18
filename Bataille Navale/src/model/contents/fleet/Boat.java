package model.contents.fleet;

import model.contents.Content;
import model.enums.BoatName;
import model.enums.Orientation;
import model.grid.Square;

import java.util.ArrayList;

public class Boat implements Content {
    private int numberSquares;
    private int[] position;
    private BoatName name;
    private Orientation orientation;
    private ArrayList<Square> squaresTaken;

    public Boat(BoatName name, int numberSquares) {
        this.name = name;
        this.numberSquares = numberSquares;
    }

    public boolean hasSunk(){
        return false;
    }

    public boolean attacked(Square s){
        return false;
    }

    public void setPosition(int x, int y){
        this.position = new int[]{x,y};
    }

    public void setOrientation(Orientation orientation){
        this.orientation = orientation;
    }

    @Override
    public int[] getPosition() {
        return this.position;
    }
}
