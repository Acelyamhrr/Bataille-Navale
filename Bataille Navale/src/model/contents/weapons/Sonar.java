package model.contents.weapons;

import model.enums.WeaponType;
import model.game.ResultAttack;
import model.grid.Grid;

public class Sonar extends Weapon{
    public Sonar() {
        super(WeaponType.SONAR);
    }

    @Override
    public ResultAttack use(Grid grid, int x, int y) {
        //TODO
        return null;
    }
}
