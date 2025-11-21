package model.grid;

import model.contents.fleet.Boat;
import model.contents.traps.Trap;
import model.contents.weapons.Weapon;
import model.enums.ModeGame;
import model.enums.Orientation;
import model.game.ResultAttack;

import java.util.ArrayList;

public class Grid {
    private int size;
    private ModeGame mode;
    private ArrayList<Square> squares;
    private Island island;

    public Grid(int size, ModeGame mode){
        this.size = size;
        this.mode = mode;
        this.squares = new ArrayList<>();

        if(mode == ModeGame.ISLAND){
            ArrayList<Square> squaresIsland = new ArrayList<>();
            for(int i = 0; i < size; i++){
                for(int j = 0; j < size; j++){
                    Square square;
                    if(i > 3 && i < 6 &&  j > 3 && j < 6){              //Coordonnées cases île en dur
                        square = new Square(i, j, true);
                        squaresIsland.add(square);
                    }
                    else{
                        square = new Square(i, j, false);
                    }
                    squares.add(square);
                }
            }
            island = new Island(3, 3, 4, squaresIsland);
        }
        else{
            for(int i = 0; i < size; i++){
                for(int j = 0; j < size; j++){
                    squares.add(new Square(i, j, false));
                }
            }
            this.island = null;
        }
    }

    public void placeBoat(Boat b, int x, int y, Orientation orientation){
        //TODO
    }

    public void placeTrap(Trap t, int x, int y){
        t.setPosition(x, y);
        this.squares.get(x*this.size + y).setContent(t);
    }

    public ResultAttack attack(int x, int y, Weapon weapon){
        //TODO
        return null;
    }

    public void reset(){
        //TODO
    }
}
