package model.contents.weapons;

import model.contents.Content;
import model.enums.ContentType;
import model.enums.Orientation;
import model.enums.WeaponType;
import model.grid.Position;

import java.util.ArrayList;

public abstract class Weapon implements Content {
    private WeaponType name;
    private Position position;
    private ContentType contentType;

    public Weapon(WeaponType name){
        this.name = name;
        this.contentType = ContentType.WEAPON;
    }

    @Override
    public Position getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(int x, int y, Orientation orientation) {
        this.position = new Position(x, y);
    }

    @Override
    public ContentType getContentType() {
        return this.contentType;
    }

    public abstract ArrayList<Position> use(Position position);

}