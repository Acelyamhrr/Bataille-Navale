package view;

import model.enums.*;
import model.grid.Position;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class GameView extends JFrame {
    private final int gridSize;
    private final String username;

    // Composants principaux
    private JLabel turnLabel;
    private JPanel playerStatsPanel;
    private JPanel robotStatsPanel;
    private JPanel playerGridPanel;
    private JPanel robotGridPanel;
    private JTextArea playerActionArea;
    private JTextArea robotActionArea;
    private JTextArea historyArea;
    private JPanel weaponPanel;

    // Boutons de grille
    private JButton[][] playerGridButtons;
    private JButton[][] robotGridButtons;

    // Radio buttons pour les armes
    private ButtonGroup weaponGroup;
    private JRadioButton missileRadio;
    private JRadioButton bombRadio;
    private JRadioButton sonarRadio;
    private JRadioButton shovelRadio; // Pour fouiller l'île

    // Labels pour les stats
    private JLabel playerBoatsIntactLabel;
    private JLabel playerBoatsTouchedLabel;
    private JLabel playerBoatsSunkLabel;
    private JLabel playerMissedShotsLabel;
    private JLabel playerHitRatioLabel;
    private JLabel playerWeaponsLabel;
    private JLabel playerIslandLabel;

    private JLabel robotBoatsIntactLabel;
    private JLabel robotBoatsTouchedLabel;
    private JLabel robotBoatsSunkLabel;
    private JLabel robotMissedShotsLabel;
    private JLabel robotHitRatioLabel;
    private JLabel robotWeaponsLabel;
    private JLabel robotIslandLabel;

    // Menu déroulant pour l'historqiue
    private JPopupMenu menuPopup;

    // Callback pour les clics sur la grille robot
    private GridClickCallback gridClickCallback;

    // Couleurs
    private static final Color WATER_COLOR = new Color(100, 150, 200);
    private static final Color BOAT_COLOR = new Color(80, 80, 80);
    private static final Color HIT_COLOR = new Color(255, 100, 100);
    private static final Color SUNK_COLOR = new Color(150, 50, 50);
    private static final Color MISS_COLOR = new Color(200, 200, 200);
    private static final Color ISLAND_COLOR = new Color(210, 180, 140);
    private static final Color ISLAND_SEARCHED_EMPTY = new Color(190, 160, 120);
    private static final Color ISLAND_SEARCHED_FOUND = new Color(255, 215, 0);
    private static final Color TRAP_COLOR = new Color(243, 88, 48);

    public GameView(int gridSize, String username) {
        this.gridSize = gridSize;
        this.username = username;

        setTitle("Bataille Navale - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // En haut : Menu + Numéro du tour
        JPanel topPanel = new JPanel(new BorderLayout());

        // Menu
        JButton menuButton = new JButton("☰");
        menuButton.setFont(new Font("Arial", Font.BOLD, 20));
        menuButton.setPreferredSize(new Dimension(50, 40));
        menuButton.addActionListener(e -> showMenu(menuButton));
        topPanel.add(menuButton, BorderLayout.WEST);

        // Numéro du tour
        turnLabel = new JLabel("Tour 1", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(turnLabel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Au milieu en grand:  Grilles + Stats de chaque joueur
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        JPanel gridsAndStatsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; // va s'étirer en largeur et en hauteur pour remplir toute la cellule dans la grille
        gbc.insets = new Insets(5, 5, 5, 5);        // marges

        // Stats joueur (gauche)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.15;
        gbc.weighty = 1.0;
        playerStatsPanel = createStatsPanel(username);
        gridsAndStatsPanel.add(playerStatsPanel, gbc);

        // Grille joueur
        gbc.gridx = 1;
        gbc.weightx = 0.35;
        playerGridPanel = createGridPanel(true);
        gridsAndStatsPanel.add(playerGridPanel, gbc);

        // Grille robot
        gbc.gridx = 2;
        gbc.weightx = 0.35;
        robotGridPanel = createGridPanel(false);
        gridsAndStatsPanel.add(robotGridPanel, gbc);

        // Stats robot
        gbc.gridx = 3;
        gbc.weightx = 0.15;
        robotStatsPanel = createStatsPanel("Robot");
        gridsAndStatsPanel.add(robotStatsPanel, gbc);

        centerPanel.add(gridsAndStatsPanel, BorderLayout.CENTER);

        // actions
        JPanel actionsPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        playerActionArea = new JTextArea(3, 30);
        playerActionArea.setEditable(false);
        playerActionArea.setBorder(BorderFactory.createTitledBorder("Dernière action - " + username));
        playerActionArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane playerActionScroll = new JScrollPane(playerActionArea);
        actionsPanel.add(playerActionScroll);

        robotActionArea = new JTextArea(3, 30);
        robotActionArea.setEditable(false);
        robotActionArea.setBorder(BorderFactory.createTitledBorder("Dernière action - Robot"));
        robotActionArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane robotActionScroll = new JScrollPane(robotActionArea);
        actionsPanel.add(robotActionScroll);

        centerPanel.add(actionsPanel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // en bas : Historique + Sélection d'armes
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        // Historique
        historyArea = new JTextArea(8, 40);
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyScroll.setBorder(BorderFactory.createTitledBorder("Historique"));
        bottomPanel.add(historyScroll);

        // Sélection d'armes
        weaponPanel = createWeaponPanel();
        bottomPanel.add(weaponPanel);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Initialiser le menu popup
        createMenuPopup();
    }

    private JPanel createStatsPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createLineBorder(Color.DARK_GRAY, 2), title, TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14) ));

        // Créer les labels de stats
        if (title.contains("Joueur")) {
            playerBoatsIntactLabel = new JLabel("Bateaux intacts: 0");
            playerBoatsTouchedLabel = new JLabel("Bateaux touchés: 0");
            playerBoatsSunkLabel = new JLabel("Bateaux coulés: 0");
            playerMissedShotsLabel = new JLabel("Tirs dans l'eau: 0");
            playerHitRatioLabel = new JLabel("Cases touchées: 0/0");
            playerWeaponsLabel = new JLabel("<html>Armes:<br/>- Missile: ∞<br/>- Bombe: 1<br/>- Sonar: 1</html>");
            playerIslandLabel = new JLabel("Île restante: 16");

            panel.add(Box.createVerticalStrut(10));
            panel.add(createStatsLabel(playerBoatsIntactLabel));
            panel.add(createStatsLabel(playerBoatsTouchedLabel));
            panel.add(createStatsLabel(playerBoatsSunkLabel));
            panel.add(Box.createVerticalStrut(10));
            panel.add(createStatsLabel(playerMissedShotsLabel));
            panel.add(createStatsLabel(playerHitRatioLabel));
            panel.add(Box.createVerticalStrut(10));
            panel.add(createStatsLabel(playerWeaponsLabel));
            panel.add(Box.createVerticalStrut(10));
            panel.add(createStatsLabel(playerIslandLabel));
        } else {
            robotBoatsIntactLabel = new JLabel("Bateaux intacts: 0");
            robotBoatsTouchedLabel = new JLabel("Bateaux touchés: 0");
            robotBoatsSunkLabel = new JLabel("Bateaux coulés: 0");
            robotMissedShotsLabel = new JLabel("Tirs dans l'eau: 0");
            robotHitRatioLabel = new JLabel("Cases touchées: 0/0");
            robotWeaponsLabel = new JLabel("<html>Armes:<br/>- Missile: ∞<br/>- Bombe: 1<br/>- Sonar: 1</html>");
            robotIslandLabel = new JLabel("Île restante: ");

            panel.add(Box.createVerticalStrut(10));
            panel.add(createStatsLabel(robotBoatsIntactLabel));
            panel.add(createStatsLabel(robotBoatsTouchedLabel));
            panel.add(createStatsLabel(robotBoatsSunkLabel));
            panel.add(Box.createVerticalStrut(10));
            panel.add(createStatsLabel(robotMissedShotsLabel));
            panel.add(createStatsLabel(robotHitRatioLabel));
            panel.add(Box.createVerticalStrut(10));
            panel.add(createStatsLabel(robotWeaponsLabel));
            panel.add(Box.createVerticalStrut(10));
            panel.add(createStatsLabel(robotIslandLabel));
        }

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JLabel createStatsLabel(JLabel label) {
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createGridPanel(boolean isPlayerGrid) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createLineBorder(Color.DARK_GRAY, 2), isPlayerGrid ? "Votre grille" : "Grille adverse", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14) ));

        // Grille de boutons
        JPanel grid = new JPanel(new GridLayout(gridSize, gridSize, 1, 1));
        grid.setBackground(Color.DARK_GRAY);

        JButton[][] buttons = new JButton[gridSize][gridSize];

        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                JButton btn = new JButton();
                btn.setBackground(WATER_COLOR);
                btn.setPreferredSize(new Dimension(40, 40));
                btn.setFocusPainted(false);
                btn.setBorderPainted(true);

                final int finalX = x;
                final int finalY = y;

                if (!isPlayerGrid) {
                    btn.addActionListener(e -> {
                        if (gridClickCallback != null) { gridClickCallback.onGridClick(finalX, finalY); } });
                }

                buttons[y][x] = btn;
                grid.add(btn);
            }
        }

        if (isPlayerGrid) {
            playerGridButtons = buttons;
        } else {
            robotGridButtons = buttons;
        }

        panel.add(grid, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createWeaponPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Sélection d'arme"));

        weaponGroup = new ButtonGroup();

        // Panel pour les armes
        JPanel weaponsGrid = new JPanel(new GridLayout(2, 4, 10, 10));
        weaponsGrid.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Missile
        JPanel missilePanel = createWeaponButton("Missile", "Bataille Navale/src/img/missile.jpg", true);
        missileRadio = (JRadioButton) missilePanel.getComponent(1);
        weaponsGrid.add(missilePanel);

        // Bombe
        JPanel bombPanel = createWeaponButton("Bombe", "Bataille Navale/src/img/bombe.jpg", true);
        bombRadio = (JRadioButton) bombPanel.getComponent(1);
        weaponsGrid.add(bombPanel);

        // Sonar
        JPanel sonarPanel = createWeaponButton("Sonar", "Bataille Navale/src/img/sonar.jpg", true);
        sonarRadio = (JRadioButton) sonarPanel.getComponent(1);
        weaponsGrid.add(sonarPanel);

        // Pelle (île)
        JPanel shovelPanel = createWeaponButton("Fouiller l'île", "Bataille Navale/src/img/pelle.jpg", true);
        shovelRadio = (JRadioButton) shovelPanel.getComponent(1);
        weaponsGrid.add(shovelPanel);

        panel.add(weaponsGrid);

        // Sélectionner missile par défaut
        missileRadio.setSelected(true);

        return panel;
    }

    private JPanel createWeaponButton(String name, String imagePath, boolean enabled) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Image
        JLabel imageLabel = new JLabel();
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            imageLabel.setText("[" + name + "]");
            imageLabel.setPreferredSize(new Dimension(60, 60));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

        // Radio button
        JRadioButton radio = new JRadioButton(name);
        radio.setAlignmentX(Component.CENTER_ALIGNMENT);
        radio.setEnabled(enabled);
        weaponGroup.add(radio);

        panel.add(imageLabel);
        panel.add(radio);

        return panel;
    }

    private void createMenuPopup() {
        menuPopup = new JPopupMenu();

        JMenuItem restartItem = new JMenuItem("Recommencer");
        JMenuItem legendItem = new JMenuItem("Légendes");
        JMenuItem quitItem = new JMenuItem("Quitter");

        menuPopup.add(restartItem);
        menuPopup.add(legendItem);
        menuPopup.addSeparator();
        menuPopup.add(quitItem);

        legendItem.addActionListener(e -> showLegend());
    }

    private void showMenu(Component component) {
        menuPopup.show(component, 0, component.getHeight());
    }

    private void showLegend() {
        JPanel legendPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        legendPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        addLegendItem(legendPanel, "Eau", WATER_COLOR);
        addLegendItem(legendPanel, "Bateau", BOAT_COLOR);
        addLegendItem(legendPanel, "Touché", HIT_COLOR);
        addLegendItem(legendPanel, "Coulé", SUNK_COLOR);
        addLegendItem(legendPanel, "Manqué", MISS_COLOR);
        addLegendItem(legendPanel, "Île", ISLAND_COLOR);
        addLegendItem(legendPanel, "Île fouillée (vide)", ISLAND_SEARCHED_EMPTY);
        addLegendItem(legendPanel, "Île fouillée (trouvé)", ISLAND_SEARCHED_FOUND);
        addLegendItem(legendPanel, "Piège", TRAP_COLOR);

        JOptionPane.showMessageDialog(this, legendPanel, "Légende des couleurs", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addLegendItem(JPanel panel, String text, Color color) {
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(30, 20));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        panel.add(colorBox);
        panel.add(new JLabel(text));
    }






    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    @FunctionalInterface
    public interface GridClickCallback {
        void onGridClick(int x, int y);
    }
}