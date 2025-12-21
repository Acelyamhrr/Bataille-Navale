package model.game.robotStrategy;

import model.enums.WeaponType;
import model.grid.Position;
import model.grid.Square;
import model.players.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class RobotStrategy {

    protected Random _random;

    public RobotStrategy() {
        this._random = new Random();

    }

    public WeaponType chooseWeapon(Player robot) {
        Map<WeaponType, Integer> weapons = robot.getWeapons();

        List<WeaponType> available = new ArrayList<>();
        for (Map.Entry<WeaponType, Integer> entry : weapons.entrySet()) {
            if(entry.getKey() == WeaponType.SONAR && !robot.canUseSonar()){
                continue;
            }

            if (entry.getValue() > 0) {
                available.add(entry.getKey());
            }
        }

        if (available.isEmpty()) {
            return WeaponType.MISSILE;
        }

        return available.get(_random.nextInt(available.size()));
    }

    // Choisit quelle case de l'île fouiller
    public Position chooseIslandSquareToSearch(Player player, int gridSize) {
        int ix = gridSize / 2 - 2;
        int iy = gridSize / 2 - 2;

        for (int attempt = 0; attempt < 50; attempt++) {
            int x = _random.nextInt(ix, ix + 4);
            int y = _random.nextInt(iy, iy + 4);
            Position pos = new Position(x, y);

            if (player.getGrid().getSquare(pos).isNotSearched()) {
                return pos;
            }
        }
        return null;

    }

    /**
     * Génère une position aléatoire valide (non encore attaquée).
     * Méthode utilitaire commune.
     */
    protected Position getRandomValidTarget(Player opponent, int gridSize) {
        Position target;
        Square square;

        do {
            int x = _random.nextInt(gridSize);
            int y = _random.nextInt(gridSize);
            target = new Position(x, y);
            square = opponent.getGrid().getSquare(target);
        } while (square.wasAttacked());

        return target;
    }



    // détermine la prochaine position à attaquer
    public abstract Position chooseTarget(Player robot, Player opponent, int gridSize);

    public abstract void notifyResult(Position position, boolean hit, boolean sunk);

    // Décide si le robot doit fouiller l'île ce tour-ci
    public abstract boolean shouldSearchIsland(Player robot, Player player, int gridSize);

}