package model.game;

import model.enums.WeaponType;
import model.grid.Position;
import model.players.Player;

public class Game {
    private GameConfig gameConfig;
    private GamePlacement gamePlacement;
    private Player player;
    private Player robot;

    public Game(GameConfig gameConfig, GamePlacement gamePlacement) {
        this.gameConfig = gameConfig;
        this.gamePlacement = gamePlacement;
    }

    public void initialize() {
        //TODO
        player = new Player(gameConfig.getUsername(), false);
        robot = new Player("Robot", true);
    }

    public void playTurn(){
        //TODO
    }

    private void playPlayerTurn(boolean isRobotAssailant){
        //TODO
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

}