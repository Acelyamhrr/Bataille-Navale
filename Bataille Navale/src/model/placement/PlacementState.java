package model.placement;

import model.enums.PlacementPhase;

/**
 * État interne du placement
 */
public class PlacementState {
    int boatIndex = 0;
    int trapIndex = 0;
    int weaponIndex = 0;
    private PlacementPhase phase = PlacementPhase.BOATS;

    public PlacementPhase getPhase() { return phase; }
    public void setPhase(PlacementPhase phase) { this.phase = phase; }

    public void reset() {
        boatIndex = 0;
        trapIndex = 0;
        weaponIndex = 0;
        phase = PlacementPhase.BOATS;
    }
}