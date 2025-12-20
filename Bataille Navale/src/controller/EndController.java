package controller;

import model.game.GameStats;
import view.EndView;

public class EndController {

    private EndView _view;
    private CentralController _centralController;
    private String _winner;
    private int _turnNumber;
    private GameStats _playerStats;
    private GameStats _robotStats;
    private String _playerName;

    public EndController(CentralController centralController, String winner, int turnNumber, GameStats playerStats, GameStats robotStats, String playerName) {
        this._centralController = centralController;
        this._winner = winner;
        this._turnNumber = turnNumber;
        this._playerStats = playerStats;
        this._robotStats = robotStats;
        this._playerName = playerName;

        initializeView();
    }

    private void initializeView() {
        this._view = new EndView(_winner, _turnNumber, _playerStats, _robotStats, _playerName, this);
        this._view.setVisible(true);
    }

    /**
     * Gère le clic su rle btn "Quitter"
     */
    public void handleQuit() {
        System.exit(0);
    }

    /**
     * Gère le clic sur le btn "Recommencer"
     */
    public void handleRestart() {
        int choice = _view.showRestartChoiceDialog();

        if (choice == -1) {
            return;
        }

        _view.dispose();

        switch (choice) {
            case 0:
                _centralController.restartWithSamePlacement();
                break;
            case 1:
                _centralController.restartWithSameConfig();
                break;
            case 2:
                _centralController.restartFromBeginning();
                break;
        }
    }

    public EndView getView() {
        return _view;
    }
}
