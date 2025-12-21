package view.panels;

import controller.PlacementController;

import javax.swing.*;
import java.awt.*;

/**
 * Panel de contrôle pour le placement des éléments.
 * Contient les sélecteurs de mode, bateaux, pièges/armes, orientation et informations.
 */
public class PlacementControlPanel extends JPanel {

    /** Sélecteur de bateau à placer */
    private JComboBox<String> _cmbBoat;

    /** Sélecteur de piège/arme à placer */
    private JComboBox<String> _cmbTrapWeapon;

    /** Bouton pour changer l'orientation (horizontal/vertical) */
    private JButton _btnOrientation;

    /** Bouton radio pour le mode de placement fixe */
    private JRadioButton _rdbFixed;

    /** Bouton radio pour le mode de placement aléatoire */
    private JRadioButton _rdbRandom;

    /** Bouton radio pour le mode de placement manuel */
    private JRadioButton _rdbManual;

    /** Label affichant les informations et instructions */
    private JLabel _lblInfo;

    /** Indique si l'orientation actuelle est horizontale (true) ou verticale (false) */
    private boolean _isHorizontal = true;

    /** Référence au contrôleur gérant la logique de placement */
    private PlacementController _controller;

    /**
     * Constructeur du panel de contrôle.
     * Initialise tous les composants et les lie au contrôleur.
     *
     * @param controller Contrôleur de placement gérant la logique
     */
    public PlacementControlPanel(PlacementController controller) {
        this._controller = controller;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 120, 140), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        setPreferredSize(new Dimension(300, 0));

        initComponents();
        attachControllerActions();
    }

    /**
     * Initialise tous les composants du panel.
     * Crée les sélecteurs, boutons radio, labels et configure leur disposition.
     */
    private void initComponents() {
        // Titre du panel
        JLabel title = new JLabel("Placement des éléments");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(title);
        add(Box.createVerticalStrut(20));

        // Section : Mode de placement
        JLabel modeLabel = new JLabel("Mode:");
        modeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(modeLabel);

        ButtonGroup modeGroup = new ButtonGroup();
        _rdbFixed = new JRadioButton("Fixe");
        _rdbRandom = new JRadioButton("Aléatoire");
        _rdbManual = new JRadioButton("Manuel", true); // Sélectionné par défaut

        modeGroup.add(_rdbFixed);
        modeGroup.add(_rdbRandom);
        modeGroup.add(_rdbManual);

        // Configuration du style des boutons radio
        _rdbFixed.setBackground(Color.WHITE);
        _rdbRandom.setBackground(Color.WHITE);
        _rdbManual.setBackground(Color.WHITE);
        _rdbFixed.setAlignmentX(Component.CENTER_ALIGNMENT);
        _rdbRandom.setAlignmentX(Component.CENTER_ALIGNMENT);
        _rdbManual.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(_rdbFixed);
        add(_rdbRandom);
        add(_rdbManual);
        add(Box.createVerticalStrut(20));

        // Section : Sélecteur de bateau
        JLabel boatLabel = new JLabel("Bateau à placer:");
        boatLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(boatLabel);

        _cmbBoat = new JComboBox<>();
        _cmbBoat.setMaximumSize(new Dimension(250, 30));
        _cmbBoat.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(_cmbBoat);
        add(Box.createVerticalStrut(15));

        // Section : Sélecteur de piège/arme
        JLabel trapWeaponLabel = new JLabel("Piège/Arme à placer:");
        trapWeaponLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(trapWeaponLabel);

        _cmbTrapWeapon = new JComboBox<>();
        _cmbTrapWeapon.setMaximumSize(new Dimension(250, 30));
        _cmbTrapWeapon.setAlignmentX(Component.CENTER_ALIGNMENT);
        _cmbTrapWeapon.setEnabled(false); // Désactivé par défaut
        add(_cmbTrapWeapon);
        add(Box.createVerticalStrut(15));

        // Section : Orientation
        JLabel orientLabel = new JLabel("Orientation:");
        orientLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(orientLabel);

        _btnOrientation = new JButton("→ Horizontal");
        _btnOrientation.setAlignmentX(Component.CENTER_ALIGNMENT);
        _btnOrientation.addActionListener(e -> toggleOrientation());
        add(_btnOrientation);
        add(Box.createVerticalStrut(20));

        // Label d'information
        _lblInfo = new JLabel("Cliquez sur la grille");
        _lblInfo.setFont(new Font("Arial", Font.ITALIC, 12));
        _lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(_lblInfo);

        add(Box.createVerticalGlue()); // Pousse tout vers le haut
    }

    /**
     * Attache les actions du contrôleur aux boutons radio.
     * Lie chaque mode de placement à sa méthode correspondante dans le contrôleur.
     */
    private void attachControllerActions() {
        _rdbFixed.addActionListener(e -> _controller.applyFixedPlacement());
        _rdbRandom.addActionListener(e -> _controller.applyRandomPlacement());
        _rdbManual.addActionListener(e -> _controller.enableManualPlacement());
    }

    /**
     * Change l'orientation entre horizontal et vertical.
     * Met à jour le texte du bouton et l'état interne.
     */
    private void toggleOrientation() {
        _isHorizontal = !_isHorizontal;
        _btnOrientation.setText(_isHorizontal ? "→ Horizontal" : "↑ Vertical");
    }

    //  Getters pour le contrôleur

    /**
     * Indique si l'orientation actuelle est horizontale.
     *
     * @return true si horizontal, false si vertical
     */
    public boolean isHorizontal() {
        return _isHorizontal;
    }

    /**
     * Récupère l'index du bateau sélectionné.
     *
     * @return Index du bateau sélectionné dans la combo box
     */
    public int getSelectedBoatIndex() {
        return _cmbBoat.getSelectedIndex();
    }

    /**
     * Récupère le mode de placement sélectionné pour les bateaux.
     *
     * @return "Fixed", "Random" ou "Manual" selon le bouton radio sélectionné
     */
    public String getModeBoat() {
        if (_rdbFixed.isSelected()) return "Fixed";
        else if (_rdbRandom.isSelected()) return "Random";
        else return "Manual";
    }

    // ==================== Méthodes de mise à jour ====================

    /**
     * Change le texte du label d'information.
     *
     * @param text Nouveau texte à afficher
     */
    public void setInfoText(String text) {
        _lblInfo.setText(text);
    }

    /**
     * Définit les options disponibles dans le sélecteur de bateaux.
     * Remplace toutes les options existantes.
     *
     * @param options Tableau des noms de bateaux à afficher
     */
    public void setBoatOptions(String[] options) {
        _cmbBoat.removeAllItems();
        for (String opt : options) {
            _cmbBoat.addItem(opt);
        }
    }

    /**
     * Définit les options disponibles dans le sélecteur de pièges/armes.
     * Remplace toutes les options existantes.
     *
     * @param options Tableau des noms de pièges/armes à afficher
     */
    public void setTrapWeaponOptions(String[] options) {
        _cmbTrapWeapon.removeAllItems();
        for (String opt : options) {
            _cmbTrapWeapon.addItem(opt);
        }
    }

    /**
     * Active ou désactive le sélecteur de bateaux.
     *
     * @param enable true pour activer, false pour désactiver
     */
    public void enableBoatSelector(boolean enable) {
        _cmbBoat.setEnabled(enable);
    }

    /**
     * Active ou désactive le sélecteur de pièges/armes.
     *
     * @param enable true pour activer, false pour désactiver
     */
    public void enableTrapWeaponSelector(boolean enable) {
        _cmbTrapWeapon.setEnabled(enable);
    }
}