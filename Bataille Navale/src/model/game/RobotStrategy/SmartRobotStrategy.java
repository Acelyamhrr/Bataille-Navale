package model.game.RobotStrategy;

import model.enums.WeaponType;
import model.game.RobotStrategy.RobotStrategy;
import model.grid.Position;
import model.grid.Square;
import model.players.Player;

import java.util.*;

public class SmartRobotStrategy extends RobotStrategy {
    // positions à explorer quand on a touché un bateau
    private Queue<Position> targetQueue;

    // Dernière position qui a touché (pour ajouter les adj)
    private Position lastHit;

    public SmartRobotStrategy() {
        super();
        this.targetQueue = new LinkedList<>();
        this.lastHit = null;
    }

    @Override
    public Position chooseTarget(Player robot, Player opponent, int gridSize) {

        // Si on a des cibles en attente, on les vise
        while (!targetQueue.isEmpty()) {
            Position target = targetQueue.poll();

            if (isValidTarget(target, opponent, gridSize)) {
                return target;
            }
        }

        // Sinon: Tir aléatoire
        return getRandomValidTarget(opponent, gridSize);
    }

    @Override
    public void notifyResult(Position position, boolean hit, boolean sunk) {
        if (sunk) {
            // Bateau coulé on vide la queue et on recommence en mode aléatoire
            targetQueue.clear();
            lastHit = null;

        } else if (hit) {
            // Bateau touché on ajoute les 4 cases adjacentes à explorer
            lastHit = position;
            addAdjacentPositions(position);
        }
    }

    private void addAdjacentPositions(Position pos) {
        targetQueue.add(new Position(pos.getX(), pos.getY() - 1));  // haut
        targetQueue.add(new Position(pos.getX(), pos.getY() + 1));  // bas
        targetQueue.add(new Position(pos.getX() - 1, pos.getY()));  // gauche
        targetQueue.add(new Position(pos.getX() + 1, pos.getY()));  // droite
    }

    /**
     * Vérifie si une position est une cible valide (dans la grille et non attaquée).
     * Méthode utilitaire commune.
     */
    protected boolean isValidTarget(Position pos, Player opponent, int gridSize) {
        // Vérifier les limites de la grille
        if (pos.getX() < 0 || pos.getX() >= gridSize ||
                pos.getY() < 0 || pos.getY() >= gridSize) {
            return false;
        }

        // Vérifier si déjà attaquée
        Square square = opponent.getGrid().getSquare(pos);
        return square != null && !square.wasAttacked();
    }


    @Override
    public boolean shouldSearchIsland(Player robot, Player player, int gridSize) {
        // Plus intelligent : fouille si peu d'armes
        int bombCount = robot.getWeaponCount(WeaponType.BOMB);
        int sonarCount = robot.getWeaponCount(WeaponType.SONAR);

        // Fouille si on a moins de 1 armes spéciales
        return (bombCount + sonarCount) < 1;
    }
}