package model.grid;

import model.Observer;
import model.contents.Content;
import model.contents.fleet.Boat;
import model.contents.traps.Trap;
import model.contents.weapons.Weapon;
import model.enums.ContentType;
import model.enums.State;
import model.enums.TrapType;
import model.enums.WeaponType;

import java.util.ArrayList;

public class Square {
    private boolean attacked;
    private boolean inIsland;
    private Content content;
    private Position position;
    private State islandState;
    private ArrayList<Observer> observers;
    private boolean robot;

    public Square(Position position, boolean inIsland, boolean robot) {
        this.position = position;
        this.inIsland = inIsland;
        this.robot = robot;

        if(inIsland) {
            this.islandState = State.INTACT;
        }

        this.observers = new ArrayList<>();
    }

    public Square(Position position, boolean robot) {
        this.position = position;
        this.inIsland = false;
        this.observers = new ArrayList<>();
        this.robot = robot;
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

        if(getContentType() == ContentType.BOAT) {
            Boat b = (Boat) this.content;
            b.attack(this.position);
        }
        else if (!isInIsland()){
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
        return this.islandState == State.INTACT;
    }

    private void notifyObserversAttacked(){
        for(Observer o : this.observers){
            o.squareAttacked(this.position, this.robot);
        }
    }

    private void notifyObserversIsland(){
        for(Observer o : this.observers){
            o.squareIsland(this.position,  this.islandState, this.robot);
        }
    }



    public ContentType getContentType() {
        if (this.content == null) {
            return ContentType.EMPTY;
        }
        return this.content.getContentType();
    }

}