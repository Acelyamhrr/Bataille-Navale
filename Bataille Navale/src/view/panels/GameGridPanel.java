package view.panels;

import controller.GameController;
import model.enums.TrapType;
import model.enums.WeaponType;
import model.game.GamePlacement;
import model.grid.Grid;
import model.grid.Position;

import static view.utils.GameColors.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Panel affichant une grille de jeu (joueur ou robot).
 * Réutilisable pour les deux grilles du jeu.
 */
public class GameGridPanel extends JPanel {

    /** Taille de la grille (6 à 10) */
    private int _gridSize;

    /** Tableau de boutons représentant les cases de la grille */
    private JButton[][] _gridButtons;

    /** Indique si c'est la grille du joueur (true) ou du robot (false) */
    private boolean _isPlayerGrid;

    /** Référence au contrôleur de jeu */
    private GameController _controller;

    /**
     * Constructeur du panel de grille.
     *
     * @param gridSize La taille de la grille
     * @param isPlayerGrid {@code true} pour la grille du joueur, {@code false} pour le robot
     * @param controller Le contrôleur de jeu
     * @param placement Le placement initial (bateaux, pièges, armes)
     */
    public GameGridPanel(int gridSize, boolean isPlayerGrid,
                         GameController controller, GamePlacement placement) {
        this._gridSize = gridSize;
        this._isPlayerGrid = isPlayerGrid;
        this._controller = controller;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
                isPlayerGrid ? "Votre grille" : "Grille adverse",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
        ));

        initGrid(placement);
    }

    /**
     * Initialise la grille de boutons avec le placement initial.
     *
     * @param placement Le placement initial des éléments
     */
    private void initGrid(GamePlacement placement) {
        JPanel grid = new JPanel(new GridLayout(_gridSize, _gridSize, 1, 1));
        grid.setBackground(Color.DARK_GRAY);

        _gridButtons = new JButton[_gridSize][_gridSize];

        for (int y = 0; y < _gridSize; y++) {
            for (int x = 0; x < _gridSize; x++) {
                JButton btn = new JButton();

                // Couleur de base (île ou eau)
                if (_controller.isInIsland(x, y)) {
                    btn.setBackground(ISLAND_COLOR);
                } else {
                    btn.setBackground(WATER_COLOR);
                }

                btn.setPreferredSize(new Dimension(40, 40));
                btn.setFocusPainted(false);
                btn.setBorderPainted(true);

                final int finalX = x;
                final int finalY = y;

                // Attacher l'action au clic
                btn.addActionListener(e ->
                        _controller.handleGridClick(finalX, finalY, _isPlayerGrid));

                _gridButtons[y][x] = btn;
                grid.add(btn);
            }
        }

        // Si c'est la grille du joueur, afficher le placement initial
        if (_isPlayerGrid) {
            displayInitialPlacement(placement);
        }

        add(grid, BorderLayout.CENTER);
    }

    /**
     * Affiche le placement initial sur la grille du joueur.
     *
     * @param placement Le placement à afficher
     */
    private void displayInitialPlacement(GamePlacement placement) {
        Grid playerGrid = placement.getPlayerGrid();

        // Afficher les bateaux
        for (Position pos : playerGrid.getPositionsBoats()) {
            _gridButtons[pos.getY()][pos.getX()].setBackground(BOAT_COLOR);
        }

        // Afficher les pièges
        for (Map.Entry<TrapType, List<Position>> entry : playerGrid.getPositionsTraps().entrySet()) {
            String text = (entry.getKey() == TrapType.BLACKHOLE) ? "N" : "O";
            for (Position pos : entry.getValue()) {
                _gridButtons[pos.getY()][pos.getX()].setBackground(TRAP_COLOR);
                _gridButtons[pos.getY()][pos.getX()].setText(text);
            }
        }

        // Afficher les armes
        for (Map.Entry<WeaponType, List<Position>> entry : playerGrid.getPositionsWeapons().entrySet()) {
            String text = (entry.getKey() == WeaponType.BOMB) ? "B" : "S";
            for (Position pos : entry.getValue()) {
                _gridButtons[pos.getY()][pos.getX()].setBackground(WEAPON_COLOR);
                _gridButtons[pos.getY()][pos.getX()].setText(text);
            }
        }
    }

    /**
     * Change la couleur d'une cellule.
     *
     * @param x Position X
     * @param y Position Y
     * @param color La nouvelle couleur
     */
    public void setCellColor(int x, int y, Color color) {
        if (x >= 0 && x < _gridSize && y >= 0 && y < _gridSize) {
            _gridButtons[y][x].setBackground(color);
        }
    }

    /**
     * Change le texte d'une cellule.
     *
     * @param x Position X
     * @param y Position Y
     * @param text Le nouveau texte
     */
    public void setCellText(int x, int y, String text) {
        if (x >= 0 && x < _gridSize && y >= 0 && y < _gridSize) {
            _gridButtons[y][x].setText(text);
        }
    }

    /**
     * Récupère la couleur actuelle d'une cellule.
     *
     * @param x Position X
     * @param y Position Y
     * @return La couleur de la cellule
     */
    public Color getCellColor(int x, int y) {
        if (x >= 0 && x < _gridSize && y >= 0 && y < _gridSize) {
            return _gridButtons[y][x].getBackground();
        }
        return null;
    }

    /**
     * Récupère le tableau de boutons de la grille.
     *
     * @return Le tableau 2D de boutons
     */
    public JButton[][] getGridButtons() {
        return _gridButtons;
    }

    public int getGridSize() {
        return _gridSize;
    }
}