package controller;

import model.enums.*;
import model.game.GameConfig;
import view.ConfigurationView;

/**
 * Controller pour la configuration du jeu.
 * pour Valider et créer le GameConfig.
 */
public class ConfigurationController {
    private ConfigurationView _view;

    public ConfigurationController() {}

    public void setView(ConfigurationView view) {
        this._view = view;
    }

    public boolean numberSquaresValid(int[] boats) {
        boolean modeIsland = !_view.isStandardMode();
        int gridSize = _view.getGridSize();
        return GameConfig.numberSquaresValid(gridSize, boats, modeIsland);
    }

    /**
     * Valide la configuration et crée le GameConfig.
     * @return GameConfig si valide, null sinon (avec affichage d'erreur)
     */
    public GameConfig validateAndCreateConfig() {
        // 1. Récupérer les données de la vue
        String username = _view.getUsername();
        int gridSize = _view.getGridSize();
        ModeGame mode = _view.isStandardMode() ? ModeGame.STANDARD : ModeGame.ISLAND;
        RobotMode robot = _view.isRandomRobot() ? RobotMode.RANDOM : RobotMode.SMART;

        String trapMode = _view.getTrapPlacementMode();
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

        int[] boats = _view.isDefaultBoats() ? new int[]{1, 1, 1, 1, 1} : _view.getCustomBoatNumbers();

        // 2. Créer le Modèle
        GameConfig config = new GameConfig(mode, gridSize, robot, boats, username, trap);

        // 3. Valider avec le Modèle
        if (!config.isValid()) {
            StringBuilder error = new StringBuilder("Configuration invalide !\n");
            if (username.isEmpty()) error.append("- Nom vide\n");
            if (config.getTotalBoatSquares() > 35) error.append("- Max 35 cases\n");
            _view.showError(error.toString());
            return null;
        }

        return config;
    }
}