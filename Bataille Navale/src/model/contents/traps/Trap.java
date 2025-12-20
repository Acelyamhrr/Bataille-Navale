package model.contents.traps;

import model.contents.Content;
import model.enums.ContentType;
import model.enums.Orientation;
import model.enums.TrapType;
import model.grid.Position;

public abstract class Trap extends Content {
    private TrapType _name;

    public Trap(TrapType name){
        this._name = name;
        this._contentType = ContentType.TRAP;
    }

    @Override
    public void setPosition(int x, int y, Orientation orientation){
        _position = new Position(x, y);
    }

    @Override
    public Position getPosition(){
        return this._position;
    }

    @Override
    public ContentType getContentType() {
        return this._contentType;
    }

    public TrapType getName() {
        return this._name;
    }
}