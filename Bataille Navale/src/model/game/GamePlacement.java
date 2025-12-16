package model.game;

import model.grid.Grid;

public class GamePlacement {
    private Grid playerGrid;
    private Grid robotGrid;

    public GamePlacement(Grid playerGrid, Grid robotGrid) {
        this.playerGrid = playerGrid;
        this.robotGrid = robotGrid;
    }

    // Getters
    public Grid getPlayerGrid() {
        return playerGrid;
    }

    public Grid getRobotGrid() {
        return robotGrid;
    }

}