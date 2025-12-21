package view.panels;

import javax.swing.*;
import java.awt.*;

/**
 * Panel permettant de sélectionner le mode de placement des pièges dans une partie.
 * Ce panel s'adapte selon le mode de jeu sélectionné :
 * - Mode standard : 3 options (Fixe, Aléatoire, Manuel)
 * - Mode île : 2 options (Aléatoire, Manuel) pour les pièges et armes
 */
public class TrapPlacementPanel extends JPanel {

    /** Bouton radio pour le placement fixe des pièges (mode standard uniquement) */
    private JRadioButton _rdbTrapFixed;

    /** Bouton radio pour le placement aléatoire des pièges (mode standard) */
    private JRadioButton _rdbTrapRandom;

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
     * Mode standard (isIslandMode = false) :
     * - Fixe : Pièges placés à des positions prédéfinies
     * - Aléatoire : Pièges placés aléatoirement sur la grille
     * - Manuel : L'utilisateur place les pièges manuellement
     *
     * Mode île (isIslandMode = true) :
     * - Aléatoire : Pièges et armes placés aléatoirement sur l'île
     * - Manuel : L'utilisateur place les pièges et armes manuellement
     *
     * @param isIslandMode true pour le mode île, false pour le mode standard
     */
    public void updateForMode(boolean isIslandMode) {
        this._isIslandMode = isIslandMode;
        removeAll(); // Supprime tous les composants existants

        // Création du titre adapté au mode
        JLabel titleLabel = createSectionLabel(
                isIslandMode ? "Placement des pièges et armes sur l'île" : "Placement des pièges"
        );
        add(titleLabel);
        add(Box.createVerticalStrut(10)); // Espacement

        if (!isIslandMode) {
            // Configuration pour le mode standard avec 3 options
            ButtonGroup trapGroup = new ButtonGroup();
            _rdbTrapFixed = new JRadioButton("Fixe", true); // Sélectionné par défaut
            _rdbTrapRandom = new JRadioButton("Aléatoire");
            _rdbTrapManual = new JRadioButton("Manuel");

            // Regroupement des boutons (un seul sélectionnable à la fois)
            trapGroup.add(_rdbTrapFixed);
            trapGroup.add(_rdbTrapRandom);
            trapGroup.add(_rdbTrapManual);

            add(_rdbTrapFixed);
            add(_rdbTrapRandom);
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
     *
     * En mode standard :
     * - "FIXED" : Placement fixe des pièges
     * - "RANDOM" : Placement aléatoire des pièges
     * - "MANUAL" : Placement manuel des pièges
     *
     * En mode île :
     * - "RANDOM" : Placement aléatoire sur l'île
     * - "MANUAL" : Placement manuel sur l'île
     *
     * @return Une chaîne représentant le mode sélectionné : "FIXED", "RANDOM" ou "MANUAL"
     */
    public String getSelectedMode() {
        if (!_isIslandMode) {
            // Vérification des sélections en mode standard
            if (_rdbTrapFixed != null && _rdbTrapFixed.isSelected()) return "FIXED";
            if (_rdbTrapRandom != null && _rdbTrapRandom.isSelected()) return "RANDOM";
            return "MANUAL"; // Par défaut si aucun n'est sélectionné
        } else {
            // Vérification des sélections en mode île
            if (_rdbIslandRandom != null && _rdbIslandRandom.isSelected()) return "RANDOM";
            return "MANUAL"; // Par défaut si aucun n'est sélectionné
        }
    }

    /**
     * Crée un label stylisé pour les titres de section.
     * Style : Arial gras 16, bleu foncé, aligné à gauche
     *
     * @param text Le texte à afficher dans le label
     * @return Un JLabel stylisé prêt à être ajouté au panel
     */
    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(30, 50, 100)); // Bleu foncé
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}