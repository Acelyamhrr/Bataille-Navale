package controller;

import model.contents.fleet.Boat;
import model.enums.*;
import model.game.*;
import model.game.Results.TurnResult;
import model.grid.*;
import model.grid.Position;
import model.players.Player;
import model.players.*;
import view.EndView;
import view.GameView;

import javax.swing.*;
import java.awt.*;


/**
 * Controller pour la partie de jeu.
 * intermédiaire entre la vue et le modèle.
 * Ne contient AUCUNE logique métier.
 * demande au modèle de faire les calculs
 * et transmet les res à la vue
 */
public class GameController {
    private Game game;
    private GameView view;
    private GameConfig config;

    private boolean playerTurn = true;
    private boolean placingTrapMode = false;
    private TrapType trapToPlace = null;

    private CentralController centralController;

    public GameController(GameConfig config, GamePlacement placement, CentralController centralController) {
        this.config = config;
        this.game = new Game(config, placement);

        this.centralController = centralController;

        game.initialize();
        setupRobotStrategy();
    }

    private void setupRobotStrategy() {
        Player robot = game.getRobot();
        if (config.getRobotMode() == RobotMode.SMART) {
            robot.setStrategy(new model.game.RobotStrategy.SmartRobotStrategy());
        } else {
            robot.setStrategy(new model.game.RobotStrategy.RandomRobotStrategy());
        }
    }

    public void setView(GameView view) {
        this.view = view;
        registerObservers();        // enregistre la vue comme observer sur tous les boats et squares
        updateAllDisplays();

        updatePlayerStatsDisplay();
        updateRobotStatsDisplay();
    }

    // OBSERVATEURS

    private void registerObservers() {
        for (Boat boat : game.getPlayer().getBoats()) {
            boat.addObserver(view);
        }

        for (Boat boat : game.getRobot().getBoats()) {
            boat.addObserver(view);
        }

        Player player = game.getPlayer();
        Player robot = game.getRobot();

        for (int x = 0; x < config.getGridSize(); x++) {
            for (int y = 0; y < config.getGridSize(); y++) {
                Position pos = new Position(x, y);
                player.getGrid().getSquare(pos).addObserver(view);
                robot.getGrid().getSquare(pos).addObserver(view);
            }
        }
    }

    // GESTION DES CLICS

    public void handleGridClick(int x, int y, boolean isPlayerGrid) {
        if (!playerTurn) {
            view.showError("Ce n'est pas votre tour !");
            return;
        }

        Position target = new Position(x, y);

        // Mode placement de piège
        if (placingTrapMode) {
            handleTrapPlacement(target, isPlayerGrid);
            return;
        }

        // Clic sur la grille du joueur (invalide sauf pour placement)
        if (isPlayerGrid) {
            view.showError("Cliquez sur la grille adverse pour attaquer !");
            return;
        }

        // Actions normales sur la grille adverse
        if (view.isShovelSelected()) {
            handleIslandSearch(target);
        } else {
            handleWeaponAttack(target);
        }
    }


    // ACTIONS DU JOUEUR

    private void handleWeaponAttack(Position target) {
        WeaponType weapon = view.getSelectedWeapon();
        TurnResult result = game.playerAttack(weapon, target);

        if (!result.isSuccess()) {
            view.showError(result.getErrorMessage());
            return;
        }

        // Afficher le résultat
        displayPlayerTurnResult(result);

        // Passer au tour du robot
        endPlayerTurn();
    }

    private void handleIslandSearch(Position target) {
        TurnResult result = game.playerSearchIsland(target);

        if (!result.isSuccess()) {
            view.showError(result.getErrorMessage());
            return;
        }

        // Afficher le résultat
        displayIslandSearchResult(result);

        // Si un piège a été trouvé, demander au joueur
        if (result.getTrapFound() != null) {
            int choice = view.showTrapFoundDialog(result.getTrapFound().toString());

            if (choice == 0) {
                // Placer immédiatement
                startPlacingTrapFromInventory(result.getTrapFound());
                return; // Ne pas terminer le tour, attendre le placement
            } else {
                // Ajouter à l'inventaire
                view.updateInventoryDisplay(game.getPlayer().getTrapInventory());
            }
        }

        // Terminer le tour
        endPlayerTurn();
    }



    private void handleTrapPlacement(Position target, boolean isPlayerGrid) {
        if (!isPlayerGrid) {
            view.showError("Vous devez placer le piège sur VOTRE grille !");
            return;
        }

        TurnResult result = game.playerPlaceTrap(trapToPlace, target);

        if (!result.isSuccess()) {
            view.showError(result.getErrorMessage());
            return;
        }

        // Afficher le résultat
        view.showSuccess("Piège placé en " + target.getX() + "," + target.getY() + " !");
        view.setPlayerAction(result.getActionDescription());
        view.appendHistory("Tour " + game.getTurnNumber() + " - " +
                game.getPlayer().getUsername() + ": " +
                result.getActionDescription() + "\n");

        // Colorer la case
        view.colorPlayerGridCell(target.getX(), target.getY(), view.getTrapColor());
        view.updateInventoryDisplay(game.getPlayer().getTrapInventory());

        // Désactiver le mode placement
        exitTrapPlacementMode();

        updatePlayerStatsDisplay();

        // Terminer le tour
        endPlayerTurn();
    }

    // MODE PLACEMENT DE PIEGE

    public void startPlacingTrapFromInventory(TrapType trapType) {
        if (!playerTurn) {
            view.showError("Ce n'est pas votre tour !");
            return;
        }

        if (game.getPlayer().getTrapInventoryCount(trapType) <= 0) {
            view.showError("Vous n'avez pas de " + trapType + " en inventaire !");
            return;
        }

        placingTrapMode = true;
        trapToPlace = trapType;
        view.setPlacingTrapMode(true);
        view.setPlayerAction("Cliquez sur votre grille pour placer le piège");
    }

    public void cancelTrapPlacement() {
        exitTrapPlacementMode();
        view.setPlayerAction("Placement annulé");
    }

    private void exitTrapPlacementMode() {
        placingTrapMode = false;
        trapToPlace = null;
        view.setPlacingTrapMode(false);
    }

    // TOUR DU ROBOT

    private void endPlayerTurn() {
        playerTurn = false;

        TurnResult robotResult = game.playRobotTurn();
        displayRobotTurnResult(robotResult);

        if (game.checkGameOver()) {
            endGame();
            return;
        }

        // Passer au tour suivant
        game.incrementTurn();
        playerTurn = true;
        updateAllDisplays();
    }

    // AFFICHAGE

    private void displayPlayerTurnResult(TurnResult result) {
        String action = result.getActionDescription();
        view.setPlayerAction(action);
        view.appendHistory("Tour " + game.getTurnNumber() + " - " +
                game.getPlayer().getUsername() + ": " + action + "\n");

        if (result.getAttackResult() != null && result.getAttackResult().isSonar()) {
            view.showSonarResult(
                    result.getRedirectedTo().getX(),
                    result.getRedirectedTo().getY(),
                    result.getAttackResult().getOccupiedCells(),
                    true  // C'est le joueur
            );
        }

        if (result.wasTornadoActivated()) {
            view.showTrapEffect("🌪️ TORNADE ACTIVÉE !",
                    "Votre tir a été dévié par la tornade du ROBOT !\n" +
                    "Destination finale : " + result.getRedirectedTo().getX() + "," + result.getRedirectedTo().getY(),
                    false);     // pas bon pour le jouer
        }

        // Afficher les activations de pièges
        if (result.getAttackResult().hadTrapActivations()) {
            for (TrapActivation trap : result.getAttackResult().getTrapActivations()) {
                if (trap.getType() == TrapType.TORNADO) {

                    view.showTrapEffect(
                            "🌪️ TORNADE DÉCLENCHÉE !",
                            "Vous avez activé la TORNADE du ROBOT !\n\n" +
                                    "⚠️ Vos 3 prochains tirs seront déviés vers des positions aléatoires !",
                            false  // Rouge = mauvais pour le joueur
                    );

                } else if (trap.getType() == TrapType.BLACKHOLE) {
                    view.showTrapEffect(
                            "🕳️ TROU NOIR ACTIVÉ !",
                            "Vous avez activé le TROU NOIR du ROBOT !\n\n" +
                                    "💥 Votre attaque vous revient dessus !",
                            false  // Rouge = mauvais pour le joueur
                    );                }
            }
        }

        updateRobotStatsDisplay();
    }


    private void displayIslandSearchResult(TurnResult result) {
        String action = result.getActionDescription();
        view.setPlayerAction(action);
        view.appendHistory("Tour " + game.getTurnNumber() + " - " +
                game.getPlayer().getUsername() + ": " + action + "\n");

        if (result.getWeaponFound() != null) {
            updateWeaponsDisplay();
        }
    }

    private void displayRobotTurnResult(TurnResult result) {
        String action = result.getActionDescription();
        view.setRobotAction(action);
        view.appendHistory("Tour " + game.getTurnNumber() + " - Robot: " + action + "\n");


        if (result.getAttackResult() != null && result.getAttackResult().isSonar()) {
            view.showSonarResult(
                    result.getRedirectedTo().getX(),
                    result.getRedirectedTo().getY(),
                    result.getAttackResult().getOccupiedCells(),
                    false  // C'est le robot
            );
        }

        if (result.wasTornadoActivated()) {
            view.showTrapEffect(
                    "🌪️ VOTRE TORNADE FONCTIONNE !",
                    "Votre tornade a dévié l'attaque du robot !\n" +
                            "Il visait une position, mais a touché : " +
                            result.getRedirectedTo().getX() + "," + result.getRedirectedTo().getY(),
                    true  // Vert = bon pour le joueur
            );
        }

        if (result.getAttackResult() != null && result.getAttackResult().hadTrapActivations()) {
            for (TrapActivation trap : result.getAttackResult().getTrapActivations()) {
                if (trap.getType() == TrapType.TORNADO) {
                    // Le robot a activé VOTRE tornade
                    view.showTrapEffect(
                            "🌪️ VOTRE TORNADE DÉCLENCHÉE !",
                            "Le robot a activé VOTRE TORNADE !\n\n" +
                                    "✅ Ses 3 prochains tirs seront déviés !",
                            true  // Vert = bon pour le joueur
                    );
                } else if (trap.getType() == TrapType.BLACKHOLE) {
                    // Le robot a activé VOTRE trou noir
                    view.showTrapEffect(
                            "🕳️ VOTRE TROU NOIR ACTIVÉ !",
                            "Le robot a activé VOTRE TROU NOIR !\n\n" +
                                    "✅ Son attaque lui revient dessus !",
                            true  // Vert = bon pour le joueur
                    );
                }
            }
        }
        if (result.getAttackResult() != null) {
            updatePlayerStatsDisplay();
        }
    }


    private void updateAllDisplays() {
        view.setTurnNumber(game.getTurnNumber());
        updateWeaponsDisplay();
    }

    private void updateWeaponsDisplay() {
        Player player = game.getPlayer();
        Player robot = game.getRobot();

        // Armes du joueur
        view.updatePlayerWeapons(
                player.getWeaponCount(WeaponType.MISSILE),
                player.getWeaponCount(WeaponType.BOMB),
                player.getWeaponCount(WeaponType.SONAR)
        );

        // Armes du robot
        view.updateRobotWeapons(
                robot.getWeaponCount(WeaponType.MISSILE),
                robot.getWeaponCount(WeaponType.BOMB),
                robot.getWeaponCount(WeaponType.SONAR)
        );

        // Activer/désactiver les boutons
        view.setWeaponEnabled(WeaponType.BOMB, player.getWeaponCount(WeaponType.BOMB) > 0);
        view.setWeaponEnabled(WeaponType.SONAR, player.getWeaponCount(WeaponType.SONAR) > 0);
    }

    public void updatePlayerStatsDisplay() {
        GameStats stats = GameStats.calculate(game.getPlayer(), game.getRobot(), game.getConfig().getGridSize());

        view.updatePlayerStats(
                stats.getBoatsIntact(),
                stats.getBoatsTouched(),
                stats.getBoatsSunk(),
                stats.getMissedShots(),
                stats.getHitCells(),
                stats.getTotalBoatCells()
        );
    }

    public void updateRobotStatsDisplay() {
        // Calculer les stats du robot
        GameStats stats = GameStats.calculate(
                game.getRobot(),      // Le robot (ses bateaux)
                game.getPlayer(),     // Le joueur (pour savoir où le robot a tiré)
                game.getConfig().getGridSize()
        );

        // Transmettre à la Vue
        view.updateRobotStats(
                stats.getBoatsIntact(),
                stats.getBoatsTouched(),
                stats.getBoatsSunk(),
                stats.getMissedShots(),
                stats.getHitCells(),
                stats.getTotalBoatCells()
        );
    }


    // FIN DE PARTIE

    private void endGame() {
        String winner = game.getWinner();

        // Calculer les stats proprement
        GameStats playerStats = GameStats.calculate(game.getPlayer(), game.getRobot(), config.getGridSize());
        GameStats robotStats = GameStats.calculate(game.getRobot(), game.getPlayer(), config.getGridSize());

        // Créer la vue de fin
        EndController endController = new EndController( centralController, winner, game.getTurnNumber(), playerStats, robotStats, game.getPlayer().getUsername() );

        view.dispose();
    }

    // GET

    public boolean isInIsland(int x, int y) {
        return game.hasIsland() && game.getRobot().isSquareOnIsland(new Position(x, y));
    }

    public boolean hasIsland() {
        return game.hasIsland();
    }

    public int getNumberBoats() {
        return config.getNumberBoatsTotal();
    }

    public int getNumberBoatSquares() {
        return config.getTotalBoatSquares();
    }

    public Player getPlayer() {
        return game.getPlayer();
    }

    public Player getRobot() { return game.getRobot(); }

    public void quit() {
        System.exit(0);
    }


}
