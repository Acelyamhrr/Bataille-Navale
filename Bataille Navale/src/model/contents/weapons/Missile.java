package model.contents.weapons;

import model.enums.WeaponType;
import model.game.ResultAttack;
import model.grid.Grid;

public class Missile extends Weapon{
    public Missile() {
        super(WeaponType.MISSILE);
    }

    @Override
    public ResultAttack use(Grid grid, int x, int y) {
        //TODO
        return null;
    }
}
