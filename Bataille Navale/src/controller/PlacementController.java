package controller;

import model.contents.fleet.Boat;
import model.enums.*;
import model.game.GameConfig;
import model.game.GamePlacement;
import model.grid.Position;
import model.placement.*;
import view.PlacementView;

import java.util.List;
import java.util.Map;

/**
 * CONTROLLER pur MVC
 * - Reçoit les événements de la vue
 * - Appelle le modèle
 * - Observe le modèle et met à jour la vue
 */
public class PlacementController implements PlacementObserver {
    private PlacementView view;
    private final PlacementModel model;

    public PlacementController(GameConfig config) {
        this.model = new PlacementModel(config);
        this.model.addObserver(this);
    }

    public void setView(PlacementView view) {
        this.view = view;
    }

    // INITIALISATION

    public void initializePlacement() {
        model.initialize();
    }

    // ÉVÉNEMENTS DE LA VUE

    public void onGridClick(int x, int y) {
        switch (model.getCurrentPhase()) {
            case BOATS:
                Orientation orient = view.isHorizontal() ? Orientation.HORIZONTAL : Orientation.VERTICAL;
                model.tryPlaceBoat(x, y, orient);
                break;
            case TRAPS:
                model.tryPlaceTrap(x, y);
                break;
            case WEAPONS:
                model.tryPlaceWeapon(x, y);
                break;
        }
    }

    public void applyFixedPlacement() {
        model.applyFixedPlacement();
    }

    public void applyRandomPlacement() {
        model.applyRandomPlacement();
    }

    public void enableManualPlacement() {
        model.enableManualPlacement();
    }

    public GamePlacement validateAndCreatePlacement() {
        String robotMode = view.getModeBoat();
        return model.validateAndCreatePlacement(robotMode);
    }

    // CALLBACKS OBSERVER (Modèle -> Vue)

    @Override
    public void onGridChanged(PlacementGrid state) {
        updateGrid(state);
    }

    @Override
    public void onPhaseChanged(PlacementPhase phase) {
        String phaseText;
        switch (phase) {
            case WEAPONS:
                phaseText = "Phase: Placement des armes";
                break;
            case TRAPS:
                phaseText = "Phase: Placement des pièges";
                break;
            default:
                phaseText = "Phase: Placement des bateaux";
        };
        view.setPhaseText(phaseText);
    }

    @Override
    public void onMessage(String message, MessageType type) {
        switch (type) {
            case INFO:
                view.setInfoText(message);
                break;
            case SUCCESS:
                view.setInfoText(message);
                view.showSuccess(message);
                break;
            case ERROR:
                view.setInfoText(message);
                view.showError(message);
                break;
        }
    }

    @Override
    public void onSelectionChanged(SelectionState state) {
        view.setBoatOptions(state.getBoatOptions().toArray(new String[0]));
        view.setTrapWeaponOptions(state.getTrapWeaponOptions().toArray(new String[0]));
        view.enableBoatSelector(state.isBoatSelectorEnabled());
        view.enableTrapWeaponSelector(state.isTrapWeaponSelectorEnabled());
    }

    // MISE À JOUR DE LA GRILLE

    public void updateGrid() {
        onGridChanged(model.getGridState());
    }

    private void updateGrid(PlacementGrid state) {
        int size = state.getGridSize();

        // Reset
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                view.setCellColor(x, y, "water");
                view.setCellText(x, y, "");
            }
        }

        // Île
        if (state.hasIsland()) {
            drawIsland(size);
        }

        // Éléments placés
        drawBoats(state);
        drawTraps(state);
        drawWeapons(state);

        // Preview
        drawPreview();
    }

    private void drawIsland(int gridSize) {
        int ix = gridSize / 2 - 2;
        int iy = gridSize / 2 - 2;
        for (int i = ix; i < ix + 4; i++) {
            for (int j = iy; j < iy + 4; j++) {
                view.setCellColor(i, j, "island");
            }
        }
    }

    private void drawBoats(PlacementGrid state) {
        for(Map.Entry<BoatName, List<Position>> entry : state.getBoats().entrySet()) {
            int size = getBoatSize(entry.getKey());
            for(Position pos : entry.getValue()) {
                for (int i = 0; i < size; i++) {
                    int x = pos.getOrientation() == Orientation.HORIZONTAL ? pos.getX() + i : pos.getX();
                    int y = pos.getOrientation() == Orientation.VERTICAL ? pos.getY() + i : pos.getY();
                    view.setCellColor(x, y, "boat");
                }
            }
        }
    }

    private void drawTraps(PlacementGrid state) {
        for(Map.Entry<TrapType, List<Position>> entry : state.getTraps().entrySet()) {
            String text = (entry.getKey() == TrapType.BLACKHOLE ? "Trou noir" : "Tornade");
            for(Position pos : entry.getValue()) {
                view.setCellColor(pos.getX(), pos.getY(), "trap");
                view.setCellText(pos.getX(), pos.getY(), text);
            }
        }
    }

    private void drawWeapons(PlacementGrid state) {
        for(Map.Entry<WeaponType, List<Position>> entry : state.getWeapons().entrySet()) {
            String text = (entry.getKey() == WeaponType.BOMB ? "Bombe" : "Sonar");
            for(Position pos : entry.getValue()) {
                view.setCellColor(pos.getX(), pos.getY(), "weapon");
                view.setCellText(pos.getX(), pos.getY(), text);
            }
        }
    }

    private void drawPreview() {
        int hx = view.getHoverX();
        int hy = view.getHoverY();
        if (hx < 0 || hy < 0) return;

        Orientation orient = view.isHorizontal() ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        PreviewInfo preview = model.getPreviewInfo(hx, hy, orient);

        if (preview != null) {
            String color = preview.isValid() ? "previewOk" : "previewBad";
            preview.getCells().forEach(pos -> {
                view.setCellColor(pos.getX(), pos.getY(), color);
            });
        }
    }

    private int getBoatSize(BoatName boatType){
        Boat boat = new Boat();
        return Boat.getBoatSize(boatType);
    }

    public boolean squareInIsland(int x, int y){
        return model.squareInIsland(x, y);
    }
}