package model.contents.weapons;

import model.contents.Content;
import model.enums.ContentType;
import model.enums.Orientation;
import model.enums.WeaponType;
import model.grid.Position;

import java.util.ArrayList;

public abstract class Weapon extends Content {
    private WeaponType _name;

    public Weapon(WeaponType name){
        this._name = name;
        this._contentType = ContentType.WEAPON;
    }

    @Override
    public Position getPosition() {
        return this._position;
    }

    @Override
    public void setPosition(int x, int y, Orientation orientation) {
        this._position = new Position(x, y);
    }

    @Override
    public ContentType getContentType() {
        return this._contentType;
    }

    public abstract ArrayList<Position> use(Position position);

    public WeaponType getName() {
        return this._name;
    }
}