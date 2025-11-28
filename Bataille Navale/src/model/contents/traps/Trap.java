package model.contents.traps;

import model.contents.Content;
import model.enums.Orientation;
import model.enums.TrapType;
import model.grid.Position;

public abstract class Trap implements Content {
    private TrapType name;
    private Position position;

    public Trap(TrapType name){
        this.name = name;
    }

    @Override
    public void setPosition(int x, int y, Orientation orientation){
        position = new Position(x, y);
    }

    @Override
    public Position getPosition(){
        return this.position;
    }
}