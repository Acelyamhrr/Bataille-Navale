package model.game;

import model.enums.ModeGame;
import model.enums.RobotMode;
import model.enums.TrapPlacement;

public class GameConfig {
    private int _gridSize;
    private String _username;
    private int[] _numberBoat;        // [porte-avions, croiseur, destroyer, sous-marin, torpilleur]
    private ModeGame _modeGame;
    private RobotMode _robotMode;
    private TrapPlacement _trapPlacement;

    public GameConfig(ModeGame mode, int gridSize, RobotMode robotMode, int[] numberBoat, String username, TrapPlacement trapMode) {
        this._modeGame = mode;
        this._gridSize = gridSize;
        this._robotMode = robotMode;
        this._numberBoat = numberBoat.clone();
        this._username = username;
        this._trapPlacement = trapMode;
    }

    public int getGridSize() { return _gridSize; }
    public String getUsername() { return _username; }
    public int[] getNumberBoat() { return _numberBoat.clone(); }
    public ModeGame getModeGame() { return _modeGame; }
    public RobotMode getRobotMode() { return _robotMode; }
    public TrapPlacement getTrapMode() { return _trapPlacement; }

    public static int totalBoatSquares(int[] boats) {
        int[] sizes = {5, 4, 3, 3, 2};
        int total = 0;
        for (int i = 0; i < boats.length; i++) {
            total += boats[i] * sizes[i];
        }
        return total;
    }

    public int getTotalBoatSquares() {
        return totalBoatSquares(_numberBoat);
    }

    public int getNumberBoatsTotal(){
        int total = 0;
        for (int j : _numberBoat) {
            total += j;
        }
        return total;
    }

    public boolean isValid() {
        //Vérifie le nombre de cases des bateaux
        if(!numberSquaresValid(_gridSize, _numberBoat, _modeGame == ModeGame.ISLAND)){
            return false;
        }

        // Au moins un boat
        int totalBoats = 0;
        for (int nb : _numberBoat) {
            totalBoats += nb;
        }
        if (totalBoats == 0) {
            return false;
        }

        // si username pas remplis
        if (_username == null || _username.trim().isEmpty()) {
            return false;
        }

        // sinon tout est OK
        return true;
    }

    public String toString() {
        return "Config: " + _username + ", " + _gridSize + "x" + _gridSize + ", jeu " + _modeGame + ", robot " + _robotMode + ", pièges " + _trapPlacement;
    }

    public static boolean numberSquaresValid(int gridSize, int[] boats, boolean island) {
        int totalSquares = totalBoatSquares(boats);

        if(totalSquares > 35){
            return false;
        }

        if(island){
            int sizeIsland = 16;

            if (gridSize == 7) {
                return totalSquares <= 49 - sizeIsland;
            } else if (gridSize == 6) {
                return totalSquares <= 36 - sizeIsland;
            }
        }
        else{
            if(gridSize == 6 && totalSquares >34) {
                return false;
            }
        }


        return true;
    }
}