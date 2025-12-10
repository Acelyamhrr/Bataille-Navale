package model.contents.fleet;

import model.Observer;
import model.contents.Content;
import model.enums.BoatName;
import model.enums.ContentType;
import model.enums.Orientation;
import model.grid.Position;

import java.util.ArrayList;

public class Boat extends Content {
    private int numberSquares;
    private boolean[] attacked;
    private BoatName name;
    private ArrayList<Observer> observers;
    private boolean robot;
    private boolean alreadySunk;
    private boolean alreadyTouched;

    public Boat(BoatName name, int numberSquares){
        this.name = name;
        this.numberSquares = numberSquares;
        this.attacked = new boolean[numberSquares];
        this.observers = new ArrayList<>();
        this.contentType = ContentType.BOAT;
        this.alreadySunk = false;
        this.alreadyTouched = false;
    }

    public void belongsTo(boolean robot){
        this.robot = robot;
    }


    @Override
    public void setPosition(int x, int y, Orientation orientation) {
        this.position = new Position(x, y, orientation);
    }

    @Override
    public Position getPosition() {
        return this.position;
    }

    @Override
    public ContentType getContentType() {
        return this.contentType;
    }

    public boolean hasSunk(){
        for(int i = 0; i < this.numberSquares; i++){
            if(!this.attacked[i]){
                return false;
            }
        }

        if (!alreadySunk) {
            notifyObserversSunk();
            alreadySunk = true;
        }
        return true;
    }

    public void attack(Position position){
        int pos;
        if(this.position.getOrientation() == Orientation.VERTICAL){
            pos = position.getY() - this.position.getY();
        }
        else{
            pos = position.getX() - this.position.getX();
        }

        this.attacked[pos] = true;

        if (!alreadyTouched) {
            notifyObserversTouched(this.position, this.numberSquares);
            alreadyTouched = true;
        }

        notifyObserversAttacked(position);
    }

    public void addObserver(Observer observer){
        this.observers.add(observer);
    }

    private void notifyObserversAttacked(Position position){
        for(Observer observer : this.observers){
            observer.boatAttacked(position, this.robot);
        }
    }

    private void notifyObserversSunk(){
        for(Observer observer : this.observers){
            observer.boatSunk(this.position, this.numberSquares, this.robot);
        }
    }

    private void notifyObserversTouched(Position position, int numberSquares){
        for(Observer observer : this.observers){
            observer.boatTouched(this.robot);
        }
    }

    public int getSize(){
        return this.numberSquares;
    }
}