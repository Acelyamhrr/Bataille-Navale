package model.contents;

import model.enums.Orientation;
import model.grid.Position;

public interface Content {
    Position getPosition();
    void setPosition(int x, int y, Orientation orientation);
}