package model.game;

import java.util.ArrayList;

public class History {
    private ArrayList<Action> actions;

    public History() {
        this.actions = new ArrayList<Action>();
    }

    public void addAction(Action action){
        this.actions.add(action);
    }

    public Action getLast(){
        return this.actions.getLast();
    }

    public void reset(){
        this.actions.clear();
    }
}
