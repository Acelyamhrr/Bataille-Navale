package model.game.RobotStrategy;

import model.enums.WeaponType;
import model.grid.Position;
import model.players.Player;

public interface RobotStrategy {

    // détermine la prochaine position a attaquer
    Position chooseTarget(Player robot, Player opponent, int gridSize);

    // choisit l'arme à utiliser
    WeaponType chooseWeapon(Player robot, Position target);

    void notifyResult(Position position, boolean hit, boolean sunk);

    // Décide si le robot doit fouiller l'île ce tour-ci
    public boolean shouldSearchIsland(Player robot, Player player, int gridSize);

    // Choisit quelle case de l'île fouiller
    public Position chooseIslandSquareToSearch(Player player, int gridSize);
}
