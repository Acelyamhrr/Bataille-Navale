package model.grid;

import model.contents.fleet.Boat;
import model.contents.traps.Trap;
import model.contents.weapons.Weapon;
import model.enums.ContentType;
import model.enums.ModeGame;
import model.enums.Orientation;

import java.util.HashMap;
import java.util.Map;

public  class Grid {
    private int size;
    private Map<Position, Square> squares;
    private ModeGame mode;
    private Island island;
    private boolean robot;

    public Grid(int size, ModeGame mode, boolean robot) {
        this.size = size;
        this.mode = mode;
        this.squares = new HashMap<Position, Square>();
        this.robot = robot;

        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++){
                Position pos = new Position(i, j);
                this.squares.put(pos, new Square(pos, robot));
            }
        }

        if(mode == ModeGame.ISLAND){
            int x = size/2 -2;
            int y = size/2 -2;
            this.island = new Island(new Position(x, y));

            for(int i=x; i<x+4; i++){
                for(int j=y; j<y+4; j++){
                    Position pos = new Position(i, j);
                    this.squares.get(pos).setIsland();
                    this.island.addSquare(pos, this.squares.get(pos));
                }
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

    public void placeWeapon(Weapon weapon, int x, int y) {
        Position pos = new Position(x, y);
        weapon.setPosition(x, y, null);
        this.squares.get(pos).setContent(weapon);
    }

    public void attack(Position position){
        this.squares.get(position).attack();
    }

    public void reset(){
        this.squares.clear();

        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++){
                Position pos = new Position(i, j);
                this.squares.put(pos, new Square(pos, this.robot));
            }
        }
    }

    public int getSize(){
        return this.size;
    }

    public ContentType getContentTypeSquare(Position pos){
        return this.squares.get(pos).getContentType();
    }

    public boolean squareIsInIsland(Position pos){
        return this.squares.get(pos).isInIsland();
    }

    public Square getSquare(Position pos) {
        return this.squares.get(pos);
    }

}