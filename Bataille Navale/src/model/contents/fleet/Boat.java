package model.contents.fleet;

import model.Observer;
import model.contents.Content;
import model.enums.BoatName;
import model.enums.Orientation;
import model.grid.Position;

import java.util.ArrayList;

public class Boat implements Content {
    private int numberSquares;
    private boolean[] attacked;
    private BoatName name;
    private Position position;
    private ArrayList<Observer> observers;

    public Boat(BoatName name, int numberSquares){
        this.name = name;
        this.numberSquares = numberSquares;
        this.attacked = new boolean[numberSquares];
        this.observers = new ArrayList<>();
    }


    @Override
    public void setPosition(int x, int y, Orientation orientation) {
        this.position = new Position(x, y, orientation);
    }

    @Override
    public Position getPosition() {
        return this.position;
    }

    public boolean hasSunk(){
        for(int i = 0; i < this.numberSquares; i++){
            if(!this.attacked[i]){
                return false;
            }
        }
        notifyObserversSunk();
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

        notifyObserversAttacked(position);
    }

    public void addObserver(Observer observer){
        this.observers.add(observer);
    }

    private void notifyObserversAttacked(Position position){
        for(Observer observer : this.observers){
            observer.boatAttacked(position);
        }
    }

    private void notifyObserversSunk(){
        for(Observer observer : this.observers){
            observer.boatSunk(this.position, this.numberSquares);
        }
    }

    public int getSize(){
        return this.numberSquares;
    }
}