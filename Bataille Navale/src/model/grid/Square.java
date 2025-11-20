package model.grid;

import model.contents.Content;
import model.contents.weapons.Weapon;
import model.enums.State;
import model.game.ResultAttack;

public class Square {
    private int[] position;
    private boolean attacked;
    private boolean inIsland;
    private Content content;
    private State islandState;

    public Square(int x, int y, boolean island){
        this.position = new int[] {x,y};
        this.inIsland = island;
    }

    public boolean isEmpty(){
        return content==null;
    }

    public ResultAttack attack(Weapon weapon){
        //TODO
        return null;
    }

    public Content getContent(){
        return this.content;
    }
}
