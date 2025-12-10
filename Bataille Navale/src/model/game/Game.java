package model.game;

import model.contents.fleet.*;
import model.contents.traps.Tornado;
import model.contents.traps.Trap;
import model.contents.traps.TrapFactory;
import model.enums.BoatName;
import model.enums.ModeGame;
import model.enums.TrapType;
import model.enums.WeaponType;
import model.grid.Grid;
import model.grid.Position;
import model.history.History;
import model.players.*;

import java.util.List;
import java.util.Map;

public class Game {
    private GameConfig gameConfig;
    private GamePlacement gamePlacement;
    private Player player;
    private Player robot;
    private History history;

    public Game(GameConfig gameConfig, GamePlacement gamePlacement) {
        this.gameConfig = gameConfig;
        this.gamePlacement = gamePlacement;
        this.history = new History();
    }

    public void initialize() {
        player = new Player(gameConfig.getUsername(), false);
        robot = new Player("Robot", true);

        Grid gridPlayer = new Grid(gameConfig.getGridSize(), gameConfig.getModeGame(), false);
        Grid gridRobot = new Grid(gameConfig.getGridSize(), gameConfig.getModeGame(), true);
        player.setGrid(gridPlayer);
        robot.setGrid(gridRobot);

        initializeBoatsPlayer();
        initializeBoatsRobot();
        initializeTrapsPlayer();
        initializeTrapsRobot();
        initializeWeaponsPlayers();
    }

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
    }

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
    }

    private void initializeWeaponsPlayers(){
        this.player.setWeaponCount(WeaponType.MISSILE, 1);
        this.robot.setWeaponCount(WeaponType.MISSILE, 1);

        if(gameConfig.getModeGame() == ModeGame.STANDARD){
            this.player.setWeaponCount(WeaponType.BOMB, 1);
            this.player.setWeaponCount(WeaponType.SONAR, 1);
            this.robot.setWeaponCount(WeaponType.BOMB, 1);
            this.robot.setWeaponCount(WeaponType.SONAR, 1);
        }
    }

    // représente UN tour de jeu
    public void playTurn(){
        playPlayerTurn(false);      // le joueur commence
        playPlayerTurn(true);       // le robot continue
    }

    // Joue le tour d'un joeu
    private void playPlayerTurn(boolean isRobotAssailant){
        
    }

    private void executeAttack(boolean isRobotAssailant, WeaponType weapon, Position position){
        //TODO
    }

    private void executeMissile(boolean isRobotAssailant, Position position){
        //TODO
    }

    private void executeBomb(boolean isRobotAssailant, Position position){
        //TODO
    }

    private void executeSonar(boolean isRobotAssailant, Position position){
        //TODO
    }

    public Player getPlayer(){
        return this.player;
    }

    public Player getRobot(){
        return this.robot;
    }

    public boolean checkGameOver(){
        return this.player.allBoatSunk() || this.robot.allBoatSunk();
    }

}