package view.dialogs;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Classe utilitaire contenant tous les dialogues utilisés pendant le jeu.
 * Fournit des méthodes statiques pour afficher des messages, dialogues de choix,
 * résultats de sonar, effets de pièges, etc.
 */
public class GameDialogs {

    /**
     * Constructeur privé pour empêcher l'instanciation.
     */
    private GameDialogs() {
        throw new UnsupportedOperationException("Classe utilitaire - ne pas instancier");
    }

    /**
     * Affiche un message d'erreur.
     *
     * @param parent La fenêtre parente
     * @param message Le message d'erreur
     */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "❌ Erreur", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Affiche un message de succès.
     *
     * @param parent La fenêtre parente
     * @param message Le message de succès
     */
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "✅ Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Affiche un dialogue demandant quoi faire avec un piège trouvé.
     *
     * @param parent La fenêtre parente
     * @param trapName Le nom du piège trouvé
     * @return 0 si "Placer maintenant", 1 si "Mettre en inventaire", -1 si fermé
     */
    public static int showTrapFoundDialog(Component parent, String trapName) {
        Object[] options = {"Placer maintenant", "Mettre en inventaire"};

        return JOptionPane.showOptionDialog(
                parent,
                "Vous avez trouvé un " + trapName + " !\n\n" +
                        "Voulez-vous le placer tout de suite sur votre grille\n" +
                        "ou le mettre dans l'inventaire ?",
                "Piège trouvé !",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
    }

    /**
     * Affiche un effet de piège avec une couleur appropriée.
     *
     * @param parent La fenêtre parente
     * @param title Le titre du message
     * @param message Le contenu du message
     * @param beneficial true si c'est bon pour le joueur (vert), false si mauvais (rouge)
     */
    public static void showTrapEffect(Component parent, String title, String message, boolean beneficial) {
        // Créer un panel personnalisé avec la couleur de fond
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Couleur de fond selon l'effet
        Color backgroundColor = beneficial ?
                new Color(200, 255, 200) :  // Vert clair pour positif
                new Color(255, 200, 200);    // Rouge clair pour négatif
        panel.setBackground(backgroundColor);

        // Titre en gros et gras
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(beneficial ?
                new Color(0, 120, 0) :      // Vert foncé
                new Color(180, 0, 0));       // Rouge foncé
        panel.add(titleLabel, BorderLayout.NORTH);

        // Message principal
        JTextArea messageArea = new JTextArea(message);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setEditable(false);
        messageArea.setOpaque(false);
        messageArea.setBorder(new EmptyBorder(10, 5, 10, 5));
        panel.add(messageArea, BorderLayout.CENTER);

        // Afficher le dialog
        JOptionPane.showMessageDialog(
                parent,
                panel,
                beneficial ? "✅ Piège Activé (Vous)" : "⚠️ Piège Activé (Adversaire)",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    /**
     * Affiche le résultat du sonar avec une visualisation de la zone 3x3.
     *
     * @param parent La fenêtre parente
     * @param centerX Position X du centre
     * @param centerY Position Y du centre
     * @param occupiedCells Nombre de cases occupées détectées
     * @param isPlayerSonar true si c'est le sonar du joueur
     * @param gridSize Taille de la grille (pour vérifier les limites)
     */
    public static void showSonarResult(Component parent, int centerX, int centerY,
                                       int occupiedCells, boolean isPlayerSonar, int gridSize) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(230, 240, 255));

        // Titre
        JLabel titleLabel = new JLabel(
                isPlayerSonar ? "📡 VOTRE SONAR" : "📡 SONAR DU ROBOT",
                SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 100, 200));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Panel central avec grille + résultat
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);

        // Visualisation de la zone 3x3
        JPanel gridPanel = new JPanel(new GridLayout(3, 3, 2, 2));
        gridPanel.setBackground(Color.DARK_GRAY);
        gridPanel.setBorder(BorderFactory.createTitledBorder("Zone scannée"));

        Color centerColor = new Color(255, 200, 100);
        Color questionColor = new Color(150, 180, 255);

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                JPanel cell = new JPanel(new BorderLayout());
                cell.setPreferredSize(new Dimension(60, 60));

                // Couleur différente pour le centre
                if (dx == 0 && dy == 0) {
                    cell.setBackground(centerColor);
                    JLabel centerLabel = new JLabel("📡", SwingConstants.CENTER);
                    centerLabel.setFont(new Font("Arial", Font.BOLD, 20));
                    cell.add(centerLabel, BorderLayout.CENTER);
                } else {
                    cell.setBackground(questionColor);
                }

                // Afficher les coordonnées en petit
                int posX = centerX + dx;
                int posY = centerY + dy;

                if (posX >= 0 && posX < gridSize && posY >= 0 && posY < gridSize) {
                    JLabel coordLabel = new JLabel(posX + "," + posY, SwingConstants.CENTER);
                    coordLabel.setFont(new Font("Arial", Font.PLAIN, 9));
                    coordLabel.setForeground(dx == 0 && dy == 0 ? Color.WHITE : new Color(60, 100, 180));
                    cell.add(coordLabel, BorderLayout.SOUTH);
                }

                cell.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
                gridPanel.add(cell);
            }
        }

        centerPanel.add(gridPanel, BorderLayout.CENTER);

        // Résultat en gros
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setOpaque(false);
        resultPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel resultLabel = new JLabel("Résultat du scan :", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel countLabel = new JLabel(occupiedCells + " case(s) occupée(s)", SwingConstants.CENTER);
        countLabel.setFont(new Font("Arial", Font.BOLD, 32));
        countLabel.setForeground(occupiedCells > 0 ? new Color(200, 0, 0) : new Color(0, 150, 0));
        countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        resultPanel.add(resultLabel);
        resultPanel.add(Box.createVerticalStrut(5));
        resultPanel.add(countLabel);

        centerPanel.add(resultPanel, BorderLayout.SOUTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Info supplémentaire
        JLabel infoLabel = new JLabel(
                "Le sonar détecte le nombre de cases occupées",
                SwingConstants.CENTER
        );
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoLabel.setForeground(Color.DARK_GRAY);
        panel.add(infoLabel, BorderLayout.SOUTH);

        // Afficher
        JOptionPane.showMessageDialog(
                parent,
                panel,
                "📡 Résultat du Sonar",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    /**
     * Affiche la légende des couleurs et abréviations.
     *
     * @param parent La fenêtre parente
     * @param waterColor Couleur de l'eau
     * @param boatColor Couleur des bateaux
     * @param hitColor Couleur touché
     * @param sunkColor Couleur coulé
     * @param missColor Couleur manqué
     * @param islandColor Couleur île
     * @param islandSearchedEmpty Couleur île fouillée (vide)
     * @param islandSearchedFound Couleur île fouillée (trouvé)
     * @param trapColor Couleur piège
     */
    public static void showLegend(Component parent, Color waterColor, Color boatColor,
                                  Color hitColor, Color sunkColor, Color missColor,
                                  Color islandColor, Color islandSearchedEmpty,
                                  Color islandSearchedFound, Color trapColor) {
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Section Couleurs
        JLabel titleColors = new JLabel("Couleurs");
        titleColors.setFont(new Font("Arial", Font.BOLD, 18));
        titleColors.setAlignmentX(Component.CENTER_ALIGNMENT);
        legendPanel.add(titleColors);
        legendPanel.add(Box.createVerticalStrut(10));

        JPanel colorsPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        addLegendItem(colorsPanel, "Eau", waterColor);
        addLegendItem(colorsPanel, "Bateau", boatColor);
        addLegendItem(colorsPanel, "Touché", hitColor);
        addLegendItem(colorsPanel, "Coulé", sunkColor);
        addLegendItem(colorsPanel, "Manqué", missColor);
        addLegendItem(colorsPanel, "Île", islandColor);
        addLegendItem(colorsPanel, "Île fouillée (vide)", islandSearchedEmpty);
        addLegendItem(colorsPanel, "Île fouillée (trouvé)", islandSearchedFound);
        addLegendItem(colorsPanel, "Piège", trapColor);
        legendPanel.add(colorsPanel);

        // Section Abréviations
        legendPanel.add(Box.createVerticalStrut(20));
        JLabel titleWords = new JLabel("Abréviations");
        titleWords.setFont(new Font("Arial", Font.BOLD, 18));
        titleWords.setAlignmentX(Component.CENTER_ALIGNMENT);
        legendPanel.add(titleWords);
        legendPanel.add(Box.createVerticalStrut(10));

        JPanel wordsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        wordsPanel.add(new JLabel("O = Tornade"));
        wordsPanel.add(new JLabel("N = Trou noir"));
        wordsPanel.add(new JLabel("B = Bombe"));
        wordsPanel.add(new JLabel("S = Sonar"));
        legendPanel.add(wordsPanel);

        JOptionPane.showMessageDialog(
                parent,
                legendPanel,
                "Légende des couleurs/abréviations",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Ajoute un item de légende (carré de couleur + texte).
     *
     * @param panel Le panel où ajouter l'item
     * @param text Le texte descriptif
     * @param color La couleur à afficher
     */
    private static void addLegendItem(JPanel panel, String text, Color color) {
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(30, 20));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        panel.add(colorBox);
        panel.add(new JLabel(text));
    }
}