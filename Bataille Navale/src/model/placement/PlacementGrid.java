package model.placement;

import model.contents.fleet.Boat;
import model.enums.*;
import model.grid.Position;
import java.util.*;

/**
 * Représente une grille de placement avec tous les éléments placés
 * Gère l'état de placement pour un joueur (humain ou robot)
 */
public class PlacementGrid {
    private final Map<BoatName, List<Position>> boats = new HashMap<>();
    private final Map<TrapType, List<Position>> traps = new HashMap<>();
    private final Map<WeaponType, List<Position>> weapons = new HashMap<>();
    private final boolean hasIsland;
    private final int gridSize;

    public PlacementGrid(int gridSize, boolean hasIsland) {
        this.gridSize = gridSize;
        this.hasIsland = hasIsland;
    }

    // AJOUT D'ÉLÉMENTS

    public void addBoat(BoatName boat, Position position) {
        if(!boats.containsKey(boat)) {
            boats.put(boat, new ArrayList<>());
        }
        boats.get(boat).add(position);
    }

    public void addTrap(TrapType trap, Position position) {
        if(!traps.containsKey(trap)) {
            traps.put(trap, new ArrayList<>());
        }
        traps.get(trap).add(position);
    }

    public void addWeapon(WeaponType weapon, Position position) {
        if(!weapons.containsKey(weapon)){
            weapons.put(weapon, new ArrayList<>());
        }
        weapons.get(weapon).add(position);
    }

    // NETTOYAGE

    public void clear() {
        boats.clear();
        traps.clear();
        weapons.clear();
    }

    public void clearBoats() {
        boats.clear();
    }

    public void clearTraps() {
        traps.clear();
    }

    public void clearWeapons() {
        weapons.clear();
    }

    // VÉRIFICATIONS

    /**
     * Vérifie si une cellule est occupée par un bateau, piège ou arme
     */
    public boolean isCellOccupied(int x, int y) {
        // Vérifier bateaux
        for (Map.Entry<BoatName, List<Position>> entry : boats.entrySet()) {
            int boatSize = Boat.getBoatSize(entry.getKey());
            for (Position pos : entry.getValue()) {
                if (isBoatAt(pos, boatSize, x, y)) {
                    return true;
                }
            }
        }

        // Vérifier pièges
        for (List<Position> positions : traps.values()) {
            if (isPositionAt(positions, x, y)) {
                return true;
            }
        }

        // Vérifier armes
        for (List<Position> positions : weapons.values()) {
            if (isPositionAt(positions, x, y)) {
                return true;
            }
        }

        return false;
    }

    private boolean isBoatAt(Position pos, int size, int x, int y) {
        for (int i = 0; i < size; i++) {
            int bx = pos.getOrientation() == Orientation.HORIZONTAL ? pos.getX() + i : pos.getX();
            int by = pos.getOrientation() == Orientation.VERTICAL ? pos.getY() + i : pos.getY();
            if (bx == x && by == y) {
                return true;
            }
        }
        return false;
    }

    private boolean isPositionAt(List<Position> positions, int x, int y) {
        for(Position pos : positions) {
            if(pos.getX() == x && pos.getY() == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si une position est dans l'île (si elle existe)
     */
    public boolean isInIsland(int x, int y) {
        if (!hasIsland) return false;
        int ix = gridSize / 2 - 2;
        int iy = gridSize / 2 - 2;
        return x >= ix && x < ix + 4 && y >= iy && y < iy + 4;
    }

    // GETTERS

    public Map<BoatName, List<Position>> getBoats() {
        return new HashMap<>(boats);
    }

    public Map<TrapType, List<Position>> getTraps() {
        return new HashMap<>(traps);
    }

    public Map<WeaponType, List<Position>> getWeapons() {
        return new HashMap<>(weapons);
    }

    public int getBoatCount(BoatName boat) {
        if(!boats.containsKey(boat)) return 0;
        return boats.get(boat).size();
    }

    public int getTrapCount(TrapType trap) {
        if(!traps.containsKey(trap)) return 0;
        return traps.get(trap).size();
    }

    public int getWeaponCount(WeaponType weapon) {
        if(!weapons.containsKey(weapon)) return 0;
        return weapons.get(weapon).size();
    }

    public int getGridSize() {
        return gridSize;
    }

    public boolean hasIsland() {
        return hasIsland;
    }
}