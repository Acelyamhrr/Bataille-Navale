package controller;

import model.game.GameConfig;
import model.game.GamePlacement;
import model.grid.Grid;
import view.*;


/**
 * Contrôleur principal.
 * Fait l'entre deux entre la vue et le modèle
 * Ne contient aucune logique métier, tout est dans model
 */
public class CentralController {
    private MenuView _menuView;
    private ConfigurationView _configView;
    private PlacementView _placementView;
    private GameView _gameView;

    private ConfigurationController _configController;
    private PlacementController _placementController;
    private GameController _gameController;

    private GameConfig _currentConfig;
    private GamePlacement _currentPlacement;

    public CentralController() {}

    // démarre le jeu
    public void start() {
        _menuView = new MenuView();
        connectMenuView();
        _menuView.setVisible(true);
    }

    private void connectMenuView() {
        _menuView.addNewGameListener(e -> openConfiguration());
        _menuView.addQuitListener(e -> quit());
    }

    // ouvre l'écran de config
    private void openConfiguration() {
        _configController = new ConfigurationController();
        _configView = new ConfigurationView(_configController);
        _configController.setView(_configView);
        connectConfigView();
        _configView.setVisible(true);
        _menuView.setVisible(false);
    }

    // events de la config
    private void connectConfigView() {
        _configView.addBackListener(e -> backToMenu());
        _configView.addNextListener(e -> validateConfiguration());
    }

    // retour au menu
    private void backToMenu() {
        _configView.dispose();
        _menuView.setVisible(true);
    }

    private void validateConfiguration() {
        _currentConfig = _configController.validateAndCreateConfig();

        if (_currentConfig == null ) {
            return;
        }

        _configView.showSuccess("Config ok : " + _currentConfig);
        openPlacement();
    }

    private void openPlacement() {
        _placementController = new PlacementController(_currentConfig);
        _placementView = new PlacementView(_currentConfig.getGridSize(), _currentConfig.getUsername(), _placementController, _placementController.getModel());
        _placementController.setView(_placementView);
        connectPlacementView();
        _placementView.setVisible(true);
        _configView.setVisible(false);
        _placementController.initializePlacement();
    }

    private void connectPlacementView() {
        _placementView.addBackListener(e -> backToConfig());
        _placementView.addValidateListener(e -> validatePlacement());
    }

    private void backToConfig() {
        _placementView.dispose();
        _configView.setVisible(true);
    }

    private void validatePlacement() {
        _currentPlacement = _placementController.validateAndCreatePlacement();

        if (_currentPlacement == null) {
            return;
        }

        _placementView.showSuccess("Placement validé ! Prêt à jouer");
        openGame();
    }

    private void openGame() {
        this._gameController = new GameController(_currentConfig, _currentPlacement, this);
        _gameView = new GameView(_currentConfig.getGridSize(), _currentConfig.getUsername(), _gameController, this._currentPlacement);
        this._gameController.setView(_gameView);

        _gameView.setVisible(true);
        _placementView.setVisible(false);

        _gameView.showSuccess("La partie commence !");
    }

    // quit
    private void quit() {
        System.exit(0);
    }

    /**
     * Recommence avec exactement le même placement.
     */
    public void restartWithSamePlacement() {
        if (_gameView != null) {
            _gameView.dispose();
            _gameView = null;
        }

        // Reset les deux grilles
        Grid playerGrid = _gameController.getPlayer().getGrid();
        Grid robotGrid = _gameController.getRobot().getGrid();

        playerGrid.reset();
        robotGrid.reset();

        this._currentPlacement = new GamePlacement(playerGrid, robotGrid);

        openGame();
    }

    /**
     * Recommence avec la même config mais nouveaux placements.
     */
    public void restartWithSameConfig() {
        if (_gameView != null) {
            _gameView.dispose();
            _gameView = null;
        }
        _currentPlacement = null;
        openPlacement();
    }

    /**
     * Recommence complètement depuis la configuration.
     */
    public void restartFromBeginning() {
        if (_gameView != null) {
            _gameView.dispose();
            _gameView = null;
        }
        _currentConfig = null;
        _currentPlacement = null;
        openConfiguration();
    }
}