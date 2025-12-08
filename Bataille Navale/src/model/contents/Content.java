package model.contents;

import model.enums.ContentType;
import model.enums.Orientation;
import model.grid.Position;

public interface Content {
    void setPosition(int x, int y, Orientation orientation);
    Position getPosition();
    ContentType getContentType();
}