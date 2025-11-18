package model.contents.weapons;

import model.enums.WeaponType;
import model.game.ResultAttack;
import model.grid.Grid;

public class Bomb extends Weapon{
    public Bomb() {
        super(WeaponType.BOMB);
    }

    @Override
    public ResultAttack use(Grid grid, int x, int y) {
        return null;
    }
}
