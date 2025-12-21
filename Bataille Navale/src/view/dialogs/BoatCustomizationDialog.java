package view.dialogs;

import controller.ConfigurationController;

import javax.swing.*;
import java.awt.*;

/**
 * JDialog permettant de personnaliser le nombre de bateaux
 * pour chaque type dans une partie.
 * Cette classe offre une interface graphique pour sélectionner entre 1 et 3
 * exemplaires de chaque type de bateau, avec une limite totale de 35 cases.
 */
public class BoatCustomizationDialog extends JDialog {

    /** permet de selectioner le nb de bateaux pour chaque type */
    private JSpinner[] _spinners;

    /** label pour afficher le total de cases occupées par tout les bateaux */
    private JLabel _lblTotal;

    /** tableau contenant le nb de bateaux pour chaque type */
    private int[] _boatNumbers;

    /** indique si l'utilisateur a validé ses choix (true) ou annulé (false) */
    private boolean _validated = false;

    /** le controller : utilisé pour la vérification des nombres de cases */
    private ConfigurationController _controller;

    /**
     * Construit une nouvelle boîte de dialogue de personnalisation des bateaux
     * @param parent La fenêtre parent
     * @param currentBoats Les nombres actuels de bateaux (5 pour le moment)
     * @param controller Le controller de configuration
     */
    public BoatCustomizationDialog(JFrame parent, int[] currentBoats, ConfigurationController controller) {
        super(parent, "Personnalisation des bateaux", true);
        // clone le tab pour éviter de modifier l'original avant la validation
        this._boatNumbers = currentBoats.clone();
        this._controller = controller;
        setSize(500, 400);
        setLocationRelativeTo(parent);
        initComponents();
    }

    /**
     * Initialise tous les composants graphiques du JDialog
     * Crée les spinners pour chaque type de bateau, configure les listeners,
     * et met en place les btn de validation et d'anulation
     */
    private void initComponents() {
        // Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Noms et tailles des différents types de bateaux
        String[] names = {"Porte-avions", "Croiseur", "Contre-torpilleur", "Sous-marin", "Torpilleur"};
        int[] sizes = {5, 4, 3, 3, 2};

        _spinners = new JSpinner[5];
        _lblTotal = new JLabel();

        // Créer une ligne pour chaque type de bateau
        for (int i = 0; i < 5; i++) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));

            // Label avec le nom et la taille du bateau
            JLabel nameLabel = new JLabel(names[i] + " (" + sizes[i] + " cases)");
            nameLabel.setPreferredSize(new Dimension(200, 25));

            // Spinner pour sélectionner le nombre (1 à 3)
            _spinners[i] = new JSpinner(new SpinnerNumberModel(_boatNumbers[i], 1, 3, 1));
            _spinners[i].setPreferredSize(new Dimension(60, 25));

            // Listener pour mettre à jour le total à chaque changement
            _spinners[i].addChangeListener(e -> updateTotal(sizes));

            row.add(nameLabel);
            row.add(_spinners[i]);
            panel.add(row);
        }

        // Espacement avant le label de total
        panel.add(Box.createVerticalStrut(20));

        // Configuration du label affichant le total
        _lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(_lblTotal);
        updateTotal(sizes); // Calcul initial du total

        // Panel des boutons de validation/annulation
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton validateBtn = new JButton("Valider");
        JButton cancelBtn = new JButton("Annuler");

        // Action du bouton Valider
        validateBtn.addActionListener(e -> {
            int total = calculateTotal(sizes);

            // Vérification de la limite de 35 cases
            if (total > 35) {
                JOptionPane.showMessageDialog(this, "Maximum 35 cases ! Actuellement: " + total, "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Sauvegarde des valeurs sélectionnées
            for (int i = 0; i < 5; i++) {
                _boatNumbers[i] = (Integer) _spinners[i].getValue();
            }
            _validated = true;
            dispose(); // Ferme la fenêtre
        });

        // Action du bouton Annuler
        cancelBtn.addActionListener(e -> {
            _validated = false;
            dispose(); // Ferme la fenêtre sans sauvegarder
        });

        buttonPanel.add(validateBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel);

        add(panel);
    }

    /**
     * Met à jour l'affichage du total de cases occupées.
     * Change la couleur du texte en rouge si le total dépasse 35 cases.
     *
     * @param sizes Tableau des tailles de chaque type de bateau
     */
    private void updateTotal(int[] sizes) {
        int total = calculateTotal(sizes);
        boolean valid = _controller.numberSquaresValid(_boatNumbers);
        _lblTotal.setText("Total: " + total + " cases" + (valid ? " (DÉPASSÉ !)" : ""));
        _lblTotal.setForeground(valid ? Color.RED : Color.BLACK);
    }

    /**
     * Calcule le nombre total de cases occupées par tous les bateaux sélectionnés.
     * Formule : somme de (nombre de bateaux × taille du bateau) pour chaque type.
     *
     * @param sizes Tableau des tailles de chaque type de bateau
     * @return Le nombre total de cases occupées
     */
    private int calculateTotal(int[] sizes) {
        int total = 0;
        for (int i = 0; i < _spinners.length; i++) {
            total += (Integer) _spinners[i].getValue() * sizes[i];
        }
        return total;
    }

    /**
     * Affiche la boîte de dialogue et retourne le résultat de la sélection.
     * Cette méthode est bloquante : elle attend que l'utilisateur valide ou annule
     * avant de retourner.
     *
     * @return Un tableau de 5 entiers représentant les nouveaux nombres de bateaux
     *         si l'utilisateur a validé, ou {@code null} s'il a annulé
     */
    public int[] showAndGetResult() {
        setVisible(true); // Affiche la boîte de dialogue (bloquant car modal)
        return _validated ? _boatNumbers : null;
    }

}
