package controller;

import model.enums.*;
import model.game.GameConfig;
import model.game.GamePlacement;
import model.grid.Grid;
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
public class PlacementController {
    private PlacementView _view;
    private final Placement _model;

    public PlacementController(GameConfig config) {
        this._model = new Placement(config);
    }

    public void setView(PlacementView view) {
        this._view = view;
        this._model.addObserver(this._view);
    }

    public Placement getModel() {
        return _model;
    }

    // INITIALISATION

    public void initializePlacement() {
        _model.initialize();
    }

    // ÉVÉNEMENTS DE LA VUE

    public void onGridClick(int x, int y) {
        switch (_model.getCurrentPhase()) {
            case BOATS:
                Orientation orient = _view.isHorizontal() ? Orientation.HORIZONTAL : Orientation.VERTICAL;
                _model.tryPlaceBoat(x, y, orient);
                break;
            case TRAPS:
                _model.tryPlaceTrap(x, y);
                break;
            case WEAPONS:
                _model.tryPlaceWeapon(x, y);
                break;
        }
    }

    public void applyFixedPlacement() {
        _model.applyFixedPlacement();
    }

    public void applyRandomPlacement() {
        _model.applyRandomPlacement();
    }

    public void enableManualPlacement() {
        _model.enableManualPlacement();
    }

    public GamePlacement validateAndCreatePlacement() {
        String robotMode = _view.getModeBoat();
        return _model.validateAndCreatePlacement(robotMode);
    }

    // MISE À JOUR DE LA GRILLE

    public void updateGrid() {
        updateGrid(_model.getGridState());
    }

    private void updateGrid(Grid grid) {
        int size = grid.getSize();

        // Reset
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                _view.setCellColor(x, y, "water");
                _view.setCellText(x, y, "");
            }
        }

        // Île
        if (grid.hasIsland()) {
            drawIsland(size);
        }

        // Éléments placés
        drawBoats(grid);
        drawTraps(grid);
        drawWeapons(grid);

        // Preview
        drawPreview();
    }

    private void drawIsland(int gridSize) {
        int ix = gridSize / 2 - 2;
        int iy = gridSize / 2 - 2;
        for (int i = ix; i < ix + 4; i++) {
            for (int j = iy; j < iy + 4; j++) {
                _view.setCellColor(i, j, "island");
            }
        }
    }

    private void drawBoats(Grid grid) {
        for(Position position : grid.getPositionsBoats()){
            _view.setCellColor(position.getX(), position.getY(), "boat");
        }
    }

    private void drawTraps(Grid grid) {
        for(Map.Entry<TrapType, List<Position>> entry : grid.getPositionsTraps().entrySet()) {
            String text = (entry.getKey() == TrapType.BLACKHOLE ? "Trou noir" : "Tornade");
            for(Position pos : entry.getValue()) {
                _view.setCellColor(pos.getX(), pos.getY(), "trap");
                _view.setCellText(pos.getX(), pos.getY(), text);
            }
        }
    }

    private void drawWeapons(Grid grid) {
        for(Map.Entry<WeaponType, List<Position>> entry : grid.getPositionsWeapons().entrySet()) {
            String text = (entry.getKey() == WeaponType.BOMB ? "Bombe" : "Sonar");
            for(Position pos : entry.getValue()) {
                _view.setCellColor(pos.getX(), pos.getY(), "weapon");
                _view.setCellText(pos.getX(), pos.getY(), text);
            }
        }
    }

    private void drawPreview() {
        int hx = _view.getHoverX();
        int hy = _view.getHoverY();
        if (hx < 0 || hy < 0) return;

        Orientation orient = _view.isHorizontal() ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        PreviewInfo preview = _model.getPreviewInfo(hx, hy, orient);

        if (preview != null) {
            String color = preview.isValid() ? "previewOk" : "previewBad";
            for(Position pos : preview.getCells()){
                _view.setCellColor(pos.getX(), pos.getY(), color);
            }
        }
    }

    public boolean squareInIsland(int x, int y){
        return _model.squareInIsland(x, y);
    }
}