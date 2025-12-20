package model.grid;

import model.Observer;
import model.contents.Content;
import model.contents.fleet.Boat;
import model.enums.ContentType;
import model.enums.State;

import java.util.ArrayList;

public class Square {
    private boolean _attacked;
    private boolean _inIsland;
    private Content _content;
    private Position _position;
    private State _islandState;
    private ArrayList<Observer> _observers;
    private boolean _robot;

    public Square(Position position, boolean inIsland, boolean robot) {
        this._position = position;
        this._inIsland = inIsland;
        this._robot = robot;

        if(inIsland) {
            this._islandState = State.INTACT;
        }

        this._observers = new ArrayList<>();
    }

    public Square(Position position, boolean robot) {
        this._position = position;
        this._inIsland = false;
        this._observers = new ArrayList<>();
        this._robot = robot;
    }

    public void setIsland(){
        this._inIsland = true;
        this._islandState = State.INTACT;
    }

    public boolean wasAttacked() {
        return this._attacked;
    }

    public boolean isEmpty(){
        return this._content == null;
    }

    public boolean isInIsland() {
        return this._inIsland;
    }

    public Content getContent() {
        return this._content;
    }

    public void setContent(Content content) {
        this._content = content;
    }

    public Position getPosition() {
        return this._position;
    }

    public void addObserver(Observer observer) {
        this._observers.add(observer);
    }

    public void attack(){
        this._attacked = true;

        if(getContentType() == ContentType.BOAT) {
            Boat b = (Boat) this._content;
            b.attack(this._position);
        }
        else if (!isInIsland()){
            notifyObserversAttacked();
        }
    }

    public Content search(){
        if(this._islandState == State.INTACT) {
            if(!this.isEmpty()){
                this._islandState = State.SEARCHED;


                notifyObserversIsland();
                return this._content;
            }
            this._islandState = State.EMPTY;
            notifyObserversIsland();
            return null;
        }
        return null;
    }

    public boolean isNotSearched(){
        return this._islandState == State.INTACT;
    }

    private void notifyObserversAttacked(){
        for(Observer o : this._observers){
            o.squareAttacked(this._position, this._robot);
        }
    }

    private void notifyObserversIsland(){
        for(Observer o : this._observers){
            o.squareIsland(this._position,  this._islandState, this._robot);
        }
    }



    public ContentType getContentType() {
        if (this._content == null) {
            return ContentType.EMPTY;
        }
        return this._content.getContentType();
    }

}