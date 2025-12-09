package controller;

import model.contents.Content;
import model.contents.fleet.Boat;
import model.enums.ContentType;
import model.enums.*;
import model.game.Game;
import model.game.GameConfig;
import model.game.GamePlacement;
import model.grid.*;
import model.grid.Position;
import model.players.Player;
import view.GameView;

import java.awt.*;

public class GameController {
    private Game game;
    private GameView view;
    private GameConfig config;

    private int turnNumber = 1;

    public GameController(GameConfig config, GamePlacement placement) {
        this.config = config;

        this.game = new Game(config, placement);
        game.initialize();
    }

    public boolean isInIsland(int x, int y){
        Player player = this.game.getPlayer();
        Grid grid = player.getGrid();
        boolean inIsland = grid.squareIsInIsland(new Position(x, y));
        return hasIsland() && inIsland;
    }

    public void setView(GameView view) {
        this.view = view;

        updateAllDisplays();
    }

    private void updateAllDisplays() {
        view.setTurnNumber(turnNumber);
        //updateWeapons();
    }
    
    public void handleGridClick(int x, int y) {
        Position target = new Position(x,y);

        if (view.isShovelSelected()) {
            handleIslandSearch(target);
        }
        else {
            WeaponType weapon = view.getSelectedWeapon();
            handleWeaponUse(weapon, target);
        }
    }

    private void handleWeaponUse(WeaponType weapon, Position target) {
        System.out.println("Target: " + target.getX() + " " + target.getY());
    }

    private void handleIslandSearch(Position target) {

    }

    public void restart() {
        // TODO
    }

    public void quit() {
        System.exit(0);
    }

    public boolean hasIsland(){
        return this.config.getModeGame() == ModeGame.ISLAND;
    }

    public int getNumberBoats(){
        return this.config.getNumberBoatsTotal();
    }

}
