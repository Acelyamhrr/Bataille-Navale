package controller;

import model.enums.*;
import model.game.GameConfig;
import view.ConfigurationView;

/**
 * Controller pour la configuration du jeu.
 * pour Valider et créer le GameConfig.
 */
public class ConfigurationController {
    private final ConfigurationView view;

    public ConfigurationController(ConfigurationView view) {
        this.view = view;
    }

    /**
     * Valide la configuration et crée le GameConfig.
     * @return GameConfig si valide, null sinon (avec affichage d'erreur)
     */
    public GameConfig validateAndCreateConfig() {
        // 1. Récupérer les données de la vue
        String username = view.getUsername();
        int gridSize = view.getGridSize();
        ModeGame mode = view.isStandardMode() ? ModeGame.STANDARD : ModeGame.ISLAND;
        RobotMode robot = view.isRandomRobot() ? RobotMode.RANDOM : RobotMode.SMART;

        String trapMode = view.getTrapPlacementMode();
        TrapPlacement trap;

        switch (trapMode) {
            case "FIXED":
                trap = TrapPlacement.FIXED;
                break;
            case "RANDOM":
                trap = TrapPlacement.RANDOM;
                break;
            default:
                trap = TrapPlacement.MANUAL;
        }

        int[] boats = view.isDefaultBoats() ? new int[]{1, 1, 1, 1, 1} : view.getCustomBoatNumbers();

        // 2. Créer le Modèle
        GameConfig config = new GameConfig(mode, gridSize, robot, boats, username, trap);

        // 3. Valider avec le Modèle
        if (!config.isValid()) {
            StringBuilder error = new StringBuilder("Configuration invalide !\n");
            if (username.isEmpty()) error.append("- Nom vide\n");
            if (config.getTotalBoatSquares() > 35) error.append("- Max 35 cases\n");
            view.showError(error.toString());
            return null;
        }

        return config;
    }
}