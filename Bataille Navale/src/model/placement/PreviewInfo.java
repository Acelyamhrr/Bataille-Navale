package model.placement;

import model.grid.Position;

import java.util.List;

/**
 * Utilisé pour les previews (transmis au controller)
 */
public class PreviewInfo {
    private List<Position> cells;
    private boolean canPlace;

    public PreviewInfo(List<Position> cells, boolean canPlace) {
        this.cells = cells;
        this.canPlace = canPlace;
    }

    public List<Position> getCells() {
        return cells;
    }

    public boolean isValid() {
        return canPlace && !cells.isEmpty();
    }
}
