package controller;

import model.enums.*;
import model.game.GameConfig;
import model.game.GamePlacement;
import model.grid.Position;
import view.*;

import java.util.*;
import java.util.List;


/**
 * Contrôleur principal.
 * CONNAÎT les Vues et le Modèle.
 * Fait le lien entre les deux.
 */
public class CentralController {
    private MenuView menuView;
    private ConfigurationView configView;
    private PlacementView placementView;
    private GameView gameView;

    private ConfigurationController configController;
    private PlacementController placementController;
    private GameController gameController;

    private GameConfig currentConfig;
    private GamePlacement currentPlacement;

    public CentralController() {}

    // démarre le jeu
    public void start() {
        menuView = new MenuView();
        connectMenuView();
        menuView.setVisible(true);
    }

    private void connectMenuView() {
        menuView.addNewGameListener(e -> openConfiguration());
        menuView.addQuitListener(e -> quit());
    }

    // ouvre l'écran de config
    private void openConfiguration() {
        configView = new ConfigurationView();
        configController = new ConfigurationController(configView);
        connectConfigView();
        configView.setVisible(true);
        menuView.setVisible(false);
    }

    // events de la config
    private void connectConfigView() {
        configView.addBackListener(e -> backToMenu());
        configView.addNextListener(e -> validateConfiguration());
    }

    // retour au menu
    private void backToMenu() {
        configView.dispose();
        menuView.setVisible(true);
    }

    private void validateConfiguration() {
        currentConfig = configController.validateAndCreateConfig();

        if (currentConfig == null ) {
            return;
        }

        configView.showSuccess("Config ok : " + currentConfig);
        openPlacement();
    }

    private void openPlacement() {
        placementView = new PlacementView(currentConfig.getGridSize(), currentConfig.getUsername(), currentConfig.getModeGame() == ModeGame.ISLAND);
        placementController = new PlacementController(placementView, currentConfig);
        connectPlacementView();
        placementView.setVisible(true);
        configView.setVisible(false);
        placementController.initializePlacement();
    }

    private void connectPlacementView() {
        placementView.addBackListener(e -> backToConfig());
        placementView.addValidateListener(e -> validatePlacement());
        placementView.addFixedModeListener(e -> placementController.applyFixedPlacement());
        placementView.addRandomModeListener(e -> placementController.applyRandomPlacement());
        placementView.addManualModeListener(e -> placementController.enableManualPlacement());
        placementView.setGridClickCallback((x, y) -> placementController.onGridClick(x, y));
        placementView.setGridHoverCallback((x, y) -> placementController.updateGrid());
    }

    private void backToConfig() {
        placementView.dispose();
        configView.setVisible(true);
    }

    private void validatePlacement() {
        currentPlacement = placementController.validateAndCreatePlacement();

        if (currentPlacement == null) {
            return;
        }

        placementView.showSuccess("Placement validé ! Prêt à jouer");
        openGame();
    }

    private void openGame() {
        this.gameController = new GameController(currentConfig, currentPlacement);
        gameView = new GameView(currentConfig.getGridSize(), currentConfig.getUsername(), gameController);
        this.gameController.setView(gameView);

        gameView.setVisible(true);
        placementView.setVisible(false);

        gameView.showSuccess("La partie commence !");
    }

    // quit
    private void quit() {
        System.exit(0);
    }
}