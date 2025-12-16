package model.placement;

import model.enums.BoatName;
import model.enums.TrapType;
import model.enums.WeaponType;
import model.grid.Grid;

import java.util.List;

/**
 * Pattern strategy : placements fixes ou aléatoires
 */
public interface PlacementStrategy {
    boolean placeBoats(Grid grid, List<BoatName> boats);
    boolean placeTraps(Grid grid, List<TrapType> traps);
    boolean placeWeapons(Grid grid, List<WeaponType> weapons);
}
