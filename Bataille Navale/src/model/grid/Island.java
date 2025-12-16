package model.grid;

public class Island {
    private static int SIZE = 4;
    private Position position;

    public Island(Position position) {
        this.position = position;
    }

    public boolean contains(Position position) {
        if(position.getX() < this.position.getX() || position.getX() >= this.position.getX() +SIZE) return false;
        if(position.getY() < this.position.getY() || position.getY() >= this.position.getY() + SIZE) return false;
        return true;
    }

    Position getPosition() {
        return position;
    }

    int getSize() {
        return SIZE;
    }
}