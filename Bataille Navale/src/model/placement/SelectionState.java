package model.placement;

import java.util.List;

/**
 * Contient la phase (boat ou pas)
 * Contient les options à mettre dans les comboBox pour la vue
 */
public class SelectionState {
    private List<String> _boatOptions;
    private List<String> _trapWeaponOptions;
    private boolean _onPhaseBoat;

    public SelectionState(List<String> boatOptions, List<String> trapWeaponOptions, boolean onPhaseBoat) {
        this._boatOptions = boatOptions;
        this._trapWeaponOptions = trapWeaponOptions;
        this._onPhaseBoat = onPhaseBoat;
    }

    public List<String> getBoatOptions() {
        return _boatOptions;
    }

    public List<String> getTrapWeaponOptions(){
        return _trapWeaponOptions;
    }

    public boolean isBoatSelectorEnabled() {
        return _onPhaseBoat;
    }

    public boolean isTrapWeaponSelectorEnabled() {
        return !_onPhaseBoat;
    }
}
