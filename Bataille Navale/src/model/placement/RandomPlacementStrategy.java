package model.placement;

import model.enums.BoatName;
import model.enums.Orientation;
import model.enums.TrapType;
import model.enums.WeaponType;
import model.grid.Position;

import java.util.List;
import java.util.Random;

public class RandomPlacementStrategy implements PlacementStrategy{
    private final Random rand = new Random();
    private final PlacementValidator validator;
    private final boolean hasIsland;

    public RandomPlacementStrategy(PlacementValidator validator, boolean hasIsland) {
        this.validator = validator;
        this.hasIsland = hasIsland;
    }

    @Override
    public boolean placeBoats(PlacementGrid grid, List<BoatName> boats, int gridSize) {
        for (BoatName boat : boats) {
            if (!tryPlaceBoatRandomly(grid, boat, gridSize)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean placeTraps(PlacementGrid grid, List<TrapType> traps, int gridSize) {
        for (TrapType trap : traps) {
            if (!tryPlaceTrapRandomly(grid, trap, gridSize)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean placeWeapons(PlacementGrid grid, List<WeaponType> weapons, int gridSize) {
        for (WeaponType weapon : weapons) {
            if (!tryPlaceWeaponRandomly(grid, weapon, gridSize)) {
                return false;
            }
        }
        return true;
    }

    private boolean tryPlaceBoatRandomly(PlacementGrid grid, BoatName boat, int gridSize) {
        for (int attempts = 0; attempts < 100; attempts++) {
            int x = rand.nextInt(gridSize);
            int y = rand.nextInt(gridSize);
            Orientation o = rand.nextBoolean() ? Orientation.HORIZONTAL : Orientation.VERTICAL;

            if (validator.canPlaceBoat(grid, boat, x, y, o, gridSize)) {
                grid.addBoat(boat, new Position(x, y, o));
                return true;
            }
        }
        return false;
    }

    private boolean tryPlaceTrapRandomly(PlacementGrid grid, TrapType trap, int gridSize) {
        for (int attempts = 0; attempts < 100; attempts++) {
            int x = rand.nextInt(gridSize);
            int y = rand.nextInt(gridSize);

            if(hasIsland){
                if(validator.canPlaceOnIsland(grid, x, y, gridSize)){
                    grid.addTrap(trap, new Position(x, y));
                    return true;
                }
            }
            else {
                if (validator.canPlaceTrap(grid, x, y, gridSize)) {
                    grid.addTrap(trap, new Position(x, y));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tryPlaceWeaponRandomly(PlacementGrid grid, WeaponType weapon, int gridSize) {
        for (int attempts = 0; attempts < 100; attempts++) {
            int x = rand.nextInt(gridSize);
            int y = rand.nextInt(gridSize);

            if (validator.canPlaceOnIsland(grid, x, y, gridSize)) {
                grid.addWeapon(weapon, new Position(x, y));
                return true;
            }
        }
        return false;
    }
}
