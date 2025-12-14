package model.game;

import model.contents.fleet.Boat;
import model.grid.Position;
import model.grid.Square;
import model.players.Player;

/**
 * Calcule les statistiques d'un joueur pour l'écran de fin.
 */
public class GameStats {
    private int boatsIntact;
    private int boatsTouched;
    private int boatsSunk;
    private int missedShots;
    private int hitCells;
    private int totalCells;

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
        boatsIntact = 0;
        boatsTouched = 0;
        boatsSunk = 0;

        for (Boat boat : player.getBoats()) {
            if (boat.hasSunk()) {
                boatsSunk++;
            } else if (boat.isTouched()) {
                // Si le bateau a été touché au moins une fois mais pas coulé
                boatsTouched++;
            } else {
                boatsIntact++;
            }
        }
    }

    private void calculateShotStats(Player opponent, int gridSize) {
        missedShots = 0;
        hitCells = 0;
        totalCells = 0;

        // Parcourir la grille de l'adversaire pour compter les tirs
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                Position pos = new Position(x, y);
                Square square = opponent.getGrid().getSquare(pos);

                if (square.wasAttacked()) {
                    totalCells++;

                    if (square.isEmpty() || square.getContentType() != model.enums.ContentType.BOAT) {
                        missedShots++;
                    } else {
                        hitCells++;
                    }
                }
            }
        }
    }

    // GETTERS

    public int getBoatsIntact() {
        return boatsIntact;
    }

    public int getBoatsTouched() {
        return boatsTouched;
    }

    public int getBoatsSunk() {
        return boatsSunk;
    }

    public int getMissedShots() {
        return missedShots;
    }

    public int getHitCells() {
        return hitCells;
    }

    public int getTotalCells() {
        return totalCells;
    }

    /**
     * Retourne le ratio de précision (pour affichage).
     */
    public String getHitRatio() {
        return hitCells + "/" + totalCells;
    }

    @Override
    public String toString() {
        return "GameStats{" +
                "boatsIntact=" + boatsIntact +
                ", boatsTouched=" + boatsTouched +
                ", boatsSunk=" + boatsSunk +
                ", missedShots=" + missedShots +
                ", hitRatio=" + getHitRatio() +
                '}';
    }
}