package view;

import controller.GameController;
import model.Observer;
import model.enums.*;
import model.game.GamePlacement;
import model.grid.Position;
import model.players.Player;
import view.dialogs.GameDialogs;
import view.panels.*;

import static view.utils.GameColors.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

/**
 * Vue principale du jeu de bataille navale.
 * Affiche les deux grilles (joueur et robot), les statistiques,
 * l'historique des actions et la sélection d'armes.
 * Implémente Observer pour être notifiée des événements du jeu.
 */
public class GameView extends JFrame implements Observer {
    /** Taille de la grille de jeu */
    private final int _gridSize;

    /** Nom du joueur humain */
    private final String _username;

    /** Référence au contrôleur de jeu */
    private GameController _controller;

    // === Composants principaux ===

    /** Label affichant le numéro du tour actuel */
    private JLabel _lblTurn;

    /** Panel de statistiques du joueur */
    private PlayerStatsPanel _pnlPlayerStats;

    /** Panel de statistiques du robot */
    private PlayerStatsPanel _pnlRobotStats;

    /** Panel de la grille du joueur */
    private GameGridPanel _pnlPlayerGrid;

    /** Panel de la grille du robot */
    private GameGridPanel _pnlRobotGrid;

    /** Panel de sélection d'armes */
    private WeaponSelectionPanel _pnlWeaponSelection;

    /** Panel d'historique et actions */
    private ActionHistoryPanel _pnlActionHistory;

    /** Panel d'inventaire des pièges (intégré dans playerStats) */
    private InventoryPanel _pnlInventory;

    // Menu

    /** Menu popup pour options de jeu */
    private JPopupMenu _menuPopup;

    /** Item de menu pour afficher la légende */
    private JMenuItem _menuItemLegend;

    /** Item de menu pour quitter */
    private JMenuItem _menuItemQuit;

    /**
     * Constructeur de la vue de jeu.
     *
     * @param gridSize La taille de la grille (6 à 10)
     * @param username Le nom du joueur humain
     * @param gameController Le contrôleur de jeu
     * @param placement Le placement initial des éléments
     */
    public GameView(int gridSize, String username, GameController gameController, GamePlacement placement) {
        this._gridSize = gridSize;
        this._username = username;
        this._controller = gameController;

        setTitle("Bataille Navale - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);

        initComponents(placement);
    }

    /**
     * Initialise tous les composants graphiques de la fenêtre.
     *
     * @param placement Le placement initial des éléments
     */
    private void initComponents(GamePlacement placement) {
        setLayout(new BorderLayout(10, 10));

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // En haut : Menu + Numéro du tour
        JPanel topPanel = new JPanel(new BorderLayout());

        // Menu
        JButton menuButton = new JButton("MENU");
        menuButton.setFont(new Font("Arial", Font.BOLD, 20));
        menuButton.setPreferredSize(new Dimension(50, 40));
        menuButton.addActionListener(e -> showMenu(menuButton));
        topPanel.add(menuButton, BorderLayout.WEST);

        // Numéro du tour
        _lblTurn = new JLabel("Tour 1", SwingConstants.CENTER);
        _lblTurn.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(_lblTurn, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Au centre : Grilles + Stats + Actions
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        _pnlInventory = new InventoryPanel(_controller);

        // Créer les panels de stats (avec inventaire pour le joueur)
        _pnlPlayerStats = new PlayerStatsPanel(
                _username,
                false,
                _controller.getNumberBoats(),
                _controller.getNumberBoatSquares(),
                _controller.hasIsland(),
                _pnlInventory
        );

        _pnlRobotStats = new PlayerStatsPanel(
                "Robot",
                true,
                _controller.getNumberBoats(),
                _controller.getNumberBoatSquares(),
                _controller.hasIsland(),
                null // Pas d'inventaire pour le robot
        );

        // Créer les panels de grille
        _pnlPlayerGrid = new GameGridPanel(_gridSize, true, _controller, placement);
        _pnlRobotGrid = new GameGridPanel(_gridSize, false, _controller, placement);

        // Disposition avec GridBagLayout
        JPanel gridsAndStatsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Stats joueur (gauche)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.15;
        gbc.weighty = 1.0;
        gridsAndStatsPanel.add(_pnlPlayerStats, gbc);

        // Grille joueur
        gbc.gridx = 1;
        gbc.weightx = 0.35;
        gridsAndStatsPanel.add(_pnlPlayerGrid, gbc);

        // Grille robot
        gbc.gridx = 2;
        gbc.weightx = 0.35;
        gridsAndStatsPanel.add(_pnlRobotGrid, gbc);

        // Stats robot
        gbc.gridx = 3;
        gbc.weightx = 0.15;
        gridsAndStatsPanel.add(_pnlRobotStats, gbc);

        centerPanel.add(gridsAndStatsPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // En bas : Sélection d'armes

        ActionHistoryPanel actionHistory = new ActionHistoryPanel(_username);

        // Panel du bas
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.add(actionHistory.getActionsPanel(), BorderLayout.NORTH);

        _pnlWeaponSelection = new WeaponSelectionPanel();
        JSplitPane splitBottom = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                actionHistory.getHistoryPanel(),
                _pnlWeaponSelection
        );

        splitBottom.setResizeWeight(0.5);   // 50 / 50
        splitBottom.setDividerSize(6);
        splitBottom.setEnabled(false);      // empêche de bouger la barre

        bottomPanel.add(splitBottom, BorderLayout.CENTER);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        _pnlActionHistory = actionHistory;

        add(mainPanel);

        // Initialiser le menu popup
        createMenuPopup();
    }

    /**
     * Affiche un dialogue pour un piège trouvé.
     *
     * @param trapName Le nom du piège
     * @return 0 pour placer maintenant, 1 pour mettre en inventaire
     */
    public int showTrapFoundDialog(String trapName) {
        return GameDialogs.showTrapFoundDialog(this, trapName);
    }

    /**
     * Affiche un effet de piège activé.
     *
     * @param title Le titre du message
     * @param message Le contenu du message
     * @param beneficial {@code true} si bénéfique pour le joueur
     */
    public void showTrapEffect(String title, String message, boolean beneficial) {
        GameDialogs.showTrapEffect(this, title, message, beneficial);
    }

    /**
     * Affiche le résultat d'un sonar.
     *
     * @param centerX Position X du centre
     * @param centerY Position Y du centre
     * @param occupiedCells Nombre de cases occupées
     * @param isPlayerSonar {@code true} si c'est le sonar du joueur
     */
    public void showSonarResult(int centerX, int centerY, int occupiedCells, boolean isPlayerSonar) {
        GameDialogs.showSonarResult(this, centerX, centerY, occupiedCells, isPlayerSonar, _gridSize);
    }

    /**
     * Met à jour l'affichage de l'inventaire.
     *
     * @param inventory Map des pièges et leurs quantités
     */
    public void updateInventoryDisplay(Map<TrapType, Integer> inventory) {
        _pnlInventory.updateInventory(inventory);
    }

    /**
     * Active ou désactive le mode placement de piège.
     *
     * @param placing true pour activer le mode placement
     */
    public void setPlacingTrapMode(boolean placing) {
        _pnlInventory.setPlacingMode(placing);
    }

    /**
     * Change la couleur d'une cellule de la grille du joueur.
     *
     * @param x Position X
     * @param y Position Y
     * @param color La nouvelle couleur
     */
    public void colorPlayerGridCell(int x, int y, Color color) {
        _pnlPlayerGrid.setCellColor(x, y, color);
    }

    /**
     * Récupère la couleur des pièges.
     *
     * @return La couleur des pièges
     */
    public Color getTrapColor() {
        return TRAP_COLOR;
    }

    /**
     * Récupère le joueur depuis le contrôleur.
     *
     * @return Le joueur humain
     */
    public Player getPlayer() {
        return _controller.getPlayer();
    }

    /**
     * Crée le menu popup (hamburger).
     */
    private void createMenuPopup() {
        _menuPopup = new JPopupMenu();

        _menuItemLegend = new JMenuItem("Légendes");
        _menuItemLegend.addActionListener(e -> showLegend());

        _menuItemQuit = new JMenuItem("Quitter");
        _menuItemQuit.addActionListener(e -> _controller.quit());

        _menuPopup.add(_menuItemLegend);
        _menuPopup.addSeparator();
        _menuPopup.add(_menuItemQuit);
    }

    /**
     * Affiche le menu popup.
     *
     * @param component Le composant déclencheur (bouton menu)
     */
    private void showMenu(Component component) {
        _menuPopup.show(component, 0, component.getHeight());
    }

    /**
     * Affiche la légende des couleurs et abréviations.
     */
    private void showLegend() {
        GameDialogs.showLegend(
                this,
                WATER_COLOR,
                BOAT_COLOR,
                HIT_COLOR,
                SUNK_COLOR,
                MISS_COLOR,
                ISLAND_COLOR,
                ISLAND_SEARCHED_EMPTY,
                ISLAND_SEARCHED_FOUND,
                TRAP_COLOR
        );
    }

    // ==================== Méthodes pour le contrôleur ====================

    /**
     * Définit le numéro du tour actuel.
     *
     * @param turn Le numéro du tour
     */
    public void setTurnNumber(int turn) {
        _lblTurn.setText("Tour " + turn);
    }

    /**
     * Met à jour les statistiques du joueur.
     *
     * @param intact Bateaux intacts
     * @param touched Bateaux touchés
     * @param sunk Bateaux coulés
     * @param missed Tirs dans l'eau
     * @param hitCells Cases touchées
     * @param totalBoatCells Total de cases de bateaux
     */
    public void updatePlayerStats(int intact, int touched, int sunk, int missed, int hitCells, int totalBoatCells) {
        _pnlPlayerStats.updateBoatStats(intact, touched, sunk);
        _pnlPlayerStats.updateShotStats(missed, hitCells, totalBoatCells);
    }

    /**
     * Met à jour les statistiques du robot.
     *
     * @param intact Bateaux intacts
     * @param touched Bateaux touchés
     * @param sunk Bateaux coulés
     * @param missed Tirs dans l'eau
     * @param hitCells Cases touchées
     * @param totalBoatCells Total de cases de bateaux
     */
    public void updateRobotStats(int intact, int touched, int sunk, int missed, int hitCells, int totalBoatCells) {
        _pnlRobotStats.updateBoatStats(intact, touched, sunk);
        _pnlRobotStats.updateShotStats(missed, hitCells, totalBoatCells);
    }

    /**
     * Met à jour les armes disponibles du joueur.
     *
     * @param missiles Nombre de missiles (infini, non utilisé)
     * @param bombs Nombre de bombes
     * @param sonars Nombre de sonars
     */
    public void updatePlayerWeapons(int missiles, int bombs, int sonars) {
        _pnlPlayerStats.updateWeapons(bombs, sonars);
    }

    /**
     * Met à jour les armes disponibles du robot.
     *
     * @param missiles Nombre de missiles (infini, non utilisé)
     * @param bombs Nombre de bombes
     * @param sonars Nombre de sonars
     */
    public void updateRobotWeapons(int missiles, int bombs, int sonars) {
        _pnlRobotStats.updateWeapons(bombs, sonars);
    }

    /**
     * Met à jour les cases d'île restantes du joueur.
     *
     * @param remaining Nombre de cases restantes
     */
    public void updatePlayerIsland(int remaining) {
        _pnlPlayerStats.updateIsland(remaining);
    }

    /**
     * Met à jour les cases d'île restantes du robot.
     *
     * @param remaining Nombre de cases restantes
     */
    public void updateRobotIsland(int remaining) {
        _pnlRobotStats.updateIsland(remaining);
    }

    /**
     * Définit le texte de la dernière action du joueur.
     *
     * @param action Le texte de l'action
     */
    public void setPlayerAction(String action) {
        _pnlActionHistory.setPlayerAction(action);
    }

    /**
     * Définit le texte de la dernière action du robot.
     *
     * @param action Le texte de l'action
     */
    public void setRobotAction(String action) {
        _pnlActionHistory.setRobotAction(action);
    }

    /**
     * Ajoute une ligne à l'historique complet.
     *
     * @param history Le texte à ajouter
     */
    public void appendHistory(String history) {
        _pnlActionHistory.appendHistory(history);
    }

    /**
     * Efface tout l'historique.
     */
    public void clearHistory() {
        _pnlActionHistory.clearHistory();
    }

    /**
     * Vérifie si la pelle (fouiller l'île) est sélectionnée.
     *
     * @return true si la pelle est sélectionnée
     */
    public boolean isShovelSelected() {
        return _pnlWeaponSelection.isShovelSelected();
    }

    /**
     * Retourne l'arme actuellement sélectionnée.
     *
     * @return Le type d'arme sélectionné
     */
    public WeaponType getSelectedWeapon() {
        return _pnlWeaponSelection.getSelectedWeapon();
    }

    /**
     * Active ou désactive une arme spécifique.
     *
     * @param weapon Le type d'arme
     * @param enabled true pour activer, false pour désactiver
     */
    public void setWeaponEnabled(WeaponType weapon, boolean enabled) {
        _pnlWeaponSelection.setWeaponEnabled(weapon, enabled);
    }

    /**
     * Affiche un message d'erreur.
     *
     * @param message Le message d'erreur
     */
    public void showError(String message) {
        GameDialogs.showError(this, message);
    }

    /**
     * Affiche un message de succès.
     *
     * @param message Le message de succès
     */
    public void showSuccess(String message) {
        GameDialogs.showSuccess(this, message);
    }

    // ==================== Méthodes de l'observer ====================

    @Override
    public void boatAttacked(Position position, boolean robot) {
        GameGridPanel grid = robot ? _pnlRobotGrid : _pnlPlayerGrid;
        Color currentColor = grid.getCellColor(position.getX(), position.getY());

        // Ne pas écraser la couleur "coulé"
        if (currentColor != SUNK_COLOR) {
            grid.setCellColor(position.getX(), position.getY(), HIT_COLOR);
        }
    }

    @Override
    public void boatSunk(Position position, int size, boolean robot) {
        GameGridPanel grid = robot ? _pnlRobotGrid : _pnlPlayerGrid;

        // Colorier toutes les cases du bateau coulé
        switch (position.getOrientation()) {
            case VERTICAL:
                for (int i = 0; i < size; i++) {
                    grid.setCellColor(position.getX(), position.getY() + i, SUNK_COLOR);
                }
                break;
            case HORIZONTAL:
                for (int i = 0; i < size; i++) {
                    grid.setCellColor(position.getX() + i, position.getY(), SUNK_COLOR);
                }
                break;
            default:
                grid.setCellColor(position.getX(), position.getY(), SUNK_COLOR);
        }
    }

    @Override
    public void boatTouched(boolean robot) {
        // Méthode vide - les stats sont mises à jour par le contrôleur
    }

    @Override
    public void squareAttacked(Position position, boolean robot) {
        GameGridPanel grid = robot ? _pnlRobotGrid : _pnlPlayerGrid;
        Color currentColor = grid.getCellColor(position.getX(), position.getY());

        // Ne colorier en "manqué" que si ce n'est pas déjà touché ou coulé
        if (currentColor != HIT_COLOR && currentColor != SUNK_COLOR) {
            grid.setCellColor(position.getX(), position.getY(), MISS_COLOR);
        }
    }

    @Override
    public void squareIsland(Position position, State state, boolean robot) {
        GameGridPanel grid = robot ? _pnlRobotGrid : _pnlPlayerGrid;
        PlayerStatsPanel stats = robot ? _pnlRobotStats : _pnlPlayerStats;

        // Changer la couleur selon l'état
        switch (state) {
            case EMPTY:
                grid.setCellColor(position.getX(), position.getY(), ISLAND_SEARCHED_EMPTY);
                break;
            case SEARCHED:
                grid.setCellColor(position.getX(), position.getY(), ISLAND_SEARCHED_FOUND);
                break;
            default:
                grid.setCellColor(position.getX(), position.getY(), ISLAND_COLOR);
        }

        // Mettre à jour le compteur d'île restante
        JLabel islandLabel = stats.getIslandLabel();
        String text = islandLabel.getText(); // Format: "Île restante : 16"
        int remaining = Integer.parseInt(text.substring(15)) - 1;
        stats.updateIsland(remaining);
    }
}

