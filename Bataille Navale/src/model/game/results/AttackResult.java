package model.game.results;

import model.game.TrapActivation;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsule le résultat d'une attaque (missile, bombe, sonar).
 */
public class AttackResult {
    private int _hits;
    private boolean _sunkBoat;
    private List<TrapActivation> _trapActivations;

    // Pour le sonar
    private boolean _isSonar;
    private int _occupiedCells;

    public AttackResult(int hits, boolean sunkBoat, List<TrapActivation> trapActivations) {
        this._hits = hits;
        this._sunkBoat = sunkBoat;
        this._trapActivations = new ArrayList<>(trapActivations);
        this._isSonar = false;
    }

    private AttackResult(int occupiedCells) {
        this._isSonar = true;
        this._occupiedCells = occupiedCells;
        this._trapActivations = new ArrayList<>();
    }

    public static AttackResult sonarResult(int occupiedCells) {
        return new AttackResult(occupiedCells);
    }

    // ===== GETTERS =====

    public int getHits() {
        return _hits;
    }

    public boolean hadSunk() {
        return _sunkBoat;
    }

    public boolean hadHit() {
        return _hits > 0;
    }

    public List<TrapActivation> getTrapActivations() {
        return new ArrayList<>(_trapActivations);
    }

    public boolean isSonar() {
        return _isSonar;
    }

    public int getOccupiedCells() {
        return _occupiedCells;
    }

    public boolean hadTrapActivations() {
        return !_trapActivations.isEmpty();
    }
}