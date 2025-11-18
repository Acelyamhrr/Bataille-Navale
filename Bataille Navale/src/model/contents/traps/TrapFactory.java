package model.contents.traps;

public class TrapFactory {
    public TrapFactory() {}

    public BlackHole createBlackHole(){
        return new BlackHole();
    }

    public Tornado createTornado(){
        return new Tornado();
    }
}
