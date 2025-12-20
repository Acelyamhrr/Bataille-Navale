package view.panels;

import javax.swing.*;
import java.awt.*;

/**
 * Panel permettant de selectionner le mode de placement des pièges dans une partie.<p>
 *  Ce panel s'adapte selon le mode de jeu sélectionné :
 *  <ul>
 *    <li><b>Mode standard</b> : 3 options (Fixe, Aléatoire, Manuel)</li>
 *    <li><b>Mode île</b> : 2 options (Aléatoire, Manuel) pour les pièges et armes</li>
 *  </ul>
 *  </p>
 */
public class TrapPlacementPanel extends JPanel {

    /** Bouton radio pour le placement fixe des pièges (mode standard uniquement) */
    private JRadioButton _rdbTrapFixed;

    /** Bouton radio pour le placement aléatoire des pièges (mode standard) */
    private JRadioButton _rdbtrapRandom;

    /** Bouton radio pour le placement manuel des pièges (mode standard) */
    private JRadioButton _rdbTrapManual;

    /** Bouton radio pour le placement aléatoire en mode île */
    private JRadioButton _rdbIslandRandom;

    /** Bouton radio pour le placement manuel en mode île */
    private JRadioButton _rdbIslandManual;

    /** Indique si le panel est actuellement en mode île (true) ou standard (false) */
    private boolean _isIslandMode;

    /**
     * Construit un nouveau panel de placement de pièges.
     */
    public TrapPlacementPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
    }

    /**
     * Met à jour le contenu du panel selon le mode de jeu sélectionné.
     * Supprime tous les composants existants et recrée l'interface adaptée
     * au mode choisi. Cette méthode doit être appelée chaque fois que le mode
     * de jeu change.
     *
     * <p><b>Mode standard (isIslandMode = false)</b> :</p>
     * <ul>
     *   <li>Fixe : Pièges placés à des positions prédéfinies</li>
     *   <li>Aléatoire : Pièges placés aléatoirement sur la grille</li>
     *   <li>Manuel : L'utilisateur place les pièges manuellement</li>
     * </ul>
     *
     * <p><b>Mode île (isIslandMode = true)</b> :</p>
     * <ul>
     *   <li>Aléatoire : Pièges et armes placés aléatoirement sur l'île</li>
     *   <li>Manuel : L'utilisateur place les pièges et armes manuellement</li>
     * </ul>
     *
     * @param isIslandMode {@code true} pour le mode île, {@code false} pour le mode standard
     */
    public void updateForMode(boolean isIslandMode) {
        this._isIslandMode = isIslandMode;
        removeAll(); // Supprime tous les composants existants

        // Création du titre adapté au mode
        JLabel titleLabel = createSectionLabel( isIslandMode ? "Placement des pièges et armes sur l'île" : "Placement des pièges");
        add(titleLabel);
        add(Box.createVerticalStrut(10)); // Espacement

        if (!isIslandMode) {
            // Configuration pour le mode standard avec 3 options
            ButtonGroup trapGroup = new ButtonGroup();
            _rdbTrapFixed = new JRadioButton("Fixe", true); // Sélectionné par défaut
            _rdbtrapRandom = new JRadioButton("Aléatoire");
            _rdbTrapManual = new JRadioButton("Manuel");

            // Regroupement des boutons (un seul sélectionnable à la fois)
            trapGroup.add(_rdbTrapFixed);
            trapGroup.add(_rdbtrapRandom);
            trapGroup.add(_rdbTrapManual);

            add(_rdbTrapFixed);
            add(_rdbtrapRandom);
            add(_rdbTrapManual);
        } else {
            // Configuration pour le mode île avec 2 options
            ButtonGroup islandGroup = new ButtonGroup();
            _rdbIslandRandom = new JRadioButton("Aléatoire", true); // Sélectionné par défaut
            _rdbIslandManual = new JRadioButton("Manuel");

            // Regroupement des boutons (un seul sélectionnable à la fois)
            islandGroup.add(_rdbIslandRandom);
            islandGroup.add(_rdbIslandManual);

            add(_rdbIslandRandom);
            add(_rdbIslandManual);
        }

        // Espacement final
        add(Box.createVerticalStrut(20));

        // Rafraîchissement de l'affichage
        revalidate();
        repaint();
    }

    /**
     * Retourne le mode de placement actuellement sélectionné par l'utilisateur.
     * <p>
     * Les valeurs retournées dépendent du mode de jeu actif :
     * </p>
     *
     * <p><b>En mode standard</b> :</p>
     * <ul>
     *   <li>"FIXED" : Placement fixe des pièges</li>
     *   <li>"RANDOM" : Placement aléatoire des pièges</li>
     *   <li>"MANUAL" : Placement manuel des pièges</li>
     * </ul>
     *
     * <p><b>En mode île</b> :</p>
     * <ul>
     *   <li>"RANDOM" : Placement aléatoire sur l'île</li>
     *   <li>"MANUAL" : Placement manuel sur l'île</li>
     * </ul>
     *
     * @return Une chaîne représentant le mode sélectionné : "FIXED", "RANDOM" ou "MANUAL"
     */
    public String getSelectedMode() {
        if (!_isIslandMode) {
            // Vérification des sélections en mode standard
            if (_rdbTrapFixed != null && _rdbTrapFixed.isSelected()) return "FIXED";
            if (_rdbtrapRandom != null && _rdbtrapRandom.isSelected()) return "RANDOM";
            return "MANUAL"; // Par défaut si aucun n'est sélectionné
        } else {
            // Vérification des sélections en mode île
            if (_rdbIslandRandom != null && _rdbIslandRandom.isSelected()) return "RANDOM";
            return "MANUAL"; // Par défaut si aucun n'est sélectionné
        }
    }

    /**
     * Crée un label pour les titres de section.
     * <p>
     * Style appliqué :
     * <ul>
     *   <li>Police : Arial, gras, taille 16</li>
     *   <li>Couleur : Bleu foncé (RGB: 30, 50, 100)</li>
     *   <li>Alignement : Gauche</li>
     * </ul>
     * </p>
     *
     * @param text Le texte à afficher dans le label
     * @return Un {@code JLabel} stylisé prêt à être ajouté au panel
     */
    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(30, 50, 100)); // Bleu foncé
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }


}
