package model;

import model.enums.Orientation;
import model.enums.State;
import model.grid.Position;

public interface Observer {
    void boatAttacked(Position position);
    void boatSunk(Position position, int size);
    void squareAttacked(Position position);
    void squareIsland(Position position, State state);
}