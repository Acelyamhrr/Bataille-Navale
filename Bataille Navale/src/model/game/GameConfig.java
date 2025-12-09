package model.game;

import model.enums.ModeGame;
import model.enums.RobotMode;
import model.enums.TrapPlacement;

public class GameConfig {
    private int gridSize;
    private String username;
    private int[] numberBoat;        // [porte-avions, croiseur, destroyer, sous-marin, torpilleur]
    private ModeGame modeGame;
    private RobotMode robotMode;
    private TrapPlacement trapPlacement;

    public GameConfig(ModeGame mode, int gridSize, RobotMode robotMode, int[] numberBoat, String username, TrapPlacement trapMode) {
        this.modeGame = mode;
        this.gridSize = gridSize;
        this.robotMode = robotMode;
        this.numberBoat = numberBoat.clone();
        this.username = username;
        this.trapPlacement = trapMode;
    }

    public int getGridSize() { return gridSize; }
    public String getUsername() { return username; }
    public int[] getNumberBoat() { return numberBoat.clone(); }
    public ModeGame getModeGame() { return modeGame; }
    public RobotMode getRobotMode() { return robotMode; }
    public TrapPlacement getTrapMode() { return trapPlacement; }

    public int getTotalBoatSquares() {
        int[] sizes = {5, 4, 3, 3, 2};
        int total = 0;
        for (int i = 0; i < numberBoat.length; i++) {
            total += numberBoat[i] * sizes[i];
        }
        return total;
    }

    public int getNumberBoatsTotal(){
        int total = 0;
        for (int j : numberBoat) {
            total += j;
        }
        return total;
    }

    public boolean isValid() {
        // trop de cases
        if (getTotalBoatSquares() > 35) {
            return false;
        }

        // Au moins un boat
        int totalBoats = 0;
        for (int nb : numberBoat) {
            totalBoats += nb;
        }
        if (totalBoats == 0) {
            return false;
        }

        // si username pas remplis
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        // sinon tout est OK
        return true;
    }

    public String toString() {
        return "Config: " + username + ", " + gridSize + "x" + gridSize + ", jeu " + modeGame + ", robot " + robotMode + ", pièges " + trapPlacement;
    }
}