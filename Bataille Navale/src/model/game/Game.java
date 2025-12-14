package model.game;

import model.contents.Content;
import model.contents.fleet.*;
import model.contents.traps.Tornado;
import model.contents.traps.Trap;
import model.contents.traps.TrapFactory;
import model.contents.weapons.Weapon;
import model.contents.weapons.WeaponFactory;
import model.enums.*;
import model.game.Results.AttackResult;
import model.game.Results.TurnResult;
import model.grid.Grid;
import model.grid.Position;
import model.history.History;
import model.players.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Game {
    private GameConfig config;
    private GamePlacement placement;
    private Player player;
    private Player robot;
    private int turnNumber;

    private WeaponFactory weaponFactory;
    private TrapFactory trapFactory;

    public Game(GameConfig config, GamePlacement placement) {
        this.config = config;
        this.placement = placement;
        this.turnNumber = 1;
        this.weaponFactory = new WeaponFactory();
        this.trapFactory = new TrapFactory();
    }

    public void initialize() {
        player = new Player(config.getUsername(), false);
        robot = new Player("Robot", true);

        //Grid gridPlayer = new Grid(config.getGridSize(), config.getModeGame(), false);
        //Grid gridRobot = new Grid(config.getGridSize(), config.getModeGame(), true);
        player.createGrid(config.getGridSize(), config.getModeGame());
        robot.createGrid(config.getGridSize(), config.getModeGame());
        //player.setGrid(gridPlayer);
        //robot.setGrid(gridRobot);

        /*initializeBoatsPlayer();
        initializeBoatsRobot();
        initializeTrapsPlayer();
        initializeTrapsRobot();
        initializeWeaponsPlayers();
        initializeWeaponsIslandPlayer();
        initializeWeaponsIslandRobot();*/

        initializeBoats(player, placement.getBoatPlacementsPlayer());
        initializeBoats(robot, placement.getBoatPlacementsRobot());

        initializeTraps(player, placement.getTrapPlacementsPlayer());
        initializeTraps(robot, placement.getTrapPlacementsRobot());

        initializeWeapons();

        if (config.getModeGame() == ModeGame.ISLAND) {
            initializeIslandWeapons(player, placement.getWeaponPlacementPlayer());
            initializeIslandWeapons(robot, placement.getWeaponPlacementRobot());
        }
    }

    // INITIALISATIONS

    /*
    private void initializeBoatsPlayer() {
        BoatFactory boatFactory = new BoatFactory();

        Map<BoatName, List<Position>> boatPlacementsPlayer = gamePlacement.getBoatPlacementsPlayer();
        for(Map.Entry<BoatName, List<Position>> entry : boatPlacementsPlayer.entrySet()) {
            for(Position position : entry.getValue()) {
                Boat boat;

                switch(entry.getKey()) {
                    case CRUISER:
                        boat = boatFactory.createCruiser();
                        break;
                    case DESTROYER:
                        boat = boatFactory.createDestroyer();
                        break;
                    case SUBMARINE:
                        boat = boatFactory.createSubmarine();
                        break;
                    case TORPEDO_BOAT:
                        boat = boatFactory.createTorpedoBoat();
                        break;
                    default:
                        boat = boatFactory.createAircraftCarrier();
                }

                boat.belongsTo(false);

                this.player.getGrid().placeBoat(boat, position.getX(), position.getY(), position.getOrientation());
                this.player.addBoat(boat);
            }
        }
    }

    private void initializeBoatsRobot() {
        BoatFactory boatFactory = new BoatFactory();

        Map<BoatName, List<Position>> boatPlacementsRobot = gamePlacement.getBoatPlacementsRobot();
        for(Map.Entry<BoatName, List<Position>> entry : boatPlacementsRobot.entrySet()) {
            for(Position position : entry.getValue()) {
                Boat boat;

                switch(entry.getKey()) {
                    case CRUISER:
                        boat = boatFactory.createCruiser();
                        break;
                    case DESTROYER:
                        boat = boatFactory.createDestroyer();
                        break;
                    case SUBMARINE:
                        boat = boatFactory.createSubmarine();
                        break;
                    case TORPEDO_BOAT:
                        boat = boatFactory.createTorpedoBoat();
                        break;
                    default:
                        boat = boatFactory.createAircraftCarrier();
                }
                boat.belongsTo(true);

                this.robot.getGrid().placeBoat(boat, position.getX(), position.getY(), position.getOrientation());
                this.robot.addBoat(boat);
            }
        }
    }*/


    // une méthode pour joueur ET robot
    private void initializeBoats(Player owner, Map<BoatName, List<Position>> placements) {
        BoatFactory factory = new BoatFactory();

        for (Map.Entry<BoatName, List<Position>> entry : placements.entrySet()) {
            for (Position pos : entry.getValue()) {
                Boat boat = createBoat(factory, entry.getKey());
                boat.belongsTo(owner.isRobot());
                owner.placeBoat(boat,pos);
            }
        }
    }

    private Boat createBoat(BoatFactory factory, BoatName type) {
        switch (type) {
            case AIRCRAFT_CARRIER: return factory.createAircraftCarrier();
            case CRUISER: return factory.createCruiser();
            case DESTROYER: return factory.createDestroyer();
            case SUBMARINE: return factory.createSubmarine();
            case TORPEDO_BOAT: return factory.createTorpedoBoat();
            default: throw new IllegalArgumentException("Type de bateau inconnu: " + type);
        }

    }

    /*
    private void initializeTrapsPlayer() {
        TrapFactory trapFactory = new TrapFactory();

        Map<TrapType, List<Position>> trapPlacementsPlayer = gamePlacement.getTrapPlacementsPlayer();
        for(Map.Entry<TrapType, List<Position>> entry : trapPlacementsPlayer.entrySet()) {
            for(Position pos : entry.getValue()){
                Trap trap;
                switch(entry.getKey()) {
                    case BLACKHOLE:
                        trap = trapFactory.createBlackHole();
                        break;
                    default:
                        trap = trapFactory.createTornado();
                        this.player.setTornado((Tornado) trap);
                }

                this.player.getGrid().placeTrap(trap, pos.getX(), pos.getY());
            }
        }
    }

    private void initializeTrapsRobot() {
        TrapFactory trapFactory = new TrapFactory();

        Map<TrapType, List<Position>> trapPlacementsRobot = gamePlacement.getTrapPlacementsRobot();
        for(Map.Entry<TrapType, List<Position>> entry : trapPlacementsRobot.entrySet()) {
            for(Position pos : entry.getValue()) {
                Trap trap;
                switch (entry.getKey()) {
                    case BLACKHOLE:
                        trap = trapFactory.createBlackHole();
                        break;
                    default:
                        trap = trapFactory.createTornado();
                        this.robot.setTornado((Tornado) trap);
                }

                this.robot.getGrid().placeTrap(trap, pos.getX(), pos.getY());
            }
        }
    }*/

    private void initializeTraps(Player owner, Map<TrapType, List<Position>> placements) {
        for (Map.Entry<TrapType, List<Position>> entry : placements.entrySet()) {
            for (Position pos: entry.getValue()) {
                Trap trap = createTrap(entry.getKey());
                owner.placeTrap(trap, pos);
            }
        }
    }

    private Trap createTrap(TrapType type) {
        switch (type) {
            case TORNADO: return trapFactory.createTornado();
            case BLACKHOLE: return trapFactory.createBlackHole();
            default: throw new IllegalArgumentException("Type de piège inconnu: " + type);
        }
    }

    private void initializeWeapons(){
        player.setWeaponCount(WeaponType.MISSILE, 1);
        robot.setWeaponCount(WeaponType.MISSILE, 1);

        if(config.getModeGame() == ModeGame.STANDARD){
            player.setWeaponCount(WeaponType.BOMB, 1);
            player.setWeaponCount(WeaponType.SONAR, 1);
            robot.setWeaponCount(WeaponType.BOMB, 1);
            robot.setWeaponCount(WeaponType.SONAR, 1);
        }
    }

    private void initializeIslandWeapons(Player owner, Map<WeaponType, List<Position>> placements) {
        for(Map.Entry<WeaponType, List<Position>> entry : placements.entrySet()) {
            for(Position pos : entry.getValue()){
                Weapon weapon = createWeapon(entry.getKey());
                owner.placeWeaponOnIsland(weapon, pos);
            }
        }

    }

    private Weapon createWeapon(WeaponType type) {
        switch (type) {
            case MISSILE: return weaponFactory.createMissile();
            case BOMB: return weaponFactory.createBomb();
            case SONAR: return weaponFactory.createSonar();
            default: throw new IllegalArgumentException("Type d'arme inconnu: " + type);
        }

    }

    /*
    private void initializeWeaponsIslandRobot(){
        WeaponFactory factory = new WeaponFactory();

        Map<WeaponType, List<Position>> weaponPlacementRobot = gamePlacement.getWeaponPlacementRobot();
        for(Map.Entry<WeaponType, List<Position>> entry : weaponPlacementRobot.entrySet()) {
            for(Position pos : entry.getValue()){
                Weapon weapon;
                switch(entry.getKey()) {
                    case BOMB:
                        weapon = factory.createBomb();
                        break;
                    default:
                        weapon = factory.createSonar();
                }

                this.robot.getGrid().placeWeapon(weapon, pos.getX(), pos.getY());
            }
        }
    }*/



    // ACTIONS DU JOUEUR

    public TurnResult playerAttack(WeaponType weaponType, Position target) {
        // verifier que le joueur a l'arme
        if (!player.hasWeapon(weaponType)) {
            return TurnResult.error("Vous n'avez pas cette arme.");
        }

        // verifie que la case n'a pas déja été attaquée
        if (robot.wasSquareAttacked(target)) {
            return TurnResult.error("Cette case a déjà été attaquée.");
        }

        // Appliquer la tornade si active
        Position finalTarget = robot.applyTornado(target);
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
        player.useWeapon(weaponType);

        // execute l'attaque
        AttackResult attackResult;
        if (weaponType == WeaponType.SONAR) {
            attackResult = executeSonar(positions, robot);
        } else {
            attackResult = executeAttack(positions, robot, false);
        }
        return TurnResult.success(attackResult, tornadoActivated, finalTarget);
    }


    // le joueur fouille l'île
    public TurnResult playerSearchIsland(Position target) {
        // verif que c'est sur l'île
        if (!robot.isSquareOnIsland(target)) {
            return TurnResult.error("Cette case n'est pas sur l'île.");
        }

        // verif que la case n'a pas déja été fouillée
        if (robot.wasSquareSearched(target)) {
            return TurnResult.error("Cette case a déjà été fouillée.");
        }

        // fouiller
        Content found = robot.searchIsland(target);

        if (found == null) {
            return TurnResult.emptyIslandSquare();
        }

        ContentType type = found.getContentType();

        if (type == ContentType.WEAPON) {
            Weapon weapon = (Weapon) found;
            player.addWeapon(weapon.getName());
            return TurnResult.weaponFound(weapon.getName());
        }

        if (type == ContentType.TRAP) {
            Trap trap = (Trap) found;
            player.addTrapToInventory(trap.getName());
            return TurnResult.trapFound(trap.getName());
        }

        return TurnResult.error("Contenu inconnu trouvé sur l'île.");
    }

    // joueur place un piège depuis son inventaire
    public TurnResult playerPlaceTrap(TrapType trapType, Position target) {
        // verif l'inventaire
        if (player.getTrapInventoryCount(trapType) <= 0) {
            return TurnResult.error("Vous n'avez pas de " + trapType + " dans votre inventaire.");
        }

        // verif que la case est valide
        if (!player.canPlaceTrapAt(target)) {
            return  TurnResult.error("Vous ne pouvez pas placer un piège à cette position.");
        }

        // créer et placer le piège
        Trap trap = createTrap(trapType);
        boolean placed = player.placeTrapFromInventory(trapType, target, trap);

        if (!placed) {
            return TurnResult.error("Échec du placement du piège.");
        }

        return TurnResult.trapPlaced(trapType, target);
    }

    // TOUR DU ROBOT

    public TurnResult playRobotTurn() {
        // verif si le robot veut fouiller l'île
        if (hasIsland() && robot.shouldSearchIsland(player, config.getGridSize())) {
            Position islandTarget = robot.chooseIslandSquare(player,config.getGridSize());

            if (islandTarget != null) {
                return robotSearchIsland(islandTarget);
            }
        }

        // sinon le robot attaque
        return robotAttack();
    }

    private TurnResult robotSearchIsland(Position target) {
        Content found = player.searchIsland(target);

        if (found == null) {
            return TurnResult.robotEmptyIsland();
        }

        ContentType type = found.getContentType();

        if (type == ContentType.WEAPON) {
            Weapon weapon = (Weapon) found;
            robot.addWeapon(weapon.getName());
            return TurnResult.robotWeaponFound(weapon.getName());
        }

        if (type == ContentType.TRAP) {
            Trap trap = (Trap) found;

            // le robot place le piège
            Position placement = robot.findEmptySquareForTrap();
            if (placement != null) {
                Trap newTrap = createTrap(trap.getName());
                robot.placeTrap(newTrap, placement);
                return TurnResult.robotTrapFound(trap.getName(), placement);
            }
            return TurnResult.robotTrapFoundButNoSpace(trap.getName());
        }

        return TurnResult.error("Contenu inconnu");
    }

    private TurnResult robotAttack() {
        Position target = robot.chooseAttackTarget(player, config.getGridSize());

        // applique la tornade du joueur si active
        Position finalTarget = player.applyTornado(target);
        boolean tornadoActivated = !finalTarget.equals(target);

        // Choisir l'arme
        WeaponType weaponChoice = robot.chooseWeapon(finalTarget);

        // Créer l'arme
        Weapon weapon = createWeapon(weaponChoice);
        ArrayList<Position> positions = weapon.use(finalTarget);

        // Utiliser l'arme si ce n'est pas un missile
        if (weaponChoice != WeaponType.MISSILE) {
            robot.useWeapon(weaponChoice);
        }

        // exec l'attaque
        AttackResult attackResult;
        if (weaponChoice == WeaponType.SONAR) {
            attackResult = executeSonar(positions, player);
            robot.notifyStrategyResult(finalTarget, false, false);
        } else {
            attackResult = executeAttack(positions, player, true);
            robot.notifyStrategyResult(finalTarget, attackResult.hadHit(), attackResult.hadSunk());
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
            TrapActivation trapResult = target.checkAndActivateTrap(pos, config.getGridSize());
            if (trapResult != null) {
                trapActivations.add(trapResult);

                // si trou noir, l'attaque revient sur l'attaquant
                if (trapResult.isBounced()) {
                    Player attacker = isRobotAttacker ? robot : player;
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
            if (robot.isSquareOnIsland(target)) {
                return "Le missile ne peut pas être utilisé sur l'île !";
            }
        } else if (weapon == WeaponType.BOMB) {
            if (bombHitsIsland(target)) {
                return "La bombe ne peut pas toucher l'île !";
            }
        } else if (weapon == WeaponType.SONAR) {
            if (robot.isSquareOnIsland(target)) {
                return "Le sonar ne peut pas être utilisé sur l'île !";
            }
        }
        return null;
    }

    private boolean bombHitsIsland(Position center) {
        if (robot.isSquareOnIsland(center)) {
            return true;
        }

        Position[] adjacent = {
                new Position(center.getX() - 1, center.getY()),
                new Position(center.getX() + 1, center.getY()),
                new Position(center.getX(), center.getY() - 1),
                new Position(center.getX(), center.getY() + 1)
        };

        for (Position pos : adjacent) {
            if (isPositionValid(pos) && robot.isSquareOnIsland(pos)) {
                return true;
            }
        }

        return false;
    }


    private boolean isPositionValid(Position pos) {
        return pos.getX() >= 0 && pos.getX() < config.getGridSize() &&
                pos.getY() >= 0 && pos.getY() < config.getGridSize();
    }


    // GAME STATE

    public boolean checkGameOver() {
        return player.allBoatSunk() || robot.allBoatSunk();
    }

    public String getWinner() {
        if (player.allBoatSunk()) {
            return "Robot";
        } else if (robot.allBoatSunk()) {
            return player.getUsername();
        }
        return null;
    }

    public void incrementTurn() {
        turnNumber++;
    }

    // GETTERS

    public Player getPlayer() {
        return player;
    }

    public Player getRobot() {
        return robot;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public GameConfig getConfig() {
        return config;
    }

    public boolean hasIsland() {
        return config.getModeGame() == ModeGame.ISLAND;
    }


}