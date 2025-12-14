package model.placement;

import model.contents.fleet.Boat;
import model.enums.*;

/**
 * Valide les placements sur la grille
 * Contient toutes les règles métier de validation
 */
public class PlacementValidator {

    /**
     * Vérifie si un bateau peut être placé à une position donnée
     */
    public boolean canPlaceBoat(PlacementGrid grid, BoatName boat, int x, int y, Orientation orient, int gridSize) {
        int size = getBoatSize(boat);

        // Vérifier que le bateau ne dépasse pas de la grille
        if (orient == Orientation.HORIZONTAL && x + size > gridSize) return false;
        if (orient == Orientation.VERTICAL && y + size > gridSize) return false;

        // Vérifier chaque cellule du bateau
        for (int i = 0; i < size; i++) {
            int cx = orient == Orientation.HORIZONTAL ? x + i : x;
            int cy = orient == Orientation.VERTICAL ? y + i : y;

            // Un bateau ne peut pas être sur l'île
            if (grid.isInIsland(cx, cy)) return false;

            // La cellule ne doit pas être déjà occupée
            if (grid.isCellOccupied(cx, cy)) return false;
        }

        return true;
    }

    /**
     * Vérifie si un piège peut être placé à une position donnée (mode standard)
     */
    public boolean canPlaceTrap(PlacementGrid grid, int x, int y, int gridSize) {
        // Vérifier les limites
        if (x >= gridSize || y >= gridSize) return false;

        // Un piège ne peut pas être sur l'île
        if (grid.isInIsland(x, y)) return false;

        // La cellule ne doit pas être occupée
        return !grid.isCellOccupied(x, y);
    }

    /**
     * Vérifie si un élément peut être placé sur l'île (mode île)
     * Utilisé pour les pièges et armes en mode île
     */
    public boolean canPlaceOnIsland(PlacementGrid grid, int x, int y, int gridSize) {
        // Vérifier les limites
        if (x >= gridSize || y >= gridSize) return false;

        // Doit être sur l'île ET la cellule ne doit pas être occupée
        return grid.isInIsland(x, y) && !grid.isCellOccupied(x, y);
    }

    private int getBoatSize(BoatName boatType){
        Boat boat = new Boat();
        return Boat.getBoatSize(boatType);
    }
}