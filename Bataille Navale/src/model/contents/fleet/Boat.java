package model.contents.fleet;

import model.Observer;
import model.contents.Content;
import model.enums.BoatName;
import model.enums.ContentType;
import model.enums.Orientation;
import model.grid.Position;

import java.util.ArrayList;

public class Boat extends Content {
    private int _numberSquares;
    private boolean[] _attacked;
    private BoatName _name;
    private ArrayList<Observer> _observers;
    private boolean _robot;
    private boolean _alreadySunk;
    private boolean _alreadyTouched;

    public Boat(BoatName name, int numberSquares){
        this._name = name;
        this._numberSquares = numberSquares;
        this._attacked = new boolean[numberSquares];
        this._observers = new ArrayList<>();
        this._contentType = ContentType.BOAT;
        this._alreadySunk = false;
        this._alreadyTouched = false;
    }

    public static int getBoatSize(BoatName boat){
        switch (boat){
            case AIRCRAFT_CARRIER:
                return 5;
            case CRUISER:
                return 4;
            case DESTROYER:
                return 3;
            case SUBMARINE:
                return 3;
            default:
                return 2;
        }
    }

    public void belongsTo(boolean robot){
        this._robot = robot;
    }


    @Override
    public void setPosition(int x, int y, Orientation orientation) {
        this._position = new Position(x, y, orientation);
    }

    @Override
    public Position getPosition() {
        return this._position;
    }

    @Override
    public ContentType getContentType() {
        return this._contentType;
    }

    public boolean hasSunk(){
        for(int i = 0; i < this._numberSquares; i++){
            if(!this._attacked[i]){
                return false;
            }
        }

        if (!_alreadySunk) {
            notifyObserversSunk();
            _alreadySunk = true;
        }
        return true;
    }

    public void attack(Position position){
        int pos;
        if(this._position.getOrientation() == Orientation.VERTICAL){
            pos = position.getY() - this._position.getY();
        }
        else{
            pos = position.getX() - this._position.getX();
        }

        this._attacked[pos] = true;

        if (!_alreadyTouched) {
            notifyObserversTouched(this._position, this._numberSquares);
            _alreadyTouched = true;
        }

        notifyObserversAttacked(position);
    }

    public void addObserver(Observer observer){
        this._observers.add(observer);
    }

    private void notifyObserversAttacked(Position position){
        for(Observer observer : this._observers){
            observer.boatAttacked(position, this._robot);
        }
    }

    private void notifyObserversSunk(){
        for(Observer observer : this._observers){
            observer.boatSunk(this._position, this._numberSquares, this._robot);
        }
    }

    private void notifyObserversTouched(Position position, int numberSquares){
        for(Observer observer : this._observers){
            observer.boatTouched(this._robot);
        }
    }

    public int getSize(){
        return this._numberSquares;
    }


    public boolean isTouched() {
        return this._alreadyTouched;
    }

    public BoatName getName() {
        return this._name;
    }
}