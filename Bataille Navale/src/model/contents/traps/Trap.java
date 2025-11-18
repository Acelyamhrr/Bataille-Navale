package model.contents.traps;

import model.contents.Content;
import model.enums.TrapType;
import model.grid.Square;
import model.players.Player;

public abstract class Trap implements Content {
    private boolean isActive;
    private int[] positions;
    private TrapType type;

    public Trap(TrapType type){
        this.type = type;
        this.isActive = false;
        this.positions = new int[2];
    }

    @Override
    public void setPosition(int x, int y){
        this.positions[0] = x;
        this.positions[1] = y;
        this.isActive = true;
    }

    public abstract int[] trigger(Player assaillant, Player defender, Square c);

    @Override
    public int[] getPosition() {
        return this.positions;
    }
}
