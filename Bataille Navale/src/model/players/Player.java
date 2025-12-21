package model.players;

import model.contents.Content;
import model.contents.fleet.Boat;
import model.contents.traps.Tornado;
import model.contents.traps.Trap;
import model.enums.*;
import model.game.robotStrategy.RobotStrategy;
import model.game.TrapActivation;
import model.grid.Grid;
import model.grid.Position;
import model.grid.Square;

import java.util.*;

public class Player {
    private String _username;
    private boolean _isRobot;
    private Map<WeaponType, Integer> _weapons;
    private Grid _grid;
    private Tornado _tornado;
    private List<Boat> _boats;
    private RobotStrategy _strategy;
    private Map<TrapType, Integer> _trapInventory;

    public Player(String username, boolean robot) {
        this._username = username;
        this._isRobot = robot;
        this._weapons = new HashMap<>();
        this._boats = new ArrayList<>();
        this._tornado = null;
        this._strategy = null;
        this._trapInventory = new HashMap<>();
    }

    // INIT

    public void setGrid(Grid grid) { this._grid = grid; }

    public void placeTrap(Trap trap, Position position) {
        _grid.placeTrap(trap, position.getX(), position.getY());

        if (trap.getName() == TrapType.TORNADO) {
            this._tornado = (Tornado) trap;
        }
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
            _weapons.put(type, count - 1);
        }
    }

    public void addWeapon(WeaponType type) {
        _weapons.put(type, getWeaponCount(type) + 1);
    }

    public int getWeaponCount(WeaponType type) {
        return _weapons.getOrDefault(type, 0);
    }

    public void setWeaponCount(WeaponType type, int count) {
        _weapons.put(type, count);
    }

    public Map<WeaponType, Integer> getWeapons() {
        return new HashMap<>(_weapons);
    }

    public boolean canUseSonar(){
        if(getWeaponCount(WeaponType.SONAR) == 0){
            return false;
        }

        return hasSubmarine();
    }

    private boolean hasSubmarine(){
        for(Boat b : _boats){
            if(b.getName() == BoatName.SUBMARINE && !b.hasSunk()){
                return true;
            }
        }
        return false;
    }

    // PIEGES

    public void addTrapToInventory(TrapType type) {
        _trapInventory.put(type, getTrapInventoryCount(type) + 1);
    }

    public int getTrapInventoryCount(TrapType type) {
        return _trapInventory.getOrDefault(type, 0);
    }

    public void removeTrapFromInventory(TrapType type) {
        int count = getTrapInventoryCount(type);
        if (count > 0) {
            _trapInventory.put(type, count - 1);
        }
    }

    public Map<TrapType, Integer> getTrapInventory() {
        return new HashMap<>(_trapInventory);
    }

    public boolean canPlaceTrapAt(Position position) {
        if (_grid == null) {
            return false;
        }

        Square square = _grid.getSquare(position);

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
        _grid.placeTrap(trap, position.getX(), position.getY());
        removeTrapFromInventory(type);

        // Si c'est une tornade, la définir comme active
        if (type == TrapType.TORNADO) {
            this._tornado = (Tornado) trap;
        }

        return true;
    }

    // tornade

    public boolean hasTornadoActive() {
        return _tornado != null && _tornado.isActive();
    }

    public Position applyTornado(Position target) {
        if (hasTornadoActive()) {
            return _tornado.getNewPosition(target);
        }
        return target;
    }

    public void setTornado(Tornado tornado) {
        this._tornado = tornado;
    }

    // ATTAQUES ET DEFENSES

    public void receiveAttack(Position position) {
        if (_grid != null) {
            _grid.attack(position);
        }
    }

    public boolean receiveAttackAndCheckHit(Position position) {
        Square square = _grid.getSquare(position);
        boolean wasHit = !square.isEmpty() && square.getContentType() == ContentType.BOAT;
        receiveAttack(position);
        return wasHit;
    }

    public boolean wasBoatSunkAt(Position position) {
        Square square = _grid.getSquare(position);
        if (square.getContentType() == ContentType.BOAT) {
            Boat boat = (Boat) square.getContent();
            return boat.hasSunk();
        }
        return false;
    }

    // verif si un piège est présent et l'active si il faut
    public TrapActivation checkAndActivateTrap(Position position, int gridSize) {
        Square square = _grid.getSquare(position);

        if (!square.isEmpty() && square.getContentType() == ContentType.TRAP) {
            Trap trap = (Trap) square.getContent();

            if (trap.getName() == TrapType.TORNADO) {
                Tornado tornado = (Tornado) trap;
                if (!tornado.isActive()) {
                    tornado.activate(gridSize, _grid.hasIsland(), _grid.getPositionIsland());
                    setTornado(tornado);
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
        for (Boat boat : _boats) {
            if (!boat.hasSunk()) {
                return false;
            }
        }
        return true;
    }

    // ILE

    public boolean isSquareOnIsland(Position position) {
        return _grid.squareIsInIsland(position);
    }

    public boolean wasSquareSearched(Position position) {
        Square square = _grid.getSquare(position);
        return !square.isNotSearched();
    }

    public Content searchIsland(Position position) {
        Square square = _grid.getSquare(position);
        return square.search();
    }

    public boolean wasSquareAttacked(Position position) {
        Square square = _grid.getSquare(position);
        return square.wasAttacked();
    }

    public boolean isSquareOccupied(Position position) {
        Square square = _grid.getSquare(position);

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

    // ROBOT

    public void setStrategy(RobotStrategy strategy) {
        this._strategy = strategy;
    }

    public boolean shouldSearchIsland(Player opponent, int gridSize) {
        return _strategy != null && _strategy.shouldSearchIsland(this, opponent, gridSize);
    }

    public Position chooseIslandSquare(Player opponent, int gridSize) {
        if (_strategy == null) {
            return null;
        }
        return _strategy.chooseIslandSquareToSearch(opponent, gridSize);
    }

    public Position chooseAttackTarget(Player opponent, int gridSize) {
        if (_strategy == null) {
            throw new IllegalStateException("Pas de stratégie définie pour le robot");
        }
        return _strategy.chooseTarget(this, opponent, gridSize);
    }

    public WeaponType chooseWeapon() {
        if (_strategy == null) {
            return WeaponType.MISSILE;
        }
        return _strategy.chooseWeapon(this);
    }

    public void notifyStrategyResult(Position target, boolean hit, boolean sunk) {
        if (_strategy != null) {
            _strategy.notifyResult(target, hit, sunk);
        }
    }

    public Position findEmptySquareForTrap() {
        if (_grid == null) {
            return null;
        }

        Random rand = new Random();
        int gridSize = _grid.getSize();

        for (int attempt = 0; attempt < 100; attempt++) {
            int x = rand.nextInt(gridSize);
            int y = rand.nextInt(gridSize);
            Position pos = new Position(x, y);

            Square square = _grid.getSquare(pos);
            if (square.isEmpty() && !square.isInIsland()) {
                return pos;
            }
        }

        return null;
    }

    // GETTERS

    public String getUsername() {
        return _username;
    }

    public boolean isRobot() {
        return _isRobot;
    }

    public Grid getGrid() {
        return _grid;
    }

    public List<Boat> getBoats() {
        return new ArrayList<>(_boats);
    }

    // SETTERS
    public void addBoat(Boat boat) {
        _boats.add(boat);
    }

}