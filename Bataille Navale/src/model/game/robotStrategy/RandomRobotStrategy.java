package model.game.robotStrategy;

import model.grid.Position;

import model.players.Player;

public class RandomRobotStrategy extends RobotStrategy {

    @Override
    public Position chooseTarget(Player robot, Player opponent, int gridSize) {
        return getRandomValidTarget(opponent, gridSize);
    }


    @Override
    public void notifyResult(Position position, boolean hit, boolean sunk) {
        // en mode aléatoire -> inutile
    }

    @Override
    public boolean shouldSearchIsland(Player robot, Player player, int gridSize) {
        // fouiller l'île si elle existe
        return Math.random() < 0.05;    // 5% de chance
    }
}

