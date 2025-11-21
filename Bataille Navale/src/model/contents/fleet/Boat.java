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
    private ArrayList<Square> squaresTaken;             //TODO (dans méthodes)

    public Boat(BoatName name, int numberSquares) {
        this.name = name;
        this.numberSquares = numberSquares;
    }

    public boolean hasSunk(){
        for(Square s: squaresTaken){
            if(!s.wasAttacked()){
                return false;
            }
        }

        return true;
    }

    public boolean attacked(int x, int y){
        Square square = getSquare(x,y);
        if(square != null){
            square.wasAttacked();
        }

        return false;
    }

    private Square getSquare(int x, int y){
        for(Square square : squaresTaken){
            int[] position = square.getPosition();
            if(position[0] == x && position[1] == y ){
                return square;
            }
        }
        return null;
    }

    @Override
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
