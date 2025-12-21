package view.panels;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Panel affichant les statistiques d'un joueur (humain ou robot).
 * Réutilisable pour afficher les stats des deux joueurs.
 */
public class PlayerStatsPanel extends JPanel {

    /** Nom du joueur affiché dans le titre du panel */
    private String _playerName;

    /** Indique si ce panel est pour le robot */
    private boolean _isRobot;

    // Labels de statistiques
    /** Label affichant le nombre de bateaux intacts */
    private JLabel _lblBoatsIntact;

    /** Label affichant le nombre de bateaux touchés */
    private JLabel _lblBoatsTouched;

    /** Label affichant le nombre de bateaux coulés */
    private JLabel _lblBoatsSunk;

    /** Label affichant le nombre de tirs dans l'eau */
    private JLabel _lblMissedShots;

    /** Label affichant le ratio de cases de bateaux touchées */
    private JLabel _lblHitRatio;

    /** Label affichant les armes disponibles */
    private JLabel _lblWeapons;

    /** Label affichant les cases d'île restantes à fouiller */
    private JLabel _lblIsland;

    /** Panel d'inventaire (uniquement pour le joueur humain) */
    private InventoryPanel _pnlInventory;

    /**
     * Constructeur du panel de statistiques.
     *
     * @param playerName Le nom du joueur à afficher
     * @param isRobot {@code true} si c'est le panel du robot, {@code false} pour le joueur
     * @param initialBoats Le nombre initial de bateaux
     * @param initialBoatSquares Le nombre total de cases de bateaux
     * @param hasIsland {@code true} si la partie a une île
     * @param inventoryPanel Le panel d'inventaire (null pour le robot)
     */
    public PlayerStatsPanel(String playerName, boolean isRobot, int initialBoats, int initialBoatSquares, boolean hasIsland, InventoryPanel inventoryPanel) {
        this._playerName = playerName;
        this._isRobot = isRobot;
        this._pnlInventory = inventoryPanel;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
                playerName,
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
        ));

        initComponents(initialBoats, initialBoatSquares, hasIsland);
    }

    /**
     * Initialise tous les composants du panel.
     *
     * @param initialBoats Le nombre initial de bateaux
     * @param initialBoatSquares Le nombre total de cases de bateaux
     * @param hasIsland {@code true} si la partie a une île
     */
    private void initComponents(int initialBoats, int initialBoatSquares, boolean hasIsland) {
        // Création des labels de statistiques
        _lblBoatsIntact = createStatsLabel("Bateaux intacts : " + initialBoats);
        _lblBoatsTouched = createStatsLabel("Bateaux touchés : 0");
        _lblBoatsSunk = createStatsLabel("Bateaux coulés : 0");
        _lblMissedShots = createStatsLabel("Tirs dans l'eau : 0");
        _lblHitRatio = createStatsLabel("Cases de bateaux touchées : 0/" + initialBoatSquares);
        _lblWeapons = createStatsLabel("<html>Armes:<br/>- Missile: ∞<br/>- Bombe: 1<br/>- Sonar: 1</html>");

        if (hasIsland) {
            _lblIsland = createStatsLabel("Île restante : 16");
        } else {
            _lblIsland = createStatsLabel("Île restante : -");
        }

        // Ajout des composants
        add(Box.createVerticalStrut(10));
        add(_lblBoatsIntact);
        add(_lblBoatsTouched);
        add(_lblBoatsSunk);
        add(Box.createVerticalStrut(10));
        add(_lblMissedShots);
        add(_lblHitRatio);
        add(Box.createVerticalStrut(10));
        add(_lblWeapons);
        add(Box.createVerticalStrut(10));
        add(_lblIsland);

        // Ajouter l'inventaire uniquement pour le joueur humain
        if (!_isRobot && _pnlInventory != null) {
            add(Box.createVerticalStrut(20));
            add(_pnlInventory);
        }

        add(Box.createVerticalGlue());
    }

    /**
     * Crée un label de statistique avec le style approprié.
     *
     * @param text Le texte à afficher
     * @return Le JLabel stylisé
     */
    private JLabel createStatsLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // Méthodes de mise à jour

    /**
     * Met à jour les statistiques de bateaux.
     *
     * @param intact Nombre de bateaux intacts
     * @param touched Nombre de bateaux touchés
     * @param sunk Nombre de bateaux coulés
     */
    public void updateBoatStats(int intact, int touched, int sunk) {
        _lblBoatsIntact.setText("Bateaux intacts : " + intact);
        _lblBoatsTouched.setText("Bateaux touchés : " + touched);
        _lblBoatsSunk.setText("Bateaux coulés : " + sunk);
    }

    /**
     * Met à jour les statistiques de tirs.
     *
     * @param missed Nombre de tirs dans l'eau
     * @param hitCells Nombre de cases de bateaux touchées
     * @param totalBoatCells Nombre total de cases de bateaux
     */
    public void updateShotStats(int missed, int hitCells, int totalBoatCells) {
        _lblMissedShots.setText("Tirs dans l'eau : " + missed);
        _lblHitRatio.setText("Cases de bateaux touchées : " + hitCells + "/" + totalBoatCells);
    }

    /**
     * Met à jour l'affichage des armes disponibles.
     *
     * @param bombs Nombre de bombes restantes
     * @param sonars Nombre de sonars restants
     */
    public void updateWeapons(int bombs, int sonars) {
        String text = "<html>Armes:<br/>";
        text += "- Missile: ∞<br/>";
        text += "- Bombe: " + bombs + "<br/>";
        text += "- Sonar: " + sonars + "<br/>";
        _lblWeapons.setText(text);
    }

    /**
     * Met à jour le nombre de cases d'île restantes à fouiller.
     *
     * @param remaining Nombre de cases restantes
     */
    public void updateIsland(int remaining) {
        _lblIsland.setText("Île restante : " + remaining);
    }

    // Getters pour accès direct (si nécessaire)

    public JLabel getBoatsIntactLabel() { return _lblBoatsIntact; }
    public JLabel getBoatsTouchedLabel() { return _lblBoatsTouched; }
    public JLabel getBoatsSunkLabel() { return _lblBoatsSunk; }
    public JLabel getMissedShotsLabel() { return _lblMissedShots; }
    public JLabel getHitRatioLabel() { return _lblHitRatio; }
    public JLabel getIslandLabel() { return _lblIsland; }
}