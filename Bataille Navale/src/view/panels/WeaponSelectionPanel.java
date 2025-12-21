package view.panels;

import model.enums.WeaponType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel permettant de sélectionner l'arme à utiliser pour attaquer.
 * Propose le choix entre Missile, Bombe, Sonar et Pelle (pour fouiller l'île).
 */
public class WeaponSelectionPanel extends JPanel {

    /** Groupe de boutons radio pour sélection unique */
    private ButtonGroup _weaponGroup;

    /** Radio button pour le missile (arme par défaut, infinie) */
    private JRadioButton _rdbMissile;

    /** Radio button pour la bombe (arme limitée, zone 3x3) */
    private JRadioButton _rdbBomb;

    /** Radio button pour le sonar (arme limitée, détection) */
    private JRadioButton _rdbSonar;

    /** Radio button pour la pelle (fouiller l'île) */
    private JRadioButton _rdbShovel;

    /**
     * Constructeur du panel de sélection d'armes.
     */
    public WeaponSelectionPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Sélection d'arme"));

        initComponents();
    }

    /**
     * Initialise tous les composants du panel.
     */
    private void initComponents() {
        _weaponGroup = new ButtonGroup();

        // Panel pour la grille d'armes (2 lignes, 4 colonnes)
        JPanel weaponsGrid = new JPanel(new GridLayout(2, 4, 10, 10));
        weaponsGrid.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Missile
        JPanel missilePanel = createWeaponButton("Missile", "Bataille Navale/src/img/missile.jpg", true);
        _rdbMissile = (JRadioButton) missilePanel.getComponent(1);
        weaponsGrid.add(missilePanel);

        // Bombe
        JPanel bombPanel = createWeaponButton("Bombe", "Bataille Navale/src/img/bombe.jpg", true);
        _rdbBomb = (JRadioButton) bombPanel.getComponent(1);
        weaponsGrid.add(bombPanel);

        // Sonar
        JPanel sonarPanel = createWeaponButton("Sonar", "Bataille Navale/src/img/sonar.jpg", true);
        _rdbSonar = (JRadioButton) sonarPanel.getComponent(1);
        weaponsGrid.add(sonarPanel);

        // Pelle (fouiller l'île)
        JPanel shovelPanel = createWeaponButton("Fouiller l'île", "Bataille Navale/src/img/pelle.jpg", true);
        _rdbShovel = (JRadioButton) shovelPanel.getComponent(1);
        weaponsGrid.add(shovelPanel);

        add(weaponsGrid);

        // Sélectionner missile par défaut
        _rdbMissile.setSelected(true);
    }

    /**
     * Crée un panel pour un bouton d'arme avec son image et son radio button.
     *
     * @param name Le nom de l'arme
     * @param imagePath Le chemin vers l'image de l'arme
     * @param enabled true si l'arme est activée au départ
     * @return Le panel contenant l'image et le radio button
     */
    private JPanel createWeaponButton(String name, String imagePath, boolean enabled) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Image de l'arme
        JLabel imageLabel = new JLabel();
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            // Si l'image n'existe pas, afficher le nom entre crochets
            imageLabel.setText("[" + name + "]");
            imageLabel.setPreferredSize(new Dimension(60, 60));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

        // Radio button
        JRadioButton radio = new JRadioButton(name);
        radio.setAlignmentX(Component.CENTER_ALIGNMENT);
        radio.setEnabled(enabled);
        _weaponGroup.add(radio);

        panel.add(imageLabel);
        panel.add(radio);

        return panel;
    }

    /**
     * Vérifie si la pelle (fouiller l'île) est sélectionnée.
     *
     * @return true si la pelle est sélectionnée
     */
    public boolean isShovelSelected() {
        return _rdbShovel.isSelected();
    }

    /**
     * Retourne l'arme actuellement sélectionnée.
     *
     * @return Le type d'arme sélectionné
     */
    public WeaponType getSelectedWeapon() {
        if (_rdbBomb.isSelected()) {
            return WeaponType.BOMB;
        }
        if (_rdbSonar.isSelected()) {
            return WeaponType.SONAR;
        }
        return WeaponType.MISSILE; // Par défaut
    }

    /**
     * Active ou désactive une arme spécifique.
     *
     * @param weapon Le type d'arme à activer/désactiver
     * @param enabled true pour activer, false pour désactiver
     */
    public void setWeaponEnabled(WeaponType weapon, boolean enabled) {
        switch (weapon) {
            case BOMB:
                _rdbBomb.setEnabled(enabled);
                break;
            case SONAR:
                _rdbSonar.setEnabled(enabled);
                break;
            case MISSILE:
                _rdbMissile.setEnabled(enabled);
                break;
        }
    }
}