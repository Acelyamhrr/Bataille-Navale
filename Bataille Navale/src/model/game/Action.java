package model.game;

import model.contents.weapons.Weapon;
import model.players.Player;

public class Action {
    private int round;
    private int[] position;
    private Player player;
    private Weapon weapon;
    private ResultAttack result;

    public Action(int round, int x, int y, Player player, Weapon weapon, ResultAttack result) {
        this.round = round;
        this.position = new int[]{x, y};
        this.player = player;
        this.weapon = weapon;
        this.result = result;
    }
}
