package model.placement;

import java.util.List;

/**
 * Contient la phase (boat ou pas)
 * Contient les options à mettre dans les comboBox pour la vue
 */
public class SelectionState {
    private List<String> boatOptions;
    private List<String> trapWeaponOptions;
    private boolean onPhaseBoat;
    private boolean onPhaseTrap;

    public SelectionState(List<String> boatOptions, List<String> trapWeaponOptions, boolean onPhaseBoat, boolean onPhaseTrap) {
        this.boatOptions = boatOptions;
        this.trapWeaponOptions = trapWeaponOptions;
        this.onPhaseBoat = onPhaseBoat;
        this.onPhaseTrap = onPhaseTrap;
    }

    public List<String> getBoatOptions() {
        return boatOptions;
    }

    public List<String> getTrapWeaponOptions(){
        return trapWeaponOptions;
    }

    public boolean isBoatSelectorEnabled() {
        return onPhaseBoat;
    }

    public boolean isTrapWeaponSelectorEnabled() {
        return onPhaseTrap;
    }
}
