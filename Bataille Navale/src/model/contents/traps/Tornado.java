package model.contents.traps;

import model.enums.TrapType;
import model.grid.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Tornado extends Trap {
    private int _numberUse;
    private Map<Position, Position> _newPositions;

    public Tornado() {
        super(TrapType.TORNADO);
        this._numberUse = 0;
    }

    public boolean isActive(){
        return this._numberUse > 0;
    }

    public void activate(int gridSize){
        Random rand = new Random();
        _numberUse = 3;
        _newPositions = new HashMap<>();

        for(int i = 0; i < gridSize; i++){
            for(int j = 0; j < gridSize; j++){
                Position newPosition;
                do {
                    int x = rand.nextInt(gridSize);
                    int y = rand.nextInt(gridSize);

                    newPosition = new Position(x, y);
                }while(!exists(newPosition));

                _newPositions.put(new Position(i, j), newPosition);
            }
        }

    }

    private boolean exists(Position p){
        for(Map.Entry<Position, Position> entry : _newPositions.entrySet()){
            if(entry.getValue().equals(p)){
                return false;
            }
        }
        return true;
    }

    public Position getNewPosition(Position position){
        this._numberUse--;
        return _newPositions.get(position);
    }
}