package model.players;

import model.contents.weapons.Weapon;

public class HumanPlayer extends Player{
    public HumanPlayer(String name) {
        super(name, false);
    }

    @Override
    public void play(Weapon weapon, int x, int y) {}
}
