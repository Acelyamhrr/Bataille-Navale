package model.game.RobotStrategy;

import model.contents.weapons.Weapon;
import model.grid.Position;
import model.grid.Square;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import model.enums.WeaponType;
import model.players.Player;

public class RandomRobotStrategy implements RobotStrategy {
    private Random random;

    public RandomRobotStrategy() {
        this.random = new Random();
    }

    @Override
    public Position chooseTarget(Player robot, Player opponent, int gridSize) {
        Position target;
        Square square;

        do {
            int x = random.nextInt(gridSize);
            int y = random.nextInt(gridSize);
            target = new Position(x,y);
            square = opponent.getGrid().getSquare(target);
        } while (square.wasAttacked());

        return target;
    }

    @Override
    public WeaponType chooseWeapon(Player robot, Position target) {
        Map<WeaponType, Integer> weapons = robot.getWeapons();

        List<WeaponType> available = new ArrayList<>();
        for (Map.Entry<WeaponType, Integer> entry : weapons.entrySet()) {
            if (entry.getValue() > 0) {
                available.add(entry.getKey());
            }
        }

        if (available.isEmpty()) {
            return WeaponType.MISSILE;
        }

        return available.get(random.nextInt(available.size()));

    }

    @Override
    public void notifyResult(Position position, boolean hit, boolean sunk) {
        // en mode aléatoire -> inutile
    }

    @Override
    public boolean shouldSearchIsland(Player robot, Player player, int gridSize) {
        // 20% de chance de fouiller l'île si elle existe
        return Math.random() < 0.2;
    }

    @Override
    public Position chooseIslandSquareToSearch(Player player, int gridSize) {
        Random rand = new Random();
        int ix = gridSize / 2 - 2;
        int iy = gridSize / 2 - 2;

        // Chercher une case non fouillée
        for (int attempt = 0; attempt < 50; attempt++) {
            int x = rand.nextInt(ix, ix + 4);
            int y = rand.nextInt(iy, iy + 4);
            Position pos = new Position(x, y);

            if (player.getGrid().getSquare(pos).isNotSearched()) {
                return pos;
            }
        }
        return null; // Toutes fouillées
    }
}

