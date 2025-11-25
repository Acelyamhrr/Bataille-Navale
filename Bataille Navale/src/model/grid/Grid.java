package model.grid;

import model.contents.fleet.Boat;
import model.contents.traps.Trap;
import model.enums.ModeGame;

import java.util.HashMap;
import java.util.Map;

public  class Grid {
    private int size;
    private Map<Position, Square> squares;
    private ModeGame mode;
    private Island island;

    public Grid(int size, ModeGame mode) {
        this.size = size;
        this.mode = mode;
        this.squares = new HashMap<Position, Square>();

        //TODO : faire les squares et l'île
        this.island = new Island(new Position(4, 4), 4); //TODO : ajuster la position de l'île

        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                Position pos = new Position(i, j);
                Square square = new Square(pos, false); //TODO : faire si inIsland
                squares.put(pos, square);
            }
        }
    }

    public void placeBoat(Boat b, Position position) {
        //TODO
    }

    public void placeTrap(Trap trap, Position position) {
        //TODO
    }

    public void attack(Position position){
        this.squares.get(position).attack();
    }

    public void reset(){
        //TODO
    }

}