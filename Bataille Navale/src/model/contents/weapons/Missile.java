package model.contents.weapons;

import model.enums.WeaponType;
import model.grid.Position;

import java.util.ArrayList;

public class Missile extends Weapon{
    public Missile(){
        super(WeaponType.MISSILE);
    }

    @Override
    public ArrayList<Position> use(Position position) {
        ArrayList<Position> positions = new ArrayList<>();
        positions.add(position);
        return positions;
    }
}