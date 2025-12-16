package model.placement;

import model.enums.MessageType;
import model.enums.PlacementPhase;
import model.grid.Grid;

public interface PlacementObserver {
    void onGridChanged(Grid grid);
    void onPhaseChanged(PlacementPhase phase);
    void onMessage(String message, MessageType type);
    void onSelectionChanged(SelectionState state);
}
