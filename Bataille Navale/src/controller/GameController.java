package controller;

import model.contents.fleet.Boat;
import model.enums.*;
import model.game.*;
import model.game.Results.TurnResult;
import model.grid.Position;
import model.players.Player;
import view.GameView;


/**
 * Controller pour la partie de jeu.
 * intermédiaire entre la vue et le modèle.
 * Ne contient AUCUNE logique métier.
 * demande au modèle de faire les calculs
 * et transmet les res à la vue
 */
public class GameController {
    private Game _game;
    private GameView _view;
    private GameConfig _config;

    private boolean _playerTurn = true;
    private boolean _placingTrapMode = false;
    private TrapType _trapToPlace = null;

    private CentralController _centralController;

    public GameController(GameConfig config, GamePlacement placement, CentralController centralController) {
        this._config = config;
        this._game = new Game(config, placement);

        this._centralController = centralController;

        _game.initialize();
        setupRobotStrategy();
    }

    private void setupRobotStrategy() {
        Player robot = _game.getRobot();
        if (_config.getRobotMode() == RobotMode.SMART) {
            robot.setStrategy(new model.game.RobotStrategy.SmartRobotStrategy());
        } else {
            robot.setStrategy(new model.game.RobotStrategy.RandomRobotStrategy());
        }
    }

    public void setView(GameView view) {
        this._view = view;
        registerObservers();        // enregistre la vue comme observer sur tous les boats et squares
        updateAllDisplays();

        updatePlayerStatsDisplay();
        updateRobotStatsDisplay();
    }

    // OBSERVATEURS

    private void registerObservers() {
        for (Boat boat : _game.getPlayer().getBoats()) {
            boat.addObserver(_view);
        }

        for (Boat boat : _game.getRobot().getBoats()) {
            boat.addObserver(_view);
        }

        Player player = _game.getPlayer();
        Player robot = _game.getRobot();

        for (int x = 0; x < _config.getGridSize(); x++) {
            for (int y = 0; y < _config.getGridSize(); y++) {
                Position pos = new Position(x, y);
                player.getGrid().getSquare(pos).addObserver(_view);
                robot.getGrid().getSquare(pos).addObserver(_view);
            }
        }
    }

    // GESTION DES CLICS

    public void handleGridClick(int x, int y, boolean isPlayerGrid) {
        if (!_playerTurn) {
            _view.showError("Ce n'est pas votre tour !");
            return;
        }

        Position target = new Position(x, y);

        // Mode placement de piège
        if (_placingTrapMode) {
            handleTrapPlacement(target, isPlayerGrid);
            return;
        }

        // Clic sur la grille du joueur (invalide sauf pour placement)
        if (isPlayerGrid) {
            _view.showError("Cliquez sur la grille adverse pour attaquer !");
            return;
        }

        // Actions normales sur la grille adverse
        if (_view.isShovelSelected()) {
            handleIslandSearch(target);
        } else {
            handleWeaponAttack(target);
        }
    }


    // ACTIONS DU JOUEUR

    private void handleWeaponAttack(Position target) {
        WeaponType weapon = _view.getSelectedWeapon();
        TurnResult result = _game.playerAttack(weapon, target);

        if (!result.isSuccess()) {
            _view.showError(result.getErrorMessage());
            return;
        }

        // Afficher le résultat
        displayPlayerTurnResult(result);

        // Passer au tour du robot
        endPlayerTurn();
    }

    private void handleIslandSearch(Position target) {
        TurnResult result = _game.playerSearchIsland(target);

        if (!result.isSuccess()) {
            _view.showError(result.getErrorMessage());
            return;
        }

        // Afficher le résultat
        displayIslandSearchResult(result);

        // Si un piège a été trouvé, demander au joueur
        if (result.getTrapFound() != null) {
            int choice = _view.showTrapFoundDialog(result.getTrapFound().toString());

            if (choice == 0) {
                // Placer immédiatement
                startPlacingTrapFromInventory(result.getTrapFound());
                return; // Ne pas terminer le tour, attendre le placement
            } else {
                // Ajouter à l'inventaire
                _view.updateInventoryDisplay(_game.getPlayer().getTrapInventory());
            }
        }

        // Terminer le tour
        endPlayerTurn();
    }



    private void handleTrapPlacement(Position target, boolean isPlayerGrid) {
        if (!isPlayerGrid) {
            _view.showError("Vous devez placer le piège sur VOTRE grille !");
            return;
        }

        TurnResult result = _game.playerPlaceTrap(_trapToPlace, target);

        if (!result.isSuccess()) {
            _view.showError(result.getErrorMessage());
            return;
        }

        // Afficher le résultat
        _view.showSuccess("Piège placé en " + target.getX() + "," + target.getY() + " !");
        _view.setPlayerAction(result.getActionDescription());
        _view.appendHistory("Tour " + _game.getTurnNumber() + " - " +
                _game.getPlayer().getUsername() + ": " +
                result.getActionDescription() + "\n");

        // Colorer la case
        _view.colorPlayerGridCell(target.getX(), target.getY(), _view.getTrapColor());
        _view.updateInventoryDisplay(_game.getPlayer().getTrapInventory());

        // Désactiver le mode placement
        exitTrapPlacementMode();

        updatePlayerStatsDisplay();

        // Terminer le tour
        endPlayerTurn();
    }

    // MODE PLACEMENT DE PIEGE

    public void startPlacingTrapFromInventory(TrapType trapType) {
        if (!_playerTurn) {
            _view.showError("Ce n'est pas votre tour !");
            return;
        }

        if (_game.getPlayer().getTrapInventoryCount(trapType) <= 0) {
            _view.showError("Vous n'avez pas de " + trapType + " en inventaire !");
            return;
        }

        _placingTrapMode = true;
        _trapToPlace = trapType;
        _view.setPlacingTrapMode(true);
        _view.setPlayerAction("Cliquez sur votre grille pour placer le piège");
    }

    public void cancelTrapPlacement() {
        exitTrapPlacementMode();
        _view.setPlayerAction("Placement annulé");
    }

    private void exitTrapPlacementMode() {
        _placingTrapMode = false;
        _trapToPlace = null;
        _view.setPlacingTrapMode(false);
    }

    // TOUR DU ROBOT

    private void endPlayerTurn() {
        _playerTurn = false;

        TurnResult robotResult = _game.playRobotTurn();
        displayRobotTurnResult(robotResult);

        if (_game.checkGameOver()) {
            endGame();
            return;
        }

        // Passer au tour suivant
        _game.incrementTurn();
        _playerTurn = true;
        updateAllDisplays();
    }

    // AFFICHAGE

    private void displayPlayerTurnResult(TurnResult result) {
        String action = result.getActionDescription();
        _view.setPlayerAction(action);
        _view.appendHistory("Tour " + _game.getTurnNumber() + " - " +
                _game.getPlayer().getUsername() + ": " + action + "\n");

        if (result.getAttackResult() != null && result.getAttackResult().isSonar()) {
            _view.showSonarResult(
                    result.getRedirectedTo().getX(),
                    result.getRedirectedTo().getY(),
                    result.getAttackResult().getOccupiedCells(),
                    true  // C'est le joueur
            );
        }

        if (result.wasTornadoActivated()) {
            _view.showTrapEffect("🌪️ TORNADE ACTIVÉE !",
                    "Votre tir a été dévié par la tornade du ROBOT !\n" +
                    "Destination finale : " + result.getRedirectedTo().getX() + "," + result.getRedirectedTo().getY(),
                    false);     // pas bon pour le jouer
        }

        // Afficher les activations de pièges
        if (result.getAttackResult().hadTrapActivations()) {
            for (TrapActivation trap : result.getAttackResult().getTrapActivations()) {
                if (trap.getType() == TrapType.TORNADO) {

                    _view.showTrapEffect(
                            "🌪️ TORNADE DÉCLENCHÉE !",
                            "Vous avez activé la TORNADE du ROBOT !\n\n" +
                                    "⚠️ Vos 3 prochains tirs seront déviés vers des positions aléatoires !",
                            false  // Rouge = mauvais pour le joueur
                    );

                } else if (trap.getType() == TrapType.BLACKHOLE) {
                    _view.showTrapEffect(
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
        _view.setPlayerAction(action);
        _view.appendHistory("Tour " + _game.getTurnNumber() + " - " +
                _game.getPlayer().getUsername() + ": " + action + "\n");

        if(result.wasTornadoActivated()){
            _view.showTrapEffect("🌪️ TORNADE ACTIVÉE !",
                    "Votre fouille a été dévié par la tornade du ROBOT !\n" +
                            "Destination finale : " + result.getRedirectedTo().getX() + "," + result.getRedirectedTo().getY(),
                    false);     // pas bon pour le jouer
        }

        if (result.getWeaponFound() != null) {
            updateWeaponsDisplay();
        }
    }

    private void displayRobotTurnResult(TurnResult result) {
        String action = result.getActionDescription();
        _view.setRobotAction(action);
        _view.appendHistory("Tour " + _game.getTurnNumber() + " - Robot: " + action + "\n");


        if (result.getAttackResult() != null && result.getAttackResult().isSonar()) {
            _view.showSonarResult(
                    result.getRedirectedTo().getX(),
                    result.getRedirectedTo().getY(),
                    result.getAttackResult().getOccupiedCells(),
                    false  // C'est le robot
            );
        }

        if (result.wasTornadoActivated()) {
            _view.showTrapEffect(
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
                    _view.showTrapEffect(
                            "🌪️ VOTRE TORNADE DÉCLENCHÉE !",
                            "Le robot a activé VOTRE TORNADE !\n\n" +
                                    "✅ Ses 3 prochains tirs seront déviés !",
                            true  // Vert = bon pour le joueur
                    );
                } else if (trap.getType() == TrapType.BLACKHOLE) {
                    // Le robot a activé VOTRE trou noir
                    _view.showTrapEffect(
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
        _view.setTurnNumber(_game.getTurnNumber());
        updateWeaponsDisplay();
    }

    private void updateWeaponsDisplay() {
        Player player = _game.getPlayer();
        Player robot = _game.getRobot();

        // Armes du joueur
        _view.updatePlayerWeapons(
                player.getWeaponCount(WeaponType.MISSILE),
                player.getWeaponCount(WeaponType.BOMB),
                player.getWeaponCount(WeaponType.SONAR)
        );

        // Armes du robot
        _view.updateRobotWeapons(
                robot.getWeaponCount(WeaponType.MISSILE),
                robot.getWeaponCount(WeaponType.BOMB),
                robot.getWeaponCount(WeaponType.SONAR)
        );

        // Activer/désactiver les boutons
        _view.setWeaponEnabled(WeaponType.BOMB, player.getWeaponCount(WeaponType.BOMB) > 0);
        _view.setWeaponEnabled(WeaponType.SONAR, player.getWeaponCount(WeaponType.SONAR) > 0);
    }

    public void updatePlayerStatsDisplay() {
        GameStats stats = GameStats.calculate(_game.getPlayer(), _game.getRobot(), _game.getConfig().getGridSize());

        _view.updatePlayerStats(
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
                _game.getRobot(),      // Le robot (ses bateaux)
                _game.getPlayer(),     // Le joueur (pour savoir où le robot a tiré)
                _game.getConfig().getGridSize()
        );

        // Transmettre à la Vue
        _view.updateRobotStats(
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
        String winner = _game.getWinner();

        // Calculer les stats proprement
        GameStats playerStats = GameStats.calculate(_game.getPlayer(), _game.getRobot(), _config.getGridSize());
        GameStats robotStats = GameStats.calculate(_game.getRobot(), _game.getPlayer(), _config.getGridSize());

        // Créer la vue de fin
        EndController endController = new EndController(_centralController, winner, _game.getTurnNumber(), playerStats, robotStats, _game.getPlayer().getUsername() );

        _view.dispose();
    }

    // GET

    public boolean isInIsland(int x, int y) {
        return _game.hasIsland() && _game.getRobot().isSquareOnIsland(new Position(x, y));
    }

    public boolean hasIsland() {
        return _game.hasIsland();
    }

    public int getNumberBoats() {
        return _config.getNumberBoatsTotal();
    }

    public int getNumberBoatSquares() {
        return _config.getTotalBoatSquares();
    }

    public Player getPlayer() {
        return _game.getPlayer();
    }

    public Player getRobot() { return _game.getRobot(); }

    public void quit() {
        System.exit(0);
    }


}
