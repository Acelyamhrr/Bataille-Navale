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
        placementView = new PlacementView(currentConfig.getGridSize(), currentConfig.getUsername());
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
        gameView = new GameView(currentConfig.getGridSize(), currentConfig.getUsername());
        connectGameView();
        gameView.setVisible(true);
        placementView.setVisible(false);

        // DEBUG: Afficher les bateaux et pièges du robot
        debugShowRobotPlacement();

        // TODO: Initialiser la partie (créer le Game, placer les bateaux, etc.)
        gameView.showSuccess("La partie commence !");
    }

    private void debugShowRobotPlacement() {
        // Récupérer tous les bateaux du robot depuis currentPlacement
        List<Position> robotBoatPositions = new ArrayList<>();
        List<Integer> robotBoatSizes = new ArrayList<>();

        Map<BoatName, List<Position>> robotBoats = currentPlacement.getBoatPlacementsRobot();
        for (Map.Entry<BoatName, List<Position>> entry : robotBoats.entrySet()) {
            for (Position pos : entry.getValue()) {
                robotBoatPositions.add(pos);
                robotBoatSizes.add(getBoatSize(entry.getKey()));
            }
        }

        // Récupérer les pièges du robot
        List<Position> robotTrapPositions = new ArrayList<>();
        Map<TrapType, Position> robotTraps = currentPlacement.getTrapPlacementsRobot();
        for (Position pos : robotTraps.values()) {
            robotTrapPositions.add(pos);
        }

        gameView.debugShowRobotBoats(robotBoatPositions, robotBoatSizes);
        gameView.debugShowRobotTraps(robotTrapPositions);
    }

    private int getBoatSize(BoatName boat) {
        switch (boat) {
            case AIRCRAFT_CARRIER: return 5;
            case CRUISER: return 4;
            case DESTROYER: return 3;
            case SUBMARINE: return 3;
            case TORPEDO_BOAT: return 2;
            default: return 0;
        }
    }

    private void connectGameView() {
        // TODO: Connecter les événements de la GameView
    }

    // quit
    private void quit() {
        System.exit(0);
    }
}