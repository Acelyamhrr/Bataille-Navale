package model.players;

import model.contents.Content;
import model.contents.fleet.Boat;
import model.contents.traps.Tornado;
import model.contents.traps.Trap;
import model.contents.weapons.Weapon;
import model.enums.ContentType;
import model.enums.ModeGame;
import model.enums.TrapType;
import model.enums.WeaponType;
import model.game.RobotStrategy.RobotStrategy;
import model.game.TrapActivation;
import model.grid.Grid;
import model.grid.Position;
import model.grid.Square;

import java.util.*;

public class Player {
    private String username;
    private boolean isRobot;
    private Map<WeaponType, Integer> weapons;
    private Grid grid;
    private Tornado tornado;
    private List<Boat> boats;
    private RobotStrategy strategy;
    private Map<TrapType, Integer> trapInventory;

    public Player(String username, Boolean robot) {
        this.username = username;
        this.isRobot = robot;
        this.weapons = new HashMap<>();
        this.boats = new ArrayList<>();
        this.tornado = null;
        this.strategy = null;
        this.trapInventory = new HashMap<>();
    }

    // INIT

    public void createGrid(int size, ModeGame mode) {
        this.grid = new Grid(size, mode, isRobot);
    }

    public void placeBoat(Boat boat, Position position) {
        grid.placeBoat(boat, position.getX(), position.getY(), position.getOrientation());
        boats.add(boat);
    }

    public void placeTrap(Trap trap, Position position) {
        grid.placeTrap(trap, position.getX(), position.getY());

        if (trap.getName() == TrapType.TORNADO) {
            this.tornado = (Tornado) trap;
        }
    }

    public void placeWeaponOnIsland(Weapon weapon, Position position) {
        grid.placeWeapon(weapon, position.getX(), position.getY());
    }

    // ARMES

    public boolean hasWeapon(WeaponType type) {
        return getWeaponCount(type) > 0;
    }

    public void useWeapon(WeaponType type) {
        int count = getWeaponCount(type);
        if (count <= 0) {
            throw new IllegalStateException("Pas d'arme de type " + type);
        }
        // Le missile est illimité, ne pas décrémenter
        if (type != WeaponType.MISSILE) {
            weapons.put(type, count - 1);
        }
    }

    public void addWeapon(WeaponType type) {
        weapons.put(type, getWeaponCount(type) + 1);
    }

    public int getWeaponCount(WeaponType type) {
        return weapons.getOrDefault(type, 0);
    }

    public void setWeaponCount(WeaponType type, int count) {
        weapons.put(type, count);
    }

    public Map<WeaponType, Integer> getWeapons() {
        return new HashMap<>(weapons);
    }

    // PIEGES

    public void addTrapToInventory(TrapType type) {
        trapInventory.put(type, getTrapInventoryCount(type) + 1);
    }

    public int getTrapInventoryCount(TrapType type) {
        return trapInventory.getOrDefault(type, 0);
    }

    public void removeTrapFromInventory(TrapType type) {
        int count = getTrapInventoryCount(type);
        if (count > 0) {
            trapInventory.put(type, count - 1);
        }
    }

    public Map<TrapType, Integer> getTrapInventory() {
        return new HashMap<>(trapInventory);
    }

    public boolean canPlaceTrapAt(Position position) {
        if (grid == null) {
            return false;
        }

        Square square = grid.getSquare(position);

        // Case doit être vide et pas sur l'île
        return square.isEmpty() && !square.isInIsland();
    }

    public boolean placeTrapFromInventory(TrapType type, Position position, Trap trap) {
        if (getTrapInventoryCount(type) <= 0) {
            return false;
        }

        if (!canPlaceTrapAt(position)) {
            return false;
        }

        // Placer le piège
        grid.placeTrap(trap, position.getX(), position.getY());
        removeTrapFromInventory(type);

        // Si c'est une tornade, la définir comme active
        if (type == TrapType.TORNADO) {
            this.tornado = (Tornado) trap;
        }

        return true;
    }

    // tornade

    public boolean hasTornadoActive() {
        return tornado != null && tornado.isActive();
    }

    public Position applyTornado(Position target) {
        if (hasTornadoActive()) {
            return tornado.getNewPosition(target);
        }
        return target;
    }

    public void setTornado(Tornado tornado) {
        this.tornado = tornado;
    }

    public Tornado getTornado() {
        return tornado;
    }

    // ATTAUQES ET DEFENSES

    public void receiveAttack(Position position) {
        if (grid != null) {
            grid.attack(position);
        }
    }

    public boolean receiveAttackAndCheckHit(Position position) {
        Square square = grid.getSquare(position);
        boolean wasHit = !square.isEmpty() && square.getContentType() == ContentType.BOAT;
        receiveAttack(position);
        return wasHit;
    }

    public boolean wasBoatSunkAt(Position position) {
        Square square = grid.getSquare(position);
        if (square.getContentType() == ContentType.BOAT) {
            Boat boat = (Boat) square.getContent();
            return boat.hasSunk();
        }
        return false;
    }

    // verif si un piège est présent et l'active si il faut
    public TrapActivation checkAndActivateTrap(Position position, int gridSize) {
        Square square = grid.getSquare(position);

        if (!square.isEmpty() && square.getContentType() == ContentType.TRAP) {
            Trap trap = (Trap) square.getContent();

            if (trap.getName() == TrapType.TORNADO) {
                Tornado tornado = (Tornado) trap;
                if (!tornado.isActive()) {
                    tornado.activate(gridSize);
                    return new TrapActivation(TrapType.TORNADO, position, false);
                }
            } else if (trap.getName() == TrapType.BLACKHOLE) {
                // Désactiver le trou noir après utilisation
                square.setContent(null);
                return new TrapActivation(TrapType.BLACKHOLE, position, true);
            }
        }

        return null;
    }

    public boolean allBoatSunk() {
        for (Boat boat : boats) {
            if (!boat.hasSunk()) {
                return false;
            }
        }
        return true;
    }

    // ILE

    public boolean isSquareOnIsland(Position position) {
        return grid.squareIsInIsland(position);
    }

    public boolean wasSquareSearched(Position position) {
        Square square = grid.getSquare(position);
        return !square.isNotSearched();
    }

    public Content searchIsland(Position position) {
        Square square = grid.getSquare(position);
        return square.search();
    }

    public boolean wasSquareAttacked(Position position) {
        Square square = grid.getSquare(position);
        return square.wasAttacked();
    }

    public boolean isSquareOccupied(Position position) {
        Square square = grid.getSquare(position);

        if (square.isEmpty()) {
            return false;
        }

        ContentType type = square.getContentType();

        // Compter les bateaux et les trous noirs
        if (type == ContentType.BOAT) {
            return true;
        }

        if (type == ContentType.TRAP) {
            Trap trap = (Trap) square.getContent();
            return trap.getName() == TrapType.BLACKHOLE;
        }

        return false;
    }

    // ROBOIT

    public void setStrategy(RobotStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean shouldSearchIsland(Player opponent, int gridSize) {
        return strategy != null && strategy.shouldSearchIsland(this, opponent, gridSize);
    }

    public Position chooseIslandSquare(Player opponent, int gridSize) {
        if (strategy == null) {
            return null;
        }
        return strategy.chooseIslandSquareToSearch(opponent, gridSize);
    }

    public Position chooseAttackTarget(Player opponent, int gridSize) {
        if (strategy == null) {
            throw new IllegalStateException("Pas de stratégie définie pour le robot");
        }
        return strategy.chooseTarget(this, opponent, gridSize);
    }

    public WeaponType chooseWeapon(Position target) {
        if (strategy == null) {
            return WeaponType.MISSILE;
        }
        return strategy.chooseWeapon(this, target);
    }

    public void notifyStrategyResult(Position target, boolean hit, boolean sunk) {
        if (strategy != null) {
            strategy.notifyResult(target, hit, sunk);
        }
    }

    public Position findEmptySquareForTrap() {
        if (grid == null) {
            return null;
        }

        Random rand = new Random();
        int gridSize = grid.getSize();

        for (int attempt = 0; attempt < 100; attempt++) {
            int x = rand.nextInt(gridSize);
            int y = rand.nextInt(gridSize);
            Position pos = new Position(x, y);

            Square square = grid.getSquare(pos);
            if (square.isEmpty() && !square.isInIsland()) {
                return pos;
            }
        }

        return null;
    }

    // GETTERS

    public String getUsername() {
        return username;
    }

    public boolean isRobot() {
        return isRobot;
    }

    public Grid getGrid() {
        return grid;
    }

    public List<Boat> getBoats() {
        return new ArrayList<>(boats);
    }

    public void reset() {
        weapons.clear();
        boats.clear();
        trapInventory.clear();
        tornado = null;
        if (grid != null) {
            grid.reset();
        }
    }









}