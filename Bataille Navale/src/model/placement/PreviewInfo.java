package model.placement;

import model.grid.Position;

import java.util.List;

/**
 * Utilisé pour les previews (transmis à la vue)
 */
public class PreviewInfo {
    private List<Position> _cells;
    private boolean _canPlace;

    public PreviewInfo(List<Position> cells, boolean canPlace) {
        this._cells = cells;
        this._canPlace = canPlace;
    }

    public List<Position> getCells() {
        return _cells;
    }

    public boolean isValid() {
        return _canPlace && !_cells.isEmpty();
    }
}
