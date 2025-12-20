package model.placement;

import model.contents.fleet.Boat;
import model.contents.traps.Trap;
import model.contents.weapons.Weapon;
import model.enums.BoatName;
import model.enums.Orientation;
import model.enums.TrapType;
import model.enums.WeaponType;
import model.grid.Grid;

import java.util.List;
import java.util.Random;

public class RandomPlacementStrategy implements PlacementStrategy{
    private final Random _rand = new Random();

    public RandomPlacementStrategy() {}

    @Override
    public boolean placeBoats(Grid grid, List<BoatName> boats) {
        for (BoatName boat : boats) {
            if (!tryPlaceBoatRandomly(grid, boat)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean placeTraps(Grid grid, List<TrapType> traps) {
        for (TrapType trap : traps) {
            if (!tryPlaceTrapRandomly(grid, trap)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean placeWeapons(Grid grid, List<WeaponType> weapons) {
        for (WeaponType weapon : weapons) {
            if (!tryPlaceWeaponRandomly(grid, weapon)) {
                return false;
            }
        }
        return true;
    }

    private boolean tryPlaceBoatRandomly(Grid grid, BoatName boatName) {
        for (int attempts = 0; attempts < 100; attempts++) {
            int x = _rand.nextInt(grid.getSize());
            int y = _rand.nextInt(grid.getSize());
            Orientation o = _rand.nextBoolean() ? Orientation.HORIZONTAL : Orientation.VERTICAL;

            if (grid.canPlaceBoat(Boat.getBoatSize(boatName), x, y, o)) {
                Boat boat = Placement.createBoat(boatName);
                grid.placeBoat(boat, x, y, o);
                return true;
            }
        }
        return false;
    }

    private boolean tryPlaceTrapRandomly(Grid grid, TrapType trapType) {
        for (int attempts = 0; attempts < 100; attempts++) {
            int x = _rand.nextInt(grid.getSize());
            int y = _rand.nextInt(grid.getSize());

            if(grid.hasIsland()){
                if(grid.canPlaceTrapWeapon(x, y)){
                    Trap trap = Placement.createTrap(trapType);
                    grid.placeTrap(trap, x, y);
                    return true;
                }
            }
            else {
                if (grid.canPlaceTrapWeapon(x, y)) {
                    Trap trap = Placement.createTrap(trapType);
                    grid.placeTrap(trap, x, y);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tryPlaceWeaponRandomly(Grid grid, WeaponType weaponType) {
        for (int attempts = 0; attempts < 100; attempts++) {
            int x = _rand.nextInt(grid.getSize());
            int y = _rand.nextInt(grid.getSize());

            if (grid.hasIsland() && grid.canPlaceTrapWeapon(x, y)) {
                Weapon weapon = Placement.createWeapon(weaponType);
                grid.placeWeapon(weapon, x, y);
                return true;
            }
        }
        return false;
    }
}
