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

    private static final Color WATER_COLOR = new Color(100, 150, 200);
    private static final Color BOAT_COLOR = new Color(80, 80, 80);
    private static final Color HIT_COLOR = new Color(255, 100, 100);
    private static final Color SUNK_COLOR = new Color(150, 50, 50);
    private static final Color MISS_COLOR = new Color(200, 200, 200);
    private static final Color ISLAND_COLOR = new Color(210, 180, 140);
    private static final Color ISLAND_SEARCHED_EMPTY = new Color(190, 160, 120);
    private static final Color ISLAND_SEARCHED_FOUND = new Color(255, 215, 0);
    private static final Color TRAP_COLOR = new Color(243, 88, 48);


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
        updatePlayerGrid();
        updateRobotGrid();
        //updateStats();
        //updateWeapons();
        if (config.getModeGame() == ModeGame.ISLAND) {
            //updateIsland();
        }
    }

    private void updatePlayerGrid() {
        int size = config.getGridSize();
        Player player = game.getPlayer();
        Grid grid = player.getGrid();

        // Pour chaque case
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Square square = grid.getSquare(new Position(x, y));
                Color color = WATER_COLOR;

                // Vérifier si la case contient quelque chose
                if (!square.isEmpty()) {
                    Content content = square.getContent();
                    ContentType contentType = content.getContentType();

                    // Si c'est un bateau
                    if (contentType == ContentType.BOAT) {
                        color = BOAT_COLOR;

                        // Si la case a été attaquée
                        if (square.wasAttacked()) {
                            Boat boat = (Boat) content;
                            if (boat.hasSunk()) {
                                color = SUNK_COLOR; // Bateau coulé
                            } else {
                                color = HIT_COLOR; // Bateau touché
                            }
                        }
                    }
                    // Si c'est un piège
                    else if (contentType == ContentType.TRAP) {
                        color = TRAP_COLOR;
                    }
                }
                // Case vide attaquée (manqué)
                else if (square.wasAttacked()) {
                    color = MISS_COLOR;
                }

                // Si mode île et case sur l'île
                if (config.getModeGame() == ModeGame.ISLAND && square.isInIsland()) {
                    if (!square.wasAttacked()) {
                        color = ISLAND_COLOR; // Île non fouillée
                    } else if (square.isEmpty()) {
                        color = ISLAND_SEARCHED_EMPTY; // Île fouillée vide
                    } else {
                        color = ISLAND_SEARCHED_FOUND; // Île fouillée avec objet
                    }
                }

                view.setPlayerCellColor(x, y, color);
            }
        }
    }

    private void updateRobotGrid() {
        int size = config.getGridSize();
        Player robot = game.getRobot();
        Grid grid = robot.getGrid();

        for(int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Square square = grid.getSquare(new Position(x, y));
                Color color = WATER_COLOR;

                // Vérifier si la case contient quelque chose
                if (!square.isEmpty()) {
                    Content content = square.getContent();
                    ContentType contentType = content.getContentType();

                    // Si c'est un bateau
                    if (contentType == ContentType.BOAT) {
                        // Si la case a été attaquée
                        if (square.wasAttacked()) {
                            Boat boat = (Boat) content;
                            if (boat.hasSunk()) {
                                color = SUNK_COLOR; // Bateau coulé
                            } else {
                                color = HIT_COLOR; // Bateau touché
                            }
                        }
                    }
                }
                // Case vide attaquée (manqué)
                else if (square.wasAttacked()) {
                    color = MISS_COLOR;
                }

                // Si mode île et case sur l'île
                if (config.getModeGame() == ModeGame.ISLAND && square.isInIsland()) {
                    if (!square.wasAttacked()) {
                        color = ISLAND_COLOR; // Île non fouillée
                    } else if (square.isEmpty()) {
                        color = ISLAND_SEARCHED_EMPTY; // Île fouillée vide
                    } else {
                        color = ISLAND_SEARCHED_FOUND; // Île fouillée avec objet
                    }
                }

                view.setRobotCellColor(x, y, color);
            }
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
