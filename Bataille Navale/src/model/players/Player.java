package model.players;

import model.contents.fleet.Boat;
import model.contents.traps.Trap;
import model.contents.weapons.Weapon;
import model.grid.Grid;
import model.grid.Square;

import java.util.List;

public abstract class Player {
    private String username;
    private boolean isRobot;
    private Grid grid;
    private List<Weapon> weapons;
    private List<Trap> traps;
    private List<Boat> boats;

    public Player(String name, boolean robot) {
        this.username = name;
        this.isRobot = robot;

        //TODO
    }

    public abstract void play(Weapon weapon, int x, int y);

    public void receiveAttack(Square s, Weapon weapon){
        //TODO
    }

    public boolean allBoatSunk(){
        //TODO
        return false;
    }

    public void reset(){
        //TODO
    }
}
