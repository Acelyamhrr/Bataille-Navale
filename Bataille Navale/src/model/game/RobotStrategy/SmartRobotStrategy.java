package model.game.RobotStrategy;

import model.enums.WeaponType;
import model.game.RobotStrategy.RobotStrategy;
import model.grid.Position;
import model.grid.Square;
import model.players.Player;

import java.util.*;

public class SmartRobotStrategy implements RobotStrategy {

    private Random random;

    // de positions à explorer quand on a touché un bateau
    private Queue<Position> targetQueue;

    // Dernière position qui a touché (pour ajouter les adj)
    private Position lastHit;

    public SmartRobotStrategy() {
        this.random = new Random();
        this.targetQueue = new LinkedList<>();
        this.lastHit = null;
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

        // Choisir aléatoirement
        return available.get(random.nextInt(available.size()));

    }

    @Override
    public Position chooseTarget(Player robot, Player opponent, int gridSize) {

        // Si on a des cibles en attente, on les vise
        while (!targetQueue.isEmpty()) {
            Position target = targetQueue.poll();

            System.out.println("DEBUG - Testing position: " + target.getX() + "," + target.getY());

            if (isValidTarget(target, opponent, gridSize)) {
                System.out.println("DEBUG - Position VALIDE, on tire !");
                return target;
            } else {
                System.out.println("DEBUG - Position INVALIDE, on passe");
            }
        }

        // Sinon: Tir aléatoire
        Position target;
        Square square;

        do {
            int x = random.nextInt(gridSize);
            int y = random.nextInt(gridSize);
            target = new Position(x, y);
            square = opponent.getGrid().getSquare(target);
        } while (square.wasAttacked());

        return target;
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
        // Haut
        targetQueue.add(new Position(pos.getX(), pos.getY() - 1));

        // Bas
        targetQueue.add(new Position(pos.getX(), pos.getY() + 1));

        // Gauche
        targetQueue.add(new Position(pos.getX() - 1, pos.getY()));

        // Droite
        targetQueue.add(new Position(pos.getX() + 1, pos.getY()));
    }


    private boolean isValidTarget(Position pos, Player opponent, int gridSize) {
        // Hors de la grille
        if (pos.getX() < 0 || pos.getX() >= gridSize ||
                pos.getY() < 0 || pos.getY() >= gridSize) {
            return false;
        }

        // Déjà attaquée
        Square square = opponent.getGrid().getSquare(pos);

        if (square == null) {
            return false;
        }

        if (square.wasAttacked()) {
            return false;
        }

        return true;
    }
}