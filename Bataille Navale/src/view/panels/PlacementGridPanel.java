package view.panels;

import controller.PlacementController;
import static view.utils.GameColors.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Panel affichant la grille de placement des bateaux/pièges/armes.
 * Gère l'affichage visuel et la détection du hover de la souris.
 */
public class PlacementGridPanel extends JPanel {

    /** Taille de la grille (6 à 10) */
    private int _gridSize;

    /** Tableau de boutons représentant la grille */
    private JButton[][] _btnGrid;

    /** Référence au contrôleur pour les actions */
    private PlacementController _controller;

    /** Position X de la souris (hover), -1 si aucune case survolée */
    private int _hoverX = -1;

    /** Position Y de la souris (hover), -1 si aucune case survolée */
    private int _hoverY = -1;

    /**
     * Constructeur du panel de grille.
     * Initialise la grille avec la taille spécifiée et lie le contrôleur.
     */
    public PlacementGridPanel(int gridSize, PlacementController controller) {
        this._gridSize = gridSize;
        this._controller = controller;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(100, 120, 140), 2));

        initGrid();
    }

    /**
     * Initialise la grille de boutons.
     * Crée un bouton pour chaque case, applique la couleur de base (eau ou île)
     * et configure les listeners de clic et de survol.
     */
    private void initGrid() {
        JPanel grid = new JPanel(new GridLayout(_gridSize, _gridSize, 2, 2));
        grid.setBackground(new Color(200, 220, 240));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        _btnGrid = new JButton[_gridSize][_gridSize];

        for (int y = 0; y < _gridSize; y++) {
            for (int x = 0; x < _gridSize; x++) {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(45, 45));

                // Couleur de base selon le terrain (île ou eau)
                if (_controller.squareInIsland(x, y)) {
                    btn.setBackground(ISLAND_COLOR);
                } else {
                    btn.setBackground(WATER_COLOR);
                }

                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

                // Variables finales pour les lambdas
                final int fx = x, fy = y;

                // Listener de clic : notifie le contrôleur
                btn.addActionListener(e -> _controller.onGridClick(fx, fy));

                // Listeners de survol pour l'aperçu visuel
                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        _hoverX = fx;
                        _hoverY = fy;
                        _controller.updateGrid(); // Mise à jour de l'aperçu
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        _hoverX = -1;
                        _hoverY = -1;
                        _controller.updateGrid(); // Suppression de l'aperçu
                    }
                });

                _btnGrid[y][x] = btn;
                grid.add(btn);
            }
        }

        add(grid, BorderLayout.CENTER);
    }

    /**
     * Change la couleur d'une cellule.
     * Utilisé pour afficher les bateaux, pièges, aperçus et états de jeu.
     * Si les coordonnées sont hors limites, l'opération est ignorée.
     *
     * @param x Position X (colonne)
     * @param y Position Y (ligne)
     * @param color Couleur à appliquer
     */
    public void setCellColor(int x, int y, Color color) {
        if (x >= 0 && x < _gridSize && y >= 0 && y < _gridSize) {
            _btnGrid[y][x].setBackground(color);
        }
    }

    /**
     * Change le texte d'une cellule.
     * Utilisé pour afficher des symboles (pièges, armes, marqueurs).
     * Si les coordonnées sont hors limites, l'opération est ignorée.
     *
     * @param x Position X (colonne)
     * @param y Position Y (ligne)
     * @param text Texte à afficher (peut être vide)
     */
    public void setCellText(int x, int y, String text) {
        if (x >= 0 && x < _gridSize && y >= 0 && y < _gridSize) {
            _btnGrid[y][x].setText(text);
        }
    }

    /**
     * Réinitialise toutes les cellules à leur couleur de base.
     * Restaure la couleur d'origine (eau ou île) et efface tout texte.
     * Appelée lors d'un recommencement ou d'un changement de configuration.
     */
    public void resetAllCells() {
        for (int y = 0; y < _gridSize; y++) {
            for (int x = 0; x < _gridSize; x++) {
                // Restauration de la couleur selon le terrain
                if (_controller.squareInIsland(x, y)) {
                    setCellColor(x, y, ISLAND_COLOR);
                } else {
                    setCellColor(x, y, WATER_COLOR);
                }
                // Effacement du texte
                setCellText(x, y, "");
            }
        }
    }

    /**
     * Récupère la coordonnée X de la case survolée.
     *
     * @return Position X survolée, ou -1 si aucune
     */
    public int getHoverX() {
        return _hoverX;
    }

    /**
     * Récupère la coordonnée Y de la case survolée.
     *
     * @return Position Y survolée, ou -1 si aucune
     */
    public int getHoverY() {
        return _hoverY;
    }

    /**
     * Récupère la taille de la grille.
     *
     * @return Nombre de cases par côté
     */
    public int getGridSize() {
        return _gridSize;
    }
}