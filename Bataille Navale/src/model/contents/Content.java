package model.contents;

import model.enums.ContentType;
import model.enums.Orientation;
import model.grid.Position;

public abstract class Content {
    protected Position _position;
    protected ContentType _contentType;

    public abstract void setPosition(int x, int y, Orientation orientation);
    public abstract Position getPosition();
    public abstract ContentType getContentType();
}