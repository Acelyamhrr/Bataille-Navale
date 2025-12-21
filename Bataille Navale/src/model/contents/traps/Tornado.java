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

    public void activate(int gridSize, boolean island, Position posIsland){
        Random rand = new Random();
        _numberUse = 3;
        _newPositions = new HashMap<>();

        if(!island) {
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    Position newPosition;
                    do {
                        int x = rand.nextInt(gridSize);
                        int y = rand.nextInt(gridSize);

                        newPosition = new Position(x, y);
                    } while (!exists(newPosition));

                    _newPositions.put(new Position(i, j), newPosition);
                }
            }
        }
        else{
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    Position newPosition;

                    //Si c'est une case de l'île elle doit rester sur l'île
                    if(i >= posIsland.getX() && i< posIsland.getX()+4 && j >= posIsland.getY() && j< posIsland.getY()+4){
                        do{
                            int x = rand.nextInt(posIsland.getX(),  posIsland.getX()+4);
                            int y = rand.nextInt(posIsland.getY(),  posIsland.getY()+4);

                            newPosition = new Position(x, y);
                        }while(!exists(newPosition));
                    }
                    else {
                        int x, y;
                        do {
                            x = rand.nextInt(gridSize);
                            y = rand.nextInt(gridSize);

                            newPosition = new Position(x, y);
                        } while (!exists(newPosition) || (x >= posIsland.getX() && x < posIsland.getX()+4 && y >= posIsland.getY() && y < posIsland.getY()+4));
                    }

                    _newPositions.put(new Position(i, j), newPosition);
                }
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