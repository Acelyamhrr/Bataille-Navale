package model.contents.traps;

import model.enums.TrapType;
import model.grid.Square;
import model.players.Player;

public class Tornado extends Trap {
    private int numberUse;

    public Tornado() {
        super(TrapType.TORNADO);
        this.numberUse = 3;
    }

    @Override
    public int[] trigger(Player assaillant, Player defender, Square c) {
        //TODO
        return null;
    }
}
