package model.game;

import model.enums.TrapType;
import model.grid.Position;

/**
 * Représente l'activation d'un piège pendant une attaque.
 */
public class TrapActivation {
    private TrapType _type;
    private Position _position;
    private boolean _bounced; // true si trou noir (attaque renvoyée)

    public TrapActivation(TrapType type, Position position, boolean bounced) {
        this._type = type;
        this._position = position;
        this._bounced = bounced;
    }

    public TrapType getType() {
        return _type;
    }

    public Position getPosition() {
        return _position;
    }

    public boolean isBounced() {
        return _bounced;
    }

    @Override
    public String toString() {
        return "TrapActivation{" + "type=" + _type + ", position=" + _position + ", bounced=" + _bounced + '}';
    }
}