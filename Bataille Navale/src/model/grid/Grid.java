package model.grid;

import model.contents.fleet.Boat;
import model.contents.traps.Trap;
import model.enums.ModeGame;
import model.enums.Orientation;

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

    public void placeBoat(Boat b, int x, int y, Orientation orientation) {
        Position pos = new Position(x, y, orientation);
        b.setPosition(x, y, orientation);

        //Placement de l'instance dans les cases occupées
        this.squares.get(pos).setContent(b);
        if(orientation == Orientation.HORIZONTAL){
            for(int i=1; i<b.getSize(); i++){
                Position pos2 = new Position(pos.getX()+i, pos.getY());
                this.squares.get(pos2).setContent(b);
            }
        }
        else{
            for(int i=1; i<b.getSize(); i++){
                Position pos2 = new Position(pos.getX(), pos.getY()+i);
                this.squares.get(pos2).setContent(b);
            }
        }
    }

    public void placeTrap(Trap trap, int x, int y) {
        Position pos = new Position(x, y);
        trap.setPosition(x, y, null);
        this.squares.get(pos).setContent(trap);
    }

    public void attack(Position position){
        this.squares.get(position).attack();
    }

    public void reset(){
        //TODO
    }

}