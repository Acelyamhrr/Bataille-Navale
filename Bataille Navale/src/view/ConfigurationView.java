package view;

import controller.ConfigurationController;
import view.dialogs.BoatCustomizationDialog;
import view.panels.TrapPlacementPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Vue de configuration de la partie de bataille navale.
 * Cette fenêtre permet au joueur de configurer tous les paramètres d'une nouvelle partie.
 */
public class ConfigurationView extends JFrame {

    /** Champ de saisie du nom du joueur */
    private JTextField _txtUsername;

    /** Liste déroulante pour sélectionner la taille de la grille */
    private JComboBox<String> _cmbGridSize;

    /** Bouton radio pour le mode de jeu standard */
    private JRadioButton _rdbModeStandard;

    /** Bouton radio pour le mode de jeu île */
    private JRadioButton _rdbModeIsland;

    /** Bouton radio pour le robot avec difficulté aléatoire */
    private JRadioButton _rdbRobotRandom;

    /** Bouton radio pour le robot avec difficulté intelligente */
    private JRadioButton _rdbRobotSmart;

    /** Bouton radio pour utiliser le nombre de bateaux par défaut */
    private JRadioButton _rdbBoatDefault;

    /** Bouton radio pour personnaliser le nombre de bateaux */
    private JRadioButton _rdbBoatCustom;

    /** Bouton pour retourner à l'écran précédent */
    private JButton _btnBack;

    /** Bouton pour passer à l'écran suivant */
    private JButton _btnNext;

    /** Bouton pour ouvrir la boîte de dialogue de personnalisation des bateaux */
    private JButton _btnCustomizeBoat;

    /** Label affichant le nombre total de cases utilisées par les bateaux */
    private JLabel _lblBoatInfo;

    /** Panel contenant les options de placement des pièges */
    private TrapPlacementPanel _pnlTrapPlacement;

    /** Controlleur */
    private ConfigurationController controller;

    /**
     * Tableau stockant le nombre de bateaux personnalisés pour chaque type.
     * Ordre : [Porte-avions, Croiseur, Contre-torpilleur, Sous-marin, Torpilleur]
     * Valeur par défaut : 1 bateau de chaque type
     */
    private int[] _customBoatNumbers = {1, 1, 1, 1, 1};

    /**
     * Construit une nouvelle vue de configuration.
     * <p>
     * Initialise la fenêtre avec une taille de 650x700 pixels, centrée à l'écran,
     * et crée tous les composants d'interface nécessaires.
     */
    public ConfigurationView(ConfigurationController controller) {
        this.controller = controller;
        setTitle("Configuration de la partie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 700);
        setLocationRelativeTo(null);
        initComponents();

    }

    /**
     * Initialise tous les composants graphiques de la fenêtre.
     */
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 245, 250));

        // Titre principal
        JLabel titleLabel = new JLabel("Configuration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(30, 50, 100));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Panel principal de configuration avec disposition verticale
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
        configPanel.setBackground(Color.WHITE);
        configPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Section : Nom du joueur
        configPanel.add(createSection("Nom du joueur"));
        _txtUsername = new JTextField("Joueur");       // Valeur par défaut
        _txtUsername.setMaximumSize(new Dimension(300, 30));
        configPanel.add(_txtUsername);
        configPanel.add(Box.createVerticalStrut(20));

        // Section : Taille de la grille
        configPanel.add(createSection("Taille de la grille"));
        String[] sizes = {"6x6", "7x7", "8x8", "9x9", "10x10"};
        _cmbGridSize = new JComboBox<>(sizes);
        _cmbGridSize.setSelectedIndex(0); // 6x6 par défaut
        _cmbGridSize.setMaximumSize(new Dimension(150, 30));
        configPanel.add(_cmbGridSize);
        configPanel.add(Box.createVerticalStrut(20));

        // Section : Mode de jeu
        configPanel.add(createSection("Mode de jeu"));
        ButtonGroup modeGroup = new ButtonGroup();
        _rdbModeStandard = new JRadioButton("Standard", true); // Sélectionné par défaut
        _rdbModeIsland = new JRadioButton("Île");

        modeGroup.add(_rdbModeStandard);
        modeGroup.add(_rdbModeIsland);

        // Mise à jour du panel de pièges selon le mode sélectionné
        _rdbModeStandard.addActionListener(e -> _pnlTrapPlacement.updateForMode(false));
        _rdbModeIsland.addActionListener(e -> _pnlTrapPlacement.updateForMode(true));

        configPanel.add(_rdbModeStandard);
        configPanel.add(_rdbModeIsland);
        configPanel.add(Box.createVerticalStrut(20));

        // Section : Difficulté du robot
        configPanel.add(createSection("Difficulté du robot"));
        ButtonGroup robotGroup = new ButtonGroup();
        _rdbRobotRandom = new JRadioButton("Aléatoire", true);     // Par défaut
        _rdbRobotSmart = new JRadioButton("Intelligent");

        robotGroup.add(_rdbRobotRandom);
        robotGroup.add(_rdbRobotSmart);
        configPanel.add(_rdbRobotRandom);
        configPanel.add(_rdbRobotSmart);
        configPanel.add(Box.createVerticalStrut(20));

        // Section : Nombre de bateaux
        configPanel.add(createSection("Nombre de bateaux"));
        ButtonGroup boatGroup = new ButtonGroup();
        _rdbBoatDefault = new JRadioButton("1 bateau de chaque type", true);
        _rdbBoatCustom = new JRadioButton("Personnalisé");

        boatGroup.add(_rdbBoatDefault);
        boatGroup.add(_rdbBoatCustom);
        configPanel.add(_rdbBoatDefault);
        configPanel.add(_rdbBoatCustom);
        configPanel.add(Box.createVerticalStrut(10));

        // Bouton de personnalisation des bateaux (désactivé par défaut)
        _btnCustomizeBoat = new JButton("Personnaliser les bateaux");
        _btnCustomizeBoat.setEnabled(false);

        // Active le bouton quand le mode personnalisé est sélectionné
        _rdbBoatCustom.addActionListener(e -> _btnCustomizeBoat.setEnabled(true));

        // Réinitialise à la configuration par défaut
        _rdbBoatDefault.addActionListener(e -> {
            _btnCustomizeBoat.setEnabled(false);
            _customBoatNumbers = new int[]{1, 1, 1, 1, 1};
            updateBoatInfo();
        });

        // Ouvre la boîte de dialogue de personnalisation
        _btnCustomizeBoat.addActionListener(e -> {
            BoatCustomizationDialog dialog = new BoatCustomizationDialog(this, _customBoatNumbers, controller);
            int[] result = dialog.showAndGetResult();
            if (result != null) {
                _customBoatNumbers = result;
                updateBoatInfo();
            }
        });

        configPanel.add(_btnCustomizeBoat);
        configPanel.add(Box.createVerticalStrut(10));

        // Label affichant le total de cases utilisées
        _lblBoatInfo = new JLabel("Total: 17 cases utilisées");
        _lblBoatInfo.setFont(new Font("Arial", Font.ITALIC, 12));
        configPanel.add(_lblBoatInfo);
        configPanel.add(Box.createVerticalStrut(20));

        // Section : Placement des pièges
        _pnlTrapPlacement = new TrapPlacementPanel();
        _pnlTrapPlacement.updateForMode(_rdbModeIsland.isSelected());
        configPanel.add(_pnlTrapPlacement);

        // Panel scrollable pour le contenu
        JScrollPane scrollPane = new JScrollPane(configPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Vitesse de scroll

        // Panel des boutons de navigation
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(240, 245, 250));

        _btnBack = createButton("Retour", new Color(150, 150, 150));
        _btnNext = createButton("Suivant", new Color(70, 150, 70));

        buttonPanel.add(_btnBack);
        buttonPanel.add(_btnNext);

        // Assemblage final de la fenêtre
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Crée un label pour les titres de section
     * @param title Le texte du titre de section
     * @return Un {@code JLabel} stylisé pour une section
     */
    private JLabel createSection(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(30, 50, 100));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /**
     * Crée un bouton stylisé avec une couleur de fond personnalisée.
     * @param text Le texte à afficher sur le bouton
     * @param bgColor La couleur de fond du bouton
     * @return Un {@code JButton} stylisé
     */
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Met à jour le label affichant le nombre total de cases utilisées par les bateaux.
     */
    private void updateBoatInfo() {
        int[] sizes = {5, 4, 3, 3, 2}; // Tailles des bateaux
        int total = 0;
        for (int i = 0; i < 5; i++) {
            total += _customBoatNumbers[i] * sizes[i];
        }
        _lblBoatInfo.setText("Total: " + total + " cases utilisées");
    }

    // Méthodes pour le contrôleur

    /**
     * Ajoute un listener au bouton "Retour".
     * @param listener L'écouteur d'événements à ajouter
     */
    public void addBackListener(ActionListener listener) {
        _btnBack.addActionListener(listener);
    }

    /**
     * Ajoute un listener au bouton "Suivant".
     * @param listener L'écouteur d'événements à ajouter
     */
    public void addNextListener(ActionListener listener) {
        _btnNext.addActionListener(listener);
    }

    /**
     * Ajoute un listener au bouton de personnalisation des bateaux.
     * @param listener L'écouteur d'événements à ajouter
     */
    public void addCustomizeBoatListener(ActionListener listener) {
        _btnCustomizeBoat.addActionListener(listener);
    }

    // Getters pour récupérer les valeurs

    /**
     * Récupère le nom du joueur saisi.
     * @return Le nom du joueur (sans espaces superflus)
     */
    public String getUsername() {
        return _txtUsername.getText().trim();
    }

    /**
     * Récupère la taille de grille sélectionnée.
     * @return La taille de la grille (entre 6 et 10)
     */
    public int getGridSize() {
        return _cmbGridSize.getSelectedIndex() + 6;
    }

    /**
     * Vérifie si le mode standard est sélectionné.
     *
     * @return {@code true} si le mode standard est sélectionné, {@code false} pour le mode île
     */
    public boolean isStandardMode() {
        return _rdbModeStandard.isSelected();
    }

    /**
     * Vérifie si le robot aléatoire est sélectionné.
     *
     * @return {@code true} si le robot aléatoire est sélectionné, {@code false} pour le robot intelligent
     */
    public boolean isRandomRobot() {
        return _rdbRobotRandom.isSelected();
    }

    /**
     * Vérifie si la configuration par défaut des bateaux est sélectionnée.
     *
     * @return {@code true} si la configuration par défaut est sélectionnée, {@code false} pour personnalisée
     */
    public boolean isDefaultBoats() {
        return _rdbBoatDefault.isSelected();
    }

    /**
     * Récupère le tableau des nombres de bateaux personnalisés.
     *
     * @return Un clone du tableau contenant le nombre de chaque type de bateau
     */
    public int[] getCustomBoatNumbers() {
        return _customBoatNumbers.clone();
    }

    /**
     * Récupère le mode de placement des pièges sélectionné.
     *
     * @return Le mode de placement sélectionné sous forme de chaîne
     */
    public String getTrapPlacementMode() {
        return _pnlTrapPlacement.getSelectedMode();
    }

    //  Méthodes d'affichage de messages

    /**
     * Affiche un message d'erreur dans une boîte de dialogue modale.
     *
     * @param message Le message d'erreur à afficher
     */
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Affiche un message de succès dans une boîte de dialogue modale.
     *
     * @param message Le message de succès à afficher
     */
    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }
}