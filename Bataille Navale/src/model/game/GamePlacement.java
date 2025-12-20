package model.game;

import model.grid.Grid;

public class GamePlacement {
    private Grid _playerGrid;
    private Grid _robotGrid;

    public GamePlacement(Grid playerGrid, Grid robotGrid) {
        this._playerGrid = playerGrid;
        this._robotGrid = robotGrid;
    }

    // Getters
    public Grid getPlayerGrid() {
        return _playerGrid;
    }

    public Grid getRobotGrid() {
        return _robotGrid;
    }

}