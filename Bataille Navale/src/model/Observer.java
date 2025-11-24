package model;

import model.grid.Position;

public interface Observer {
    void boatAttacked(Position position);
}