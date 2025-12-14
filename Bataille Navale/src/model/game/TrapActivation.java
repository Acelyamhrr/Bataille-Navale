package model.game;

import model.enums.TrapType;
import model.grid.Position;

/**
 * Représente l'activation d'un piège pendant une attaque.
 */
public class TrapActivation {
    private TrapType type;
    private Position position;
    private boolean bounced; // true si trou noir (attaque renvoyée)

    public TrapActivation(TrapType type, Position position, boolean bounced) {
        this.type = type;
        this.position = position;
        this.bounced = bounced;
    }

    public TrapType getType() {
        return type;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isBounced() {
        return bounced;
    }

    @Override
    public String toString() {
        return "TrapActivation{" + "type=" + type + ", position=" + position + ", bounced=" + bounced + '}';
    }
}