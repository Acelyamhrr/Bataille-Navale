package model.contents.weapons;

import model.contents.Content;
import model.enums.Orientation;
import model.enums.WeaponType;
import model.grid.Position;

import java.util.ArrayList;

public abstract class Weapon implements Content {
    private WeaponType name;
    private Position position;

    public Weapon(WeaponType name){
        this.name = name;
    }

    @Override
    public Position getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(int x, int y, Orientation orientation) {
        this.position = new Position(x, y);
    }

    public abstract ArrayList<Position> use(Position position);

}