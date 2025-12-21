package view.panels;

import controller.GameController;
import model.enums.TrapType;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Map;

/**
 * Panel affichant l'inventaire des pièges du joueur.
 * Permet de placer des pièges trouvés sur l'île pendant la partie.
 */
public class InventoryPanel extends JPanel {

    /** Label affichant le nombre de Trous Noirs en inventaire */
    private JLabel _lblBlackholeCount;

    /** Label affichant le nombre de Tornades en inventaire */
    private JLabel _lblTornadoCount;

    /** Bouton pour placer un Trou Noir depuis l'inventaire */
    private JButton _btnPlaceBlackhole;

    /** Bouton pour placer une Tornade depuis l'inventaire */
    private JButton _btnPlaceTornado;

    /** Bouton pour annuler le placement en cours */
    private JButton _btnCancelPlacement;

    /** Référence au contrôleur de jeu pour les actions */
    private GameController _controller;

    /**
     * Constructeur du panel d'inventaire.
     *
     * @param controller Le contrôleur de jeu pour gérer les actions
     */
    public InventoryPanel(GameController controller) {
        this._controller = controller;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                "Inventaire",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)
        ));
        setMaximumSize(new Dimension(250, 150));

        initComponents();
        attachActions();
    }

    /**
     * Initialise tous les composants graphiques du panel.
     */
    private void initComponents() {
        // Labels pour afficher le nombre de pièges
        _lblBlackholeCount = new JLabel("Trou Noir: 0");
        _lblBlackholeCount.setFont(new Font("Arial", Font.PLAIN, 11));
        _lblBlackholeCount.setAlignmentX(Component.LEFT_ALIGNMENT);

        _lblTornadoCount = new JLabel("Tornade: 0");
        _lblTornadoCount.setFont(new Font("Arial", Font.PLAIN, 11));
        _lblTornadoCount.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Boutons pour placer les pièges
        _btnPlaceBlackhole = new JButton("Placer");
        _btnPlaceBlackhole.setFont(new Font("Arial", Font.PLAIN, 10));
        _btnPlaceBlackhole.setEnabled(false);
        _btnPlaceBlackhole.setMaximumSize(new Dimension(80, 25));
        _btnPlaceBlackhole.setAlignmentX(Component.LEFT_ALIGNMENT);

        _btnPlaceTornado = new JButton("Placer");
        _btnPlaceTornado.setFont(new Font("Arial", Font.PLAIN, 10));
        _btnPlaceTornado.setEnabled(false);
        _btnPlaceTornado.setMaximumSize(new Dimension(80, 25));
        _btnPlaceTornado.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Bouton annuler (caché par défaut)
        _btnCancelPlacement = new JButton("Annuler placement");
        _btnCancelPlacement.setFont(new Font("Arial", Font.PLAIN, 10));
        _btnCancelPlacement.setVisible(false);
        _btnCancelPlacement.setMaximumSize(new Dimension(150, 25));
        _btnCancelPlacement.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Panneau pour Trou Noir
        JPanel blackholePanel = new JPanel();
        blackholePanel.setLayout(new BoxLayout(blackholePanel, BoxLayout.X_AXIS));
        blackholePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        blackholePanel.add(_lblBlackholeCount);
        blackholePanel.add(Box.createHorizontalStrut(10));
        blackholePanel.add(_btnPlaceBlackhole);

        // Panneau pour Tornade
        JPanel tornadoPanel = new JPanel();
        tornadoPanel.setLayout(new BoxLayout(tornadoPanel, BoxLayout.X_AXIS));
        tornadoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        tornadoPanel.add(_lblTornadoCount);
        tornadoPanel.add(Box.createHorizontalStrut(10));
        tornadoPanel.add(_btnPlaceTornado);

        // Ajout au panel principal
        add(Box.createVerticalStrut(5));
        add(blackholePanel);
        add(Box.createVerticalStrut(5));
        add(tornadoPanel);
        add(Box.createVerticalStrut(10));
        add(_btnCancelPlacement);
        add(Box.createVerticalStrut(5));
    }

    /**
     * Attache les actions du contrôleur aux boutons.
     */
    private void attachActions() {
        _btnPlaceBlackhole.addActionListener(e ->
                _controller.startPlacingTrapFromInventory(TrapType.BLACKHOLE));
        _btnPlaceTornado.addActionListener(e ->
                _controller.startPlacingTrapFromInventory(TrapType.TORNADO));
        _btnCancelPlacement.addActionListener(e ->
                _controller.cancelTrapPlacement());
    }

    /**
     * Met à jour l'affichage de l'inventaire avec les quantités actuelles.
     *
     * @param inventory Map contenant les types de pièges et leurs quantités
     */
    public void updateInventory(Map<TrapType, Integer> inventory) {
        int blackholeCount = inventory.getOrDefault(TrapType.BLACKHOLE, 0);
        int tornadoCount = inventory.getOrDefault(TrapType.TORNADO, 0);

        _lblBlackholeCount.setText("Trou Noir: " + blackholeCount);
        _lblTornadoCount.setText("Tornade: " + tornadoCount);

        _btnPlaceBlackhole.setEnabled(blackholeCount > 0);
        _btnPlaceTornado.setEnabled(tornadoCount > 0);
    }

    /**
     * Active ou désactive le mode placement de piège.
     * En mode placement, les boutons de sélection sont désactivés et le bouton
     * d'annulation devient visible.
     *
     * @param placing {@code true} pour activer le mode placement, {@code false} sinon
     */
    public void setPlacingMode(boolean placing) {
        _btnCancelPlacement.setVisible(placing);

        if (placing) {
            // Désactiver les autres boutons pendant le placement
            _btnPlaceBlackhole.setEnabled(false);
            _btnPlaceTornado.setEnabled(false);
        }
        // Sinon, le contrôleur appellera updateInventory() pour réactiver selon stock
    }
}