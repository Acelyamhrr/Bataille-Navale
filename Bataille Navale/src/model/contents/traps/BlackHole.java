package model.contents.traps;

import model.enums.TrapType;
import model.grid.Square;
import model.players.Player;

public class BlackHole extends Trap {
    public BlackHole() {
        super(TrapType.BLACKHOLE);
    }

    @Override
    public int[] trigger(Player assaillant, Player defender, Square c) {
        //TODO
        return null;
    }
}
