package model.contents.fleet;

import model.Observer;
import model.contents.Content;
import model.enums.BoatName;
import model.enums.Orientation;
import model.grid.Position;

import java.util.ArrayList;

public class Boat implements Content {
    private int numberSquares;
    private boolean[] attacked;
    private BoatName name;
    private Position position;
    private ArrayList<Observer> observers;

    public Boat(BoatName name, int numberSquares) {
        this.name = name;
        this.numberSquares = numberSquares;
    }


    @Override
    public Position getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(int x, int y, Orientation orientation) {
        this.position = new Position(x, y, orientation);
    }
}