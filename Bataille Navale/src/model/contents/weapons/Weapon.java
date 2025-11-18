package model.contents.weapons;

import model.contents.Content;
import model.enums.WeaponType;
import model.game.ResultAttack;
import model.grid.Grid;

public abstract class Weapon implements Content {
    private int[] position;
    private WeaponType name;

    public Weapon(WeaponType name){
        this.name = name;
    }

    public abstract ResultAttack use(Grid grid, int x, int y);

    public void setPosition(int x, int y){
        position = new int[]{x,y};
    }

    @Override
    public int[] getPosition() {
        return this.position;
    }
}
