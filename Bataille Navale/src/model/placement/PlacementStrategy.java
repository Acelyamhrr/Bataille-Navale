package model.placement;

import model.enums.BoatName;
import model.enums.TrapType;
import model.enums.WeaponType;

import java.util.List;

public interface PlacementStrategy {
    boolean placeBoats(PlacementGrid grid, List<BoatName> boats, int gridSize);
    boolean placeTraps(PlacementGrid grid, List<TrapType> traps, int gridSize);
    boolean placeWeapons(PlacementGrid grid, List<WeaponType> weapons, int gridSize);
}
