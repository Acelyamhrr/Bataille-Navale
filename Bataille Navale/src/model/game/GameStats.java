package model.game;

import model.contents.fleet.Boat;
import model.grid.Position;
import model.grid.Square;
import model.players.Player;

/**
 * Calcule les statistiques d'un joueur pour l'écran de fin.
 */
public class GameStats {
    private int _boatsIntact;
    private int _boatsTouched;
    private int _boatsSunk;

    private int _missedShots;
    private int _hitCells;
    private int _totalCells;

    private int _totalBoatCells;

    private GameStats() {}

    //Calcule les stats pour un joueur donné.
    public static GameStats calculate(Player player, Player opponent, int gridSize) {
        GameStats stats = new GameStats();

        // Stats des bateaux du joueur
        stats.calculateBoatStats(player);

        // Stats des tirs sur l'adversaire
        stats.calculateShotStats(opponent, gridSize);

        return stats;
    }

    private void calculateBoatStats(Player player) {
        _boatsIntact = 0;
        _boatsTouched = 0;
        _boatsSunk = 0;
        _totalBoatCells = 0;

        for (Boat boat : player.getBoats()) {
            _totalBoatCells += boat.getSize();

            if (boat.hasSunk()) {
                _boatsSunk++;
            } else if (boat.isTouched()) {
                _boatsTouched++;
            } else {
                _boatsIntact++;
            }
        }
    }

    private void calculateShotStats(Player opponent, int gridSize) {
        _missedShots = 0;
        _hitCells = 0;
        _totalCells = 0;

        // Parcourir la grille de l'adversaire pour compter les tirs
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                Position pos = new Position(x, y);
                Square square = opponent.getGrid().getSquare(pos);

                if (square.wasAttacked()) {
                    _totalCells++;

                    if (square.isEmpty() || square.getContentType() != model.enums.ContentType.BOAT) {
                        _missedShots++;
                    } else {
                        _hitCells++;
                    }
                }
            }
        }
    }

    // GETTERS

    public int getBoatsIntact() {
        return _boatsIntact;
    }

    public int getBoatsTouched() {
        return _boatsTouched;
    }

    public int getBoatsSunk() {
        return _boatsSunk;
    }

    public int getMissedShots() {
        return _missedShots;
    }

    public int getHitCells() {
        return _hitCells;
    }

    public int getTotalCells() {
        return _totalCells;
    }

    public int getTotalBoatCells() {
        return _totalBoatCells;
    }

    /**
     * Retourne le ratio de précision (pour affichage).
     */
    public String getHitRatio() {
        return _hitCells + "/" + _totalBoatCells;
    }

    @Override
    public String toString() {
        return "GameStats{" +
                "boatsIntact=" + _boatsIntact +
                ", boatsTouched=" + _boatsTouched +
                ", boatsSunk=" + _boatsSunk +
                ", missedShots=" + _missedShots +
                ", hitRatio=" + getHitRatio() +
                '}';
    }
}