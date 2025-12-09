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
        return this.config.getModeGame() == ModeGame.ISLAND && inIsland;
    }

    public void setView(GameView view) {
        this.view = view;

        updateAllDisplays();
    }

    private void updateAllDisplays() {
        view.setTurnNumber(turnNumber);
        //updateStats();
        //updateWeapons();
        if (config.getModeGame() == ModeGame.ISLAND) {
            //updateIsland();
        }
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


    /*
    private void displayPlayerGrid(){
        Player player = game.getPlayer();
        Grid playerGrid = player.getGrid();

        for(int y=0; y<playerGrid.getSize(); y++){
            for(int x=0; x<playerGrid.getSize(); x++){
                Position pos = new Position(x, y);
                ContentType type = playerGrid.getContentTypeSquare(pos);

                switch(type){
                    case BOAT:
                        this.view.setPlayerCellColor(x, y, BOAT_COLOR);
                        break;
                    case TRAP:
                        this.view.setPlayerCellColor(x, y, TRAP_COLOR);
                        break;
                    default:
                        this.view.setPlayerCellColor(x, y, WATER_COLOR);
                }

                if(playerGrid.squareIsInIsland(pos)){
                    this.view.setPlayerCellColor(x, y, ISLAND_COLOR);
                }
            }
        }
    }
    */

}
