package model.grid;

import model.contents.fleet.Boat;
import model.contents.traps.Trap;
import model.contents.weapons.Weapon;
import model.enums.ModeGame;
import model.enums.Orientation;
import model.game.ResultAttack;

import java.util.ArrayList;

public class Grid {
    private int size;
    private ModeGame mode;
    private ArrayList<Square> squares;
    private Island island;

    public Grid(int size, ModeGame mode){
        this.size = size;
        this.mode = mode;

        //TODO
    }

    public void placeBoat(Boat b, int x, int y, Orientation orientation){
        //TODO
    }

    public void placeTrap(Trap t, int x, int y){
        //TODO
    }

    public ResultAttack attack(int x, int y, Weapon weapon){
        //TODO
        return null;
    }

    public void reset(){
        //TODO
    }
}
