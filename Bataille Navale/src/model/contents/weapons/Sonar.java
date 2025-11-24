package model.contents.weapons;

import model.enums.WeaponType;
import model.grid.Position;

import java.util.ArrayList;

public class Sonar extends Weapon{
    public Sonar(){
        super(WeaponType.SONAR);
    }

    @Override
    public ArrayList<Position> use(Position position) {
        ArrayList<Position> positions = new ArrayList<>();
        //TODO
        return positions;
    }
}