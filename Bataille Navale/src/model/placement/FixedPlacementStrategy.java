package model.placement;

import model.contents.fleet.Boat;
import model.contents.traps.Trap;
import model.contents.weapons.Weapon;
import model.enums.BoatName;
import model.enums.Orientation;
import model.enums.TrapType;
import model.enums.WeaponType;
import model.grid.Grid;
import model.grid.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FixedPlacementStrategy implements PlacementStrategy{

    public FixedPlacementStrategy() {}

    @Override
    public boolean placeBoats(Grid grid, List<BoatName> boats) {
        if (grid.hasIsland()) {
            return placeBoatsAroundIsland(grid, boats);
        } else {
            return placeBoatsBalanced(grid, boats);
        }
    }

    private boolean placeBoatsAroundIsland(Grid grid, List<BoatName> boats) {
        int margin = (grid.getSize() - grid.getSizeIsland()) / 2;

        for (BoatName boat : boats) {
            // Essayer lignes du haut
            boolean placed = tryPlaceInZone(grid, boat, 0, margin, 0, grid.getSize(), Orientation.HORIZONTAL);

            // Essayer lignes du bas
            if (!placed) {
                placed = tryPlaceInZone(grid, boat, grid.getSize() - margin, grid.getSize(), 0, grid.getSize(), Orientation.HORIZONTAL);
            }

            // Essayer colonnes gauche
            if (!placed) {
                placed = tryPlaceInZone(grid, boat, 0, grid.getSize(), 0, margin, Orientation.VERTICAL);
            }

            // Essayer colonnes droite
            if (!placed) {
                placed = tryPlaceInZone(grid, boat, 0, grid.getSize(), grid.getSize() - margin, grid.getSize(), Orientation.VERTICAL);
            }

            if (!placed) return false;
        }
        return true;
    }

    private boolean placeBoatsBalanced(Grid grid, List<BoatName> boats) {
        List<Position> startPositions = getBalancedStartPositions(grid.getSize());
        int startIndex = 0;

        for (BoatName boat : boats) {
            Position start = startPositions.get(startIndex % startPositions.size());
            startIndex++;

            boolean placed = tryPlaceFromStart(grid, boat, start);
            if (!placed) return false;
        }
        return true;
    }

    private boolean tryPlaceInZone(Grid grid, BoatName boatName, int yStart, int yEnd, int xStart, int xEnd, Orientation orient) {
        for (int y = yStart; y < yEnd; y++) {
            for (int x = xStart; x < xEnd; x++) {
                if (grid.canPlaceBoat(Boat.getBoatSize(boatName), x, y, orient)) {
                    Boat boat = Placement.createBoat(boatName);
                    grid.placeBoat(boat, x, y, orient);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tryPlaceFromStart(Grid grid, BoatName boat, Position start) {
        // Essayer horizontal
        if (tryPlaceInDirection(grid, boat, start, Orientation.HORIZONTAL)) {
            return true;
        }
        // Essayer vertical
        return tryPlaceInDirection(grid, boat, start, Orientation.VERTICAL);
    }

    private boolean tryPlaceInDirection(Grid grid, BoatName boatName, Position start, Orientation orient) {
        int boatSize = Boat.getBoatSize(boatName);
        int maxX = orient == Orientation.HORIZONTAL ? grid.getSize() - boatSize : grid.getSize() - 1;
        int maxY = orient == Orientation.VERTICAL ? grid.getSize() - boatSize : grid.getSize() - 1;

        for (int dy = 0; dy <= maxY; dy++) {
            for (int dx = 0; dx <= maxX; dx++) {
                int x = (start.getX() + dx) % grid.getSize();
                int y = (start.getY() + dy) % grid.getSize();

                if (grid.canPlaceBoat(boatSize, x, y, orient)) {
                    Boat boat = Placement.createBoat(boatName);
                    grid.placeBoat(boat, x, y, orient);
                    return true;
                }
            }
        }
        return false;
    }

    private List<Position> getBalancedStartPositions(int gridSize) {
        int mid = gridSize / 2;
        List<Position> starts = new ArrayList<>();
        starts.add(new Position(0, 0, Orientation.HORIZONTAL));
        starts.add(new Position(gridSize - 1, gridSize - 1, Orientation.VERTICAL));
        starts.add(new Position(gridSize - 1, 0, Orientation.VERTICAL));
        starts.add(new Position(0, gridSize - 1, Orientation.HORIZONTAL));
        starts.add(new Position(mid, mid, Orientation.HORIZONTAL));
        return starts;
    }

    @Override
    public boolean placeTraps(Grid grid, List<TrapType> traps) {
        int x = 3, y = 3;
        for (TrapType trapType : traps) {
            if (grid.canPlaceTrapWeapon(x + 1, y + 1)) {
                Trap trap = Placement.createTrap(trapType);
                grid.placeTrap(trap, x + 1, y + 1);
                x++; y++;
            } else if (grid.canPlaceTrapWeapon(x + 2, y + 2)) {
                Trap trap = Placement.createTrap(trapType);
                grid.placeTrap(trap, x + 2, y + 2);
                x += 2; y += 2;
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean placeWeapons(Grid grid, List<WeaponType> weapons) {
        // Pour l'instant, placement simple sur l'île
        Random rand = new Random();
        Position posIsland = grid.getPositionIsland();

        for (WeaponType weaponType : weapons) {
            boolean placed = false;
            for (int attempts = 0; attempts < 100 && !placed; attempts++) {
                int x = rand.nextInt(posIsland.getX(), posIsland.getX() + 4);
                int y = rand.nextInt(posIsland.getY(), posIsland.getY() + 4);

                if (grid.hasIsland() && grid.canPlaceTrapWeapon(x, y)) {
                    Weapon weapon = Placement.createWeapon(weaponType);
                    grid.placeWeapon(weapon, x, y);
                    placed = true;
                }
            }
            if (!placed) return false;
        }
        return true;
    }
}
