package controller;

import model.game.GameStats;
import view.EndView;

public class EndController {

    private EndView view;
    private CentralController centralController;
    private String winner;
    private int turnNumber;
    private GameStats playerStats;
    private GameStats robotStats;
    private String playerName;

    public EndController(CentralController centralController, String winner, int turnNumber, GameStats playerStats, GameStats robotStats, String playerName) {
        this.centralController = centralController;
        this.winner = winner;
        this.turnNumber = turnNumber;
        this.playerStats = playerStats;
        this.robotStats = robotStats;
        this.playerName = playerName;

        initializeView();
    }

    private void initializeView() {
        this.view = new EndView(winner, turnNumber, playerStats, robotStats, playerName, this);
        this.view.setVisible(true);
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
        int choice = view.showRestartChoiceDialog();

        if (choice == -1) {
            return;
        }

        view.dispose();

        switch (choice) {
            case 0:
                centralController.restartWithSamePlacement();
                break;
            case 1:
                centralController.restartWithSameConfig();
                break;
            case 2:
                centralController.restartFromBeginning();
                break;
        }
    }

    public EndView getView() {
        return view;
    }
}
