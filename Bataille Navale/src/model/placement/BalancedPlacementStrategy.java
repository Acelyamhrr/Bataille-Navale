package model.placement;

import model.contents.fleet.Boat;
import model.enums.BoatName;
import model.enums.Orientation;
import model.enums.TrapType;
import model.enums.WeaponType;
import model.grid.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BalancedPlacementStrategy implements PlacementStrategy{
    private final PlacementValidator validator;
    private final boolean hasIsland;

    public BalancedPlacementStrategy(PlacementValidator validator, boolean hasIsland) {
        this.validator = validator;
        this.hasIsland = hasIsland;
    }

    @Override
    public boolean placeBoats(PlacementGrid grid, List<BoatName> boats, int gridSize) {
        if (hasIsland) {
            return placeBoatsAroundIsland(grid, boats, gridSize);
        } else {
            return placeBoatsBalanced(grid, boats, gridSize);
        }
    }

    private boolean placeBoatsAroundIsland(PlacementGrid grid, List<BoatName> boats, int gridSize) {
        int islandSize = 4;
        int margin = (gridSize - islandSize) / 2;

        for (BoatName boat : boats) {
            // Essayer lignes du haut
            boolean placed = tryPlaceInZone(grid, boat, 0, margin, 0, gridSize, Orientation.HORIZONTAL, gridSize);

            // Essayer lignes du bas
            if (!placed) {
                placed = tryPlaceInZone(grid, boat, gridSize - margin, gridSize, 0, gridSize, Orientation.HORIZONTAL, gridSize);
            }

            // Essayer colonnes gauche
            if (!placed) {
                placed = tryPlaceInZone(grid, boat, 0, gridSize, 0, margin, Orientation.VERTICAL, gridSize);
            }

            // Essayer colonnes droite
            if (!placed) {
                placed = tryPlaceInZone(grid, boat, 0, gridSize, gridSize - margin, gridSize, Orientation.VERTICAL, gridSize);
            }

            if (!placed) return false;
        }
        return true;
    }

    private boolean placeBoatsBalanced(PlacementGrid grid, List<BoatName> boats, int gridSize) {
        List<Position> startPositions = getBalancedStartPositions(gridSize);
        int startIndex = 0;

        for (BoatName boat : boats) {
            Position start = startPositions.get(startIndex % startPositions.size());
            startIndex++;

            boolean placed = tryPlaceFromStart(grid, boat, start, gridSize);
            if (!placed) return false;
        }
        return true;
    }

    private boolean tryPlaceInZone(PlacementGrid grid, BoatName boat, int yStart, int yEnd, int xStart, int xEnd, Orientation orient, int gridSize) {
        for (int y = yStart; y < yEnd; y++) {
            for (int x = xStart; x < xEnd; x++) {
                if (validator.canPlaceBoat(grid, boat, x, y, orient, gridSize)) {
                    grid.addBoat(boat, new Position(x, y, orient));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tryPlaceFromStart(PlacementGrid grid, BoatName boat, Position start, int gridSize) {
        // Essayer horizontal
        if (tryPlaceInDirection(grid, boat, start, Orientation.HORIZONTAL, gridSize)) {
            return true;
        }
        // Essayer vertical
        return tryPlaceInDirection(grid, boat, start, Orientation.VERTICAL, gridSize);
    }

    private boolean tryPlaceInDirection(PlacementGrid grid, BoatName boat, Position start, Orientation orient, int gridSize) {
        int boatSize = getBoatSize(boat);
        int maxX = orient == Orientation.HORIZONTAL ? gridSize - boatSize : gridSize - 1;
        int maxY = orient == Orientation.VERTICAL ? gridSize - boatSize : gridSize - 1;

        for (int dy = 0; dy <= maxY; dy++) {
            for (int dx = 0; dx <= maxX; dx++) {
                int x = (start.getX() + dx) % gridSize;
                int y = (start.getY() + dy) % gridSize;

                if (validator.canPlaceBoat(grid, boat, x, y, orient, gridSize)) {
                    grid.addBoat(boat, new Position(x, y, orient));
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
    public boolean placeTraps(PlacementGrid grid, List<TrapType> traps, int gridSize) {
        int x = 3, y = 3;
        for (TrapType trap : traps) {
            if (validator.canPlaceTrap(grid, x + 1, y + 1, gridSize)) {
                grid.addTrap(trap, new Position(x + 1, y + 1));
                x++; y++;
            } else if (validator.canPlaceTrap(grid, x + 2, y + 2, gridSize)) {
                grid.addTrap(trap, new Position(x + 2, y + 2));
                x += 2; y += 2;
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean placeWeapons(PlacementGrid grid, List<WeaponType> weapons, int gridSize) {
        // Pour l'instant, placement simple sur l'île
        Random rand = new Random();
        int ix = gridSize / 2 - 2;
        int iy = gridSize / 2 - 2;

        for (WeaponType weapon : weapons) {
            boolean placed = false;
            for (int attempts = 0; attempts < 100 && !placed; attempts++) {
                int x = rand.nextInt(ix, ix + 4);
                int y = rand.nextInt(iy, iy + 4);

                if (validator.canPlaceOnIsland(grid, x, y, gridSize)) {
                    grid.addWeapon(weapon, new Position(x, y));
                    placed = true;
                }
            }
            if (!placed) return false;
        }
        return true;
    }

    private int getBoatSize(BoatName boatType){
        Boat boat = new Boat();
        return Boat.getBoatSize(boatType);
    }
}
