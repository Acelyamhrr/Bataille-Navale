package model.game;

import model.contents.Content;
import model.contents.fleet.*;
import model.contents.traps.Trap;
import model.contents.traps.TrapFactory;
import model.contents.weapons.Weapon;
import model.contents.weapons.WeaponFactory;
import model.enums.*;
import model.game.Results.AttackResult;
import model.game.Results.TurnResult;
import model.grid.Grid;
import model.grid.Position;
import model.players.*;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private GameConfig _config;
    private GamePlacement _placement;
    private Player _player;
    private Player _robot;
    private int _turnNumber;

    private WeaponFactory _weaponFactory;
    private TrapFactory _trapFactory;

    public Game(GameConfig config, GamePlacement placement) {
        this._config = config;
        this._placement = placement;
        this._turnNumber = 1;
        this._weaponFactory = new WeaponFactory();
        this._trapFactory = new TrapFactory();
    }

    public void initialize() {
        _player = new Player(_config.getUsername(), false);
        _robot = new Player("Robot", true);

        _player.setGrid(_placement.getPlayerGrid());
        setBoatPlayer(_player);
        _robot.setGrid(_placement.getRobotGrid());
        setBoatPlayer(_robot);

        initializeWeapons();
    }

    // INITIALISATIONS

    private void setBoatPlayer(Player player){
        Grid grid = player.getGrid();
        for(Boat b : grid.getBoats()){
            player.addBoat(b);
        }
    }

    private Trap createTrap(TrapType type) {
        switch (type) {
            case TORNADO: return _trapFactory.createTornado();
            case BLACKHOLE: return _trapFactory.createBlackHole();
            default: throw new IllegalArgumentException("Type de piège inconnu: " + type);
        }
    }

    private void initializeWeapons(){
        _player.setWeaponCount(WeaponType.MISSILE, 1);
        _robot.setWeaponCount(WeaponType.MISSILE, 1);

        if(_config.getModeGame() == ModeGame.STANDARD){
            _player.setWeaponCount(WeaponType.BOMB, 1);
            _player.setWeaponCount(WeaponType.SONAR, 1);
            _robot.setWeaponCount(WeaponType.BOMB, 1);
            _robot.setWeaponCount(WeaponType.SONAR, 1);
        }
    }

    private Weapon createWeapon(WeaponType type) {
        switch (type) {
            case MISSILE: return _weaponFactory.createMissile();
            case BOMB: return _weaponFactory.createBomb();
            case SONAR: return _weaponFactory.createSonar();
            default: throw new IllegalArgumentException("Type d'arme inconnu: " + type);
        }

    }

    // ACTIONS DU JOUEUR

    public TurnResult playerAttack(WeaponType weaponType, Position target) {
        // verifier que le joueur a l'arme
        if (!_player.hasWeapon(weaponType)) {
            return TurnResult.error("Vous n'avez pas cette arme.");
        }

        //Si sonar, vérifie s'il peut l'utiliser
        if(weaponType == WeaponType.SONAR && !_player.canUseSonar()){
            return TurnResult.error("Vous ne pouvez pas utilisé le sonar (pas de sous-marin en état).");
        }

        // verifie que la case n'a pas déja été attaquée
        if (_robot.wasSquareAttacked(target)) {
            return TurnResult.error("Cette case a déjà été attaquée.");
        }

        // Appliquer la tornade si active
        Position finalTarget = _robot.applyTornado(target);
        boolean tornadoActivated = !finalTarget.equals(target);

        // verif l'île
        if (hasIsland()) {
            String islandError = checkIslandRestrictions(weaponType, finalTarget);
            if (islandError != null) {
                return TurnResult.error(islandError);
            }
        }

        Weapon weapon = createWeapon(weaponType);
        ArrayList<Position> positions = weapon.use(finalTarget);

        // utilise l'arme
        _player.useWeapon(weaponType);

        // execute l'attaque
        AttackResult attackResult;
        if (weaponType == WeaponType.SONAR) {
            attackResult = executeSonar(positions, _robot);
        } else {
            attackResult = executeAttack(positions, _robot, false);
        }
        return TurnResult.success(attackResult, tornadoActivated, finalTarget);
    }


    // le joueur fouille l'île
    public TurnResult playerSearchIsland(Position target) {
        // verif que c'est sur l'île
        if (!_robot.isSquareOnIsland(target)) {
            return TurnResult.error("Cette case n'est pas sur l'île.");
        }

        // verif que la case n'a pas déja été fouillée
        if (_robot.wasSquareSearched(target)) {
            return TurnResult.error("Cette case a déjà été fouillée.");
        }

        // verifier la tornade
        Position finalTarget = _robot.applyTornado(target);
        boolean tornadoActivated = !finalTarget.equals(target);

        // fouiller
        Content found = _robot.searchIsland(finalTarget);

        if (found == null) {
            return TurnResult.emptyIslandSquare(tornadoActivated, finalTarget);
        }

        ContentType type = found.getContentType();

        if (type == ContentType.WEAPON) {
            Weapon weapon = (Weapon) found;
            _player.addWeapon(weapon.getName());
            return TurnResult.weaponFound(weapon.getName(), tornadoActivated, finalTarget);
        }

        if (type == ContentType.TRAP) {
            Trap trap = (Trap) found;
            _player.addTrapToInventory(trap.getName());
            return TurnResult.trapFound(trap.getName(), tornadoActivated, finalTarget);
        }

        return TurnResult.error("Contenu inconnu trouvé sur l'île.");
    }

    // joueur place un piège depuis son inventaire
    public TurnResult playerPlaceTrap(TrapType trapType, Position target) {
        // verif l'inventaire
        if (_player.getTrapInventoryCount(trapType) <= 0) {
            return TurnResult.error("Vous n'avez pas de " + trapType + " dans votre inventaire.");
        }

        // verif que la case est valide
        if (!_player.canPlaceTrapAt(target)) {
            return  TurnResult.error("Vous ne pouvez pas placer un piège à cette position.");
        }

        // créer et placer le piège
        Trap trap = createTrap(trapType);
        boolean placed = _player.placeTrapFromInventory(trapType, target, trap);

        if (!placed) {
            return TurnResult.error("Échec du placement du piège.");
        }

        return TurnResult.trapPlaced(trapType, target);
    }

    // TOUR DU ROBOT

    public TurnResult playRobotTurn() {
        // verif si le robot veut fouiller l'île
        if (hasIsland() && _robot.shouldSearchIsland(_player, _config.getGridSize())) {
            Position islandTarget = _robot.chooseIslandSquare(_player, _config.getGridSize());

            if (islandTarget != null) {
                return robotSearchIsland(islandTarget);
            }
        }

        // sinon le robot attaque
        return robotAttack();
    }

    private TurnResult robotSearchIsland(Position target) {
        // verifier la tornade
        Position finalTarget = _player.applyTornado(target);
        boolean tornadoActivated = !finalTarget.equals(target);

        Content found = _player.searchIsland(finalTarget);

        if (found == null) {
            return TurnResult.robotEmptyIsland(tornadoActivated, finalTarget);
        }

        ContentType type = found.getContentType();

        if (type == ContentType.WEAPON) {
            Weapon weapon = (Weapon) found;
            _robot.addWeapon(weapon.getName());
            return TurnResult.robotWeaponFound(weapon.getName(), tornadoActivated, finalTarget);
        }

        if (type == ContentType.TRAP) {
            Trap trap = (Trap) found;

            // le robot place le piège
            Position placement = _robot.findEmptySquareForTrap();
            if (placement != null) {
                Trap newTrap = createTrap(trap.getName());
                _robot.placeTrap(newTrap, placement);
                return TurnResult.robotTrapFound(trap.getName(), placement, tornadoActivated, finalTarget);
            }
            return TurnResult.robotTrapFoundButNoSpace(trap.getName());
        }

        return TurnResult.error("Contenu inconnu");
    }

    private TurnResult robotAttack() {
        Position target = _robot.chooseAttackTarget(_player, _config.getGridSize());

        // applique la tornade du joueur si active
        Position finalTarget = _player.applyTornado(target);
        boolean tornadoActivated = !finalTarget.equals(target);

        // Choisir l'arme
        WeaponType weaponChoice = _robot.chooseWeapon(finalTarget);

        // Créer l'arme
        Weapon weapon = createWeapon(weaponChoice);
        ArrayList<Position> positions = weapon.use(finalTarget);

        // Utiliser l'arme si ce n'est pas un missile
        if (weaponChoice != WeaponType.MISSILE) {
            _robot.useWeapon(weaponChoice);
        }

        // exec l'attaque
        AttackResult attackResult;
        if (weaponChoice == WeaponType.SONAR) {
            attackResult = executeSonar(positions, _player);
            _robot.notifyStrategyResult(finalTarget, false, false);
        } else {
            attackResult = executeAttack(positions, _player, true);
            _robot.notifyStrategyResult(finalTarget, attackResult.hadHit(), attackResult.hadSunk());
        }

        return TurnResult.robotAttack(weaponChoice, attackResult, tornadoActivated, finalTarget);
    }

    // EXECUTION DES ATTAQUES

    // execute une attaque (missile ou bombe car même logique)
    private AttackResult executeAttack(ArrayList<Position> positions, Player target, boolean isRobotAttacker) {
        int hits = 0;
        int misses = 0;
        boolean sunkBoat = false;
        List<TrapActivation> trapActivations = new ArrayList<>();

        for (Position pos : positions) {
            // verif les limites
            if (!isPositionValid(pos)) {
                continue;
            }

            // traiter les pièges
            TrapActivation trapResult = target.checkAndActivateTrap(pos, _config.getGridSize());
            if (trapResult != null) {
                trapActivations.add(trapResult);

                // si trou noir, l'attaque revient sur l'attaquant
                if (trapResult.isBounced()) {
                    Player attacker = isRobotAttacker ? _robot : _player;
                    attacker.receiveAttack(pos);
                    continue;
                }
            }

            // attaque normal
            boolean wasHit = target.receiveAttackAndCheckHit(pos);

            if (wasHit) {
                hits++;
                if (target.wasBoatSunkAt(pos)) {
                    sunkBoat = true;
                }
            }
            else {
                misses++;
            }
        }

        return new AttackResult(hits, misses, sunkBoat, trapActivations);
    }

    // execute une attaque sonar
    private AttackResult executeSonar(ArrayList<Position> positions, Player target) {
        int occupiedCells = 0;

        for (Position pos : positions) {
            if (!isPositionValid(pos)) {
                continue;
            }

            if (target.isSquareOccupied(pos)) {
                occupiedCells++;
            }
        }

        return AttackResult.sonarResult(occupiedCells);
    }

    // VERIFICATIONS


    private String checkIslandRestrictions(WeaponType weapon, Position target) {
        if (weapon == WeaponType.MISSILE) {
            if (_robot.isSquareOnIsland(target)) {
                return "Le missile ne peut pas être utilisé sur l'île !";
            }
        } else if (weapon == WeaponType.BOMB) {
            if (bombHitsIsland(target)) {
                return "La bombe ne peut pas toucher l'île !";
            }
        } else if (weapon == WeaponType.SONAR) {
            if (_robot.isSquareOnIsland(target)) {
                return "Le sonar ne peut pas être utilisé sur l'île !";
            }
        }
        return null;
    }

    private boolean bombHitsIsland(Position center) {
        if (_robot.isSquareOnIsland(center)) {
            return true;
        }

        Position[] adjacent = {
                new Position(center.getX() - 1, center.getY()),
                new Position(center.getX() + 1, center.getY()),
                new Position(center.getX(), center.getY() - 1),
                new Position(center.getX(), center.getY() + 1)
        };

        for (Position pos : adjacent) {
            if (isPositionValid(pos) && _robot.isSquareOnIsland(pos)) {
                return true;
            }
        }

        return false;
    }


    private boolean isPositionValid(Position pos) {
        return pos.getX() >= 0 && pos.getX() < _config.getGridSize() &&
                pos.getY() >= 0 && pos.getY() < _config.getGridSize();
    }


    // GAME STATE

    public boolean checkGameOver() {
        return _player.allBoatSunk() || _robot.allBoatSunk();
    }

    public String getWinner() {
        if (_player.allBoatSunk()) {
            return "Robot";
        } else if (_robot.allBoatSunk()) {
            return _player.getUsername();
        }
        return null;
    }

    public void incrementTurn() {
        _turnNumber++;
    }

    // GETTERS

    public Player getPlayer() {
        return _player;
    }

    public Player getRobot() {
        return _robot;
    }

    public int getTurnNumber() {
        return _turnNumber;
    }

    public GameConfig getConfig() {
        return _config;
    }

    public boolean hasIsland() {
        return _config.getModeGame() == ModeGame.ISLAND;
    }


}