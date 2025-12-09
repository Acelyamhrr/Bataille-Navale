package model.contents.traps;

import model.enums.TrapType;
import model.grid.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Tornado extends Trap {
    private int numberUse;
    private Map<Position, Position> newPositions;

    public Tornado() {
        super(TrapType.TORNADO);
        this.numberUse = 0;
    }

    public boolean isActive(){
        return this.numberUse > 0;
    }

    public void activate(int gridSize){
        Random rand = new Random();
        numberUse = 3;
        newPositions = new HashMap<>();

        for(int i = 0; i < gridSize; i++){
            for(int j = 0; j < gridSize; j++){
                Position newPosition;
                do {
                    int x = rand.nextInt(gridSize);
                    int y = rand.nextInt(gridSize);

                    newPosition = new Position(x, y);
                }while(!exists(newPosition));

                newPositions.put(new Position(i, j), newPosition);
            }
        }

    }

    private boolean exists(Position p){
        for(Map.Entry<Position, Position> entry : newPositions.entrySet()){
            if(entry.getValue().equals(p)){
                return false;
            }
        }
        return true;
    }

    public Position getNewPosition(Position position){
        this.numberUse--;
        return newPositions.get(position);
    }
}