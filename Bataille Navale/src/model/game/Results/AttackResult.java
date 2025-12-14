package model.game.Results;

import model.game.TrapActivation;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsule le résultat d'une attaque (missile, bombe, sonar).
 */
public class AttackResult {
    private int hits;
    private int misses;
    private boolean sunkBoat;
    private List<TrapActivation> trapActivations;

    // Pour le sonar
    private boolean isSonar;
    private int occupiedCells;

    public AttackResult(int hits, int misses, boolean sunkBoat, List<TrapActivation> trapActivations) {
        this.hits = hits;
        this.misses = misses;
        this.sunkBoat = sunkBoat;
        this.trapActivations = new ArrayList<>(trapActivations);
        this.isSonar = false;
    }

    private AttackResult(int occupiedCells) {
        this.isSonar = true;
        this.occupiedCells = occupiedCells;
        this.trapActivations = new ArrayList<>();
    }

    public static AttackResult sonarResult(int occupiedCells) {
        return new AttackResult(occupiedCells);
    }

    // ===== GETTERS =====

    public int getHits() {
        return hits;
    }

    public int getMisses() {
        return misses;
    }

    public boolean hadSunk() {
        return sunkBoat;
    }

    public boolean hadHit() {
        return hits > 0;
    }

    public List<TrapActivation> getTrapActivations() {
        return new ArrayList<>(trapActivations);
    }

    public boolean isSonar() {
        return isSonar;
    }

    public int getOccupiedCells() {
        return occupiedCells;
    }

    public boolean hadTrapActivations() {
        return !trapActivations.isEmpty();
    }
}