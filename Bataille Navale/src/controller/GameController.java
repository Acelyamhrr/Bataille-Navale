package controller;

import model.contents.Content;
import model.contents.fleet.Boat;
import model.contents.traps.Trap;
import model.contents.weapons.Weapon;
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


    public GameController(GameView view, GameConfig config, GamePlacement placement) {
        this.view = view;
        this.config = config;

        this.game = new Game(config, placement);
        game.initialize();

        connectView();
        updateAllDisplays();
    }

    private void connectView() {
        view.setGridClickHandler((x, y) -> handleGridClick(x, y));
        view.addRestartListener(e -> restart());
        view.addQuitListener(e -> quit());
    }

    private void updateAllDisplays() {
        view.setTurnNumber(turnNumber);
        updatePlayerGrid();
        updateRobotGrid();
        updateStats();
        updateWeapons();
        if (config.getModeGame() == ModeGame.ISLAND) {
            updateIsland();
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

                if (!square.wasAttacked()) {
                    if (!square.isEmpty()) {
                        Content content = square.getContent();
                        ContentType contentType = content.getContentType();

                        if (contentType == ContentType.BOAT) {
                            Boat boat = (Boat) content;
                            if (boat.hasSunk()) {
                                color = SUNK_COLOR;
                            } else {
                                color = HIT_COLOR;
                            }
                        }
                    } else {
                        color = MISS_COLOR;
                    }
                }
                else if (config.getModeGame() == ModeGame.ISLAND && square.isInIsland()) {
                    color = ISLAND_COLOR;

                }

                if (config.getModeGame() == ModeGame.ISLAND && square.isInIsland() && square.wasAttacked()) {
                    if (square.isEmpty()) {
                        color= ISLAND_SEARCHED_EMPTY;
                    }
                    else {
                        color = ISLAND_SEARCHED_FOUND;
                    }
                }

                view.setRobotCellColor(x, y, color);
            }
        }
    }
    
    private void handleGridClick(int x, int y) {
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

    }

    private void handleIslandSearch(Position target) {

    }

    private void restart() {
        // to do
    }

    private void quit() {
        System.exit(0);
    }



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

    private void displayRobotGrid(){
        Player robot = game.getRobot();
        Grid robotGrid = robot.getGrid();

        for(int y=0; y<robotGrid.getSize(); y++){
            for(int x=0; x<robotGrid.getSize(); x++){
                Position pos = new Position(x, y);

                if(robotGrid.squareIsInIsland(pos)){
                    this.view.setRobotCellColor(x, y, ISLAND_COLOR);
                }
                else{
                    this.view.setRobotCellColor(x, y, WATER_COLOR);
                }
            }
        }
    }

}
