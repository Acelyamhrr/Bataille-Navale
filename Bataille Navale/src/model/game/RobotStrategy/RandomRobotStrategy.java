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
}
