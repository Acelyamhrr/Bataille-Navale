package model.grid;

import model.Observer;
import model.contents.Content;
import model.contents.fleet.Boat;
import model.enums.State;

import java.util.ArrayList;

public class Square {
    private boolean attacked;
    private boolean inIsland;
    private Content content;
    private Position position;
    private State islandState;
    private ArrayList<Observer> observers;

    public Square(Position position, boolean inIsland) {
        this.position = position;
        this.inIsland = inIsland;

        if(inIsland) {
            this.islandState = State.INTACT;
        }

        this.observers = new ArrayList<>();
    }

    public Square(Position position) {
        this.position = position;
        this.inIsland = false;
        this.observers = new ArrayList<>();
    }

    public void setIsland(){
        this.inIsland = true;
        this.islandState = State.INTACT;
    }

    public boolean wasAttacked() {
        return this.attacked;
    }

    public boolean isEmpty(){
        return this.content == null;
    }

    public boolean isInIsland() {
        return this.inIsland;
    }

    public Content getContent() {
        return this.content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Position getPosition() {
        return this.position;
    }

    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    public void attack(){
        this.attacked = true;

        if(this.content instanceof Boat){
            Boat b = (Boat) this.content;
            b.attack(this.position);
        }
        else{
            notifyObserversAttacked();
        }
    }

    public Content search(){
        if(this.islandState == State.INTACT) {
            if(!this.isEmpty()){
                this.islandState = State.SEARCHED;
                notifyObserversIsland();
                return this.content;
            }
            this.islandState = State.EMPTY;
            notifyObserversIsland();
            return null;
        }
        return null;
    }

    public boolean isNotSearched(){
        return this.islandState != State.INTACT;
    }

    private void notifyObserversAttacked(){
        for(Observer o : this.observers){
            o.squareAttacked(this.position);
        }
    }

    private void notifyObserversIsland(){
        for(Observer o : this.observers){
            o.squareIsland(this.position,  this.islandState);
        }
    }

}