package model.placement;

import model.enums.MessageType;
import model.enums.PlacementPhase;

public interface PlacementObserver {
    void onGridChanged(PlacementGrid state);
    void onPhaseChanged(PlacementPhase phase);
    void onMessage(String message, MessageType type);
    void onSelectionChanged(SelectionState state);
}
