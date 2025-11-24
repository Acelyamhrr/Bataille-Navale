package model.contents.traps;

public class TrapFactory {
    public TrapFactory() {
    }

    public Trap createBlackHole(){
        return new BlackHole();
    }

    public Trap createTornado(){
        return new Tornado();
    }
}