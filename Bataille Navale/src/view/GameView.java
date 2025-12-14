package view;

import controller.GameController;
import model.Observer;
import model.enums.*;
import model.game.GamePlacement;
import model.grid.Position;
import model.players.Player;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class GameView extends JFrame implements Observer {
    private final int gridSize;
    private final String username;

    private JPanel inventoryPanel;
    private JLabel blackholeInventoryLabel;
    private JLabel tornadoInventoryLabel;
    private JButton placeBlackholeButton;
    private JButton placeTornadoButton;
    private JButton cancelPlacementButton;

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

    // Menu déroulant pour l'historique
    private JPopupMenu menuPopup;
    private JMenuItem restartItem;
    private JMenuItem legendItem;
    private JMenuItem quitItem;

    private GameController gameController;


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
    private static final Color WEAPON_COLOR = new Color(218, 14, 232);

    public GameView(int gridSize, String username, GameController gameController, GamePlacement placement) {
        this.gridSize = gridSize;
        this.username = username;
        this.gameController = gameController;

        setTitle("Bataille Navale - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);

        initComponents(placement);
    }

    private void initComponents(GamePlacement placement) {
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
        playerGridPanel = createGridPanel(true, placement);
        gridsAndStatsPanel.add(playerGridPanel, gbc);

        // Grille robot
        gbc.gridx = 2;
        gbc.weightx = 0.35;
        robotGridPanel = createGridPanel(false, placement);
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
        if (title.contains("Robot")) {
            robotBoatsIntactLabel = new JLabel("Bateaux intacts : " + this.gameController.getNumberBoats());
            robotBoatsTouchedLabel = new JLabel("Bateaux touchés : 0");
            robotBoatsSunkLabel = new JLabel("Bateaux coulés : 0");
            robotMissedShotsLabel = new JLabel("Tirs dans l'eau : 0");
            robotHitRatioLabel = new JLabel("Cases de bateaux touchées : 0/" + this.gameController.getNumberBoatSquares());
            robotWeaponsLabel = new JLabel("<html>Armes:<br/>- Missile: ∞<br/>- Bombe: 1<br/>- Sonar: 1</html>");

            if(this.gameController.hasIsland()){
                robotIslandLabel = new JLabel("Île restante : 16");
            }
            else{
                robotIslandLabel = new JLabel("Île restante : -");
            }

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
        } else {
            playerBoatsIntactLabel = new JLabel("Bateaux intacts : " + this.gameController.getNumberBoats());
            playerBoatsTouchedLabel = new JLabel("Bateaux touchés : 0");
            playerBoatsSunkLabel = new JLabel("Bateaux coulés : 0");
            playerMissedShotsLabel = new JLabel("Tirs dans l'eau : 0");
            playerHitRatioLabel = new JLabel("Cases de bateaux touchées : 0/" + this.gameController.getNumberBoatSquares());
            playerWeaponsLabel = new JLabel("<html>Armes:<br/>- Missile: ∞<br/>- Bombe: 1<br/>- Sonar: 1</html>");

            if(this.gameController.hasIsland()){
                playerIslandLabel = new JLabel("Île restante : 16");
            }
            else{
                playerIslandLabel = new JLabel("Île restante : -");
            }

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
        }

        if (!title.contains("Robot")) {
            panel.add(Box.createVerticalStrut(20));
            inventoryPanel = createInventoryPanel();
            panel.add(inventoryPanel);
        }

        panel.add(Box.createVerticalGlue());

        return panel;
    }


    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                "Inventaire",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)
        ));
        panel.setMaximumSize(new Dimension(250, 150));

        // Labels pour afficher le nombre de pièges
        blackholeInventoryLabel = new JLabel("Trou Noir: 0");
        blackholeInventoryLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        blackholeInventoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        tornadoInventoryLabel = new JLabel("Tornade: 0");
        tornadoInventoryLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        tornadoInventoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Boutons pour placer les pièges
        placeBlackholeButton = new JButton("Placer");
        placeBlackholeButton.setFont(new Font("Arial", Font.PLAIN, 10));
        placeBlackholeButton.setEnabled(false);
        placeBlackholeButton.setMaximumSize(new Dimension(80, 25));
        placeBlackholeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        placeBlackholeButton.addActionListener(e ->
                this.gameController.startPlacingTrapFromInventory(TrapType.BLACKHOLE)
        );

        placeTornadoButton = new JButton("Placer");
        placeTornadoButton.setFont(new Font("Arial", Font.PLAIN, 10));
        placeTornadoButton.setEnabled(false);
        placeTornadoButton.setMaximumSize(new Dimension(80, 25));
        placeTornadoButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        placeTornadoButton.addActionListener(e ->
                this.gameController.startPlacingTrapFromInventory(TrapType.TORNADO)
        );

        // Bouton annuler (caché par défaut)
        cancelPlacementButton = new JButton("Annuler placement");
        cancelPlacementButton.setFont(new Font("Arial", Font.PLAIN, 10));
        cancelPlacementButton.setVisible(false);
        cancelPlacementButton.setMaximumSize(new Dimension(150, 25));
        cancelPlacementButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        cancelPlacementButton.addActionListener(e ->
                this.gameController.cancelTrapPlacement()
        );

        // Panneau pour Trou Noir
        JPanel blackholePanel = new JPanel();
        blackholePanel.setLayout(new BoxLayout(blackholePanel, BoxLayout.X_AXIS));
        blackholePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        blackholePanel.add(blackholeInventoryLabel);
        blackholePanel.add(Box.createHorizontalStrut(10));
        blackholePanel.add(placeBlackholeButton);

        // Panneau pour Tornade
        JPanel tornadoPanel = new JPanel();
        tornadoPanel.setLayout(new BoxLayout(tornadoPanel, BoxLayout.X_AXIS));
        tornadoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        tornadoPanel.add(tornadoInventoryLabel);
        tornadoPanel.add(Box.createHorizontalStrut(10));
        tornadoPanel.add(placeTornadoButton);

        panel.add(Box.createVerticalStrut(5));
        panel.add(blackholePanel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(tornadoPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(cancelPlacementButton);
        panel.add(Box.createVerticalStrut(5));

        return panel;
    }


    public int showTrapFoundDialog(String trapName) {
        Object[] options = {"Placer maintenant", "Mettre en inventaire"};

        return JOptionPane.showOptionDialog(
                this,
                "Vous avez trouvé un " + trapName + " !\n\nVoulez-vous le placer tout de suite sur votre grille\nou le mettre dans l'inventaire ?",
                "Piège trouvé !",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
    }



    public void updateInventoryDisplay(Map<TrapType, Integer> inventory) {
        int blackholeCount = inventory.getOrDefault(TrapType.BLACKHOLE, 0);
        int tornadoCount = inventory.getOrDefault(TrapType.TORNADO, 0);

        blackholeInventoryLabel.setText("Trou Noir: " + blackholeCount);
        tornadoInventoryLabel.setText("Tornade: " + tornadoCount);

        placeBlackholeButton.setEnabled(blackholeCount > 0);
        placeTornadoButton.setEnabled(tornadoCount > 0);
    }



    public void setPlacingTrapMode(boolean placing) {
        cancelPlacementButton.setVisible(placing);

        if (placing) {
            // Désactiver les autres boutons pendant le placement
            placeBlackholeButton.setEnabled(false);
            placeTornadoButton.setEnabled(false);
        } else {
            // Réactiver selon l'inventaire
            Player player = this.gameController.getPlayer();
            updateInventoryDisplay(player.getTrapInventory());
        }
    }

    public void colorPlayerGridCell(int x, int y, Color color) {
        if (x >= 0 && x < gridSize && y >= 0 && y < gridSize) {
            playerGridButtons[y][x].setBackground(color);
        }
    }


    public Color getTrapColor() {
        return TRAP_COLOR;
    }

    public Player getPlayer() {
        return this.gameController.getPlayer();
    }



    private JLabel createStatsLabel(JLabel label) {
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createGridPanel(boolean isPlayerGrid, GamePlacement placement) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder( BorderFactory.createLineBorder(Color.DARK_GRAY, 2), isPlayerGrid ? "Votre grille" : "Grille adverse", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14) ));

        // Grille de boutons
        JPanel grid = new JPanel(new GridLayout(gridSize, gridSize, 1, 1));
        grid.setBackground(Color.DARK_GRAY);

        JButton[][] buttons = new JButton[gridSize][gridSize];

        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                JButton btn = new JButton();

                if(this.gameController.isInIsland(x, y)){
                    btn.setBackground(ISLAND_COLOR);
                }
                else{
                    btn.setBackground(WATER_COLOR);
                }

                btn.setPreferredSize(new Dimension(40, 40));
                btn.setFocusPainted(false);
                btn.setBorderPainted(true);

                final int finalX = x;
                final int finalY = y;

                if (!isPlayerGrid) {
                    btn.addActionListener(e -> this.gameController.handleGridClick(finalX, finalY, false));
                }
                else {
                    btn.addActionListener(e -> this.gameController.handleGridClick(finalX, finalY, true));
                }

                buttons[y][x] = btn;
                grid.add(btn);
            }
        }

        if (isPlayerGrid) {
            playerGridButtons = buttons;

            Map<BoatName, List<Position>> placementsBoats = placement.getBoatPlacementsPlayer();

            for(Map.Entry<BoatName, List<Position>> entry : placementsBoats.entrySet()){
                int size = 2;
                switch(entry.getKey()){
                    case AIRCRAFT_CARRIER:
                        size = 5;
                        break;
                    case CRUISER:
                        size = 4;
                        break;
                    case DESTROYER, SUBMARINE:
                        size = 3;
                        break;
                }

                for(Position position : entry.getValue()){
                    if(position.getOrientation() == Orientation.HORIZONTAL){
                        for (int i = 0; i < size; i++) {
                            playerGridButtons[position.getY()][position.getX()+i].setBackground(BOAT_COLOR);
                        }
                    }
                    else {
                        for (int i = 0; i < size; i++) {
                            playerGridButtons[position.getY()+i][position.getX()].setBackground(BOAT_COLOR);
                        }
                    }
                }
            }

            Map<TrapType, List<Position>> placementTraps = placement.getTrapPlacementsPlayer();

            for(Map.Entry<TrapType, List<Position>> entry : placementTraps.entrySet()){
                for(Position pos  : entry.getValue()){
                    playerGridButtons[pos.getY()][pos.getX()].setBackground(TRAP_COLOR);

                    String text;
                    if(entry.getKey() == TrapType.BLACKHOLE){
                        text = "TN";
                    }
                    else{
                        text = "TO";
                    }
                    playerGridButtons[pos.getY()][pos.getX()].setText(text);
                }
            }

            Map<WeaponType, List<Position>> placementWeapons = placement.getWeaponPlacementPlayer();

            for(Map.Entry<WeaponType, List<Position>> entry : placementWeapons.entrySet()){
                for(Position pos  : entry.getValue()){
                    playerGridButtons[pos.getY()][pos.getX()].setBackground(WEAPON_COLOR);

                    String text;
                    if(entry.getKey() == WeaponType.BOMB){
                        text = "B";
                    }
                    else{
                        text = "S";
                    }
                    playerGridButtons[pos.getY()][pos.getX()].setText(text);
                }
            }

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

        legendItem = new JMenuItem("Légendes");
        quitItem = new JMenuItem("Quitter");
        quitItem.addActionListener(e -> this.gameController.quit());

        menuPopup.add(legendItem);
        menuPopup.addSeparator();
        menuPopup.add(quitItem);

        legendItem.addActionListener(e -> showLegend());
    }

    private void showMenu(Component component) {
        menuPopup.show(component, 0, component.getHeight());
    }

    private void showLegend() {
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        //Colors
        JLabel titleColors = new JLabel("Couleurs");
        titleColors.setFont(new Font("Arial", Font.BOLD, 18));
        titleColors.setAlignmentX(Component.CENTER_ALIGNMENT);

        legendPanel.add(titleColors);
        legendPanel.add(Box.createVerticalStrut(10));

        JPanel colorsPanel = new JPanel(new GridLayout(0, 2, 10, 5));

        addLegendItem(colorsPanel, "Eau", WATER_COLOR);
        addLegendItem(colorsPanel, "Bateau", BOAT_COLOR);
        addLegendItem(colorsPanel, "Touché", HIT_COLOR);
        addLegendItem(colorsPanel, "Coulé", SUNK_COLOR);
        addLegendItem(colorsPanel, "Manqué", MISS_COLOR);
        addLegendItem(colorsPanel, "Île", ISLAND_COLOR);
        addLegendItem(colorsPanel, "Île fouillée (vide)", ISLAND_SEARCHED_EMPTY);
        addLegendItem(colorsPanel, "Île fouillée (trouvé)", ISLAND_SEARCHED_FOUND);
        addLegendItem(colorsPanel, "Piège", TRAP_COLOR);

        legendPanel.add(colorsPanel);

        //Words
        legendPanel.add(Box.createVerticalStrut(20));

        JLabel titleWords = new JLabel("Abréviations");
        titleWords.setFont(new Font("Arial", Font.BOLD, 18));
        titleWords.setAlignmentX(Component.CENTER_ALIGNMENT);

        legendPanel.add(titleWords);
        legendPanel.add(Box.createVerticalStrut(10));

        JPanel wordsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        wordsPanel.add(new JLabel("TO = Tornade"));
        wordsPanel.add(new JLabel("TN = Trou noir"));
        wordsPanel.add(new JLabel("B = Bombe"));
        wordsPanel.add(new JLabel("S = Sonar"));

        legendPanel.add(wordsPanel);


        JOptionPane.showMessageDialog(this, legendPanel, "Légende des couleurs/abréviations", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addLegendItem(JPanel panel, String text, Color color) {
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(30, 20));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        panel.add(colorBox);
        panel.add(new JLabel(text));
    }

    // Methodes pour le controller

    public void setTurnNumber(int turn) {
        turnLabel.setText("Tour " + turn);
    }

    public void updatePlayerStats(int intact, int touched, int sunk, int missed, int hitCells, int totalBoatCells) {
        playerBoatsIntactLabel.setText("Bateaux intacts : " + intact);
        playerBoatsTouchedLabel.setText("Bateaux touchés : " + touched);
        playerBoatsSunkLabel.setText("Bateaux coulés : " + sunk);
        playerMissedShotsLabel.setText("Tirs dans l'eau : " + missed);
        playerHitRatioLabel.setText("Cases de bateaux touchées : " + hitCells + "/" + totalBoatCells);
    }


    public void updateRobotStats(int intact, int touched, int sunk, int missed, int hitCells, int totalBoatCells) {
        robotBoatsIntactLabel.setText("Bateaux intacts : " + intact);
        robotBoatsTouchedLabel.setText("Bateaux touchés : " + touched);
        robotBoatsSunkLabel.setText("Bateaux coulés : " + sunk);
        robotMissedShotsLabel.setText("Tirs dans l'eau : " + missed);
        robotHitRatioLabel.setText("Cases de bateaux touchées : " + hitCells + "/" + totalBoatCells);
    }


    public void updatePlayerWeapons(int missiles, int bombs, int sonars) {
        String text = "<html>Armes:<br/>";
        text += "-Missile: ∞<br/>";
        text += "-Bombe: " + bombs + "<br/>";
        text += "-Sonar: " + sonars + "<br/>";
        playerWeaponsLabel.setText(text);
    }

    public void updateRobotWeapons(int missiles, int bombs, int sonars) {
        String text = "<html>Armes:<br/>";
        text += "-Missile: ∞<br/>";
        text += "-Bombe: " + bombs + "<br/>";
        text += "-Sonar: " + sonars + "<br/>";
        robotWeaponsLabel.setText(text);
    }

    public void updatePlayerIsland(int remaining) {
        playerIslandLabel.setText("Île restante : " + remaining);
    }

    public void updateRobotIsland(int remaining) {
        robotIslandLabel.setText("Île restante : " + remaining);
    }

    public void setPlayerAction(String action) {
        this.playerActionArea.setText(action);
    }

    public void setRobotAction(String action) {
        this.robotActionArea.setText(action);
    }

    public void appendHistory(String history) {
        this.historyArea.append(history);
    }

    public void clearHistory() {
        this.historyArea.setText("");
    }

    public boolean isShovelSelected(){
        return this.shovelRadio.isSelected();
    }

    public WeaponType getSelectedWeapon(){
        if(this.bombRadio.isSelected()){
            return WeaponType.BOMB;
        }
        
        if(this.sonarRadio.isSelected()){
            return WeaponType.SONAR;
        }

        return WeaponType.MISSILE;
    }

    public void setWeaponEnabled(WeaponType weapon, boolean enabled){
        switch(weapon){
            case BOMB:
                this.bombRadio.setEnabled(enabled);
                break;
            case SONAR:
                this.sonarRadio.setEnabled(enabled);
                break;
            default:
                this.missileRadio.setEnabled(true);
        }
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }


    //Méthodes de l'observer

    @Override
    public void boatAttacked(Position position, boolean robot) {
        if(robot){
            // Mise à jour visuelle
            if(this.robotGridButtons[position.getY()][position.getX()].getBackground() != SUNK_COLOR) {
                this.robotGridButtons[position.getY()][position.getX()].setBackground(HIT_COLOR);
            }

            // Demander au Contrôleur de recalculer et afficher les stats
            //gameController.updateRobotStatsDisplay();
        }
        else{
            // Pareil pour le joueur
            if(this.playerGridButtons[position.getY()][position.getX()].getBackground() != SUNK_COLOR) {
                this.playerGridButtons[position.getY()][position.getX()].setBackground(HIT_COLOR);
            }
            //gameController.updatePlayerStatsDisplay();
        }
    }


    @Override
    public void boatSunk(Position position, int size, boolean robot) {
        if(robot){
            // Colorier toutes les cases du bateau coulé
            switch (position.getOrientation()){
                case VERTICAL:
                    for(int i=0; i<size; i++){
                        this.robotGridButtons[position.getY()+i][position.getX()].setBackground(SUNK_COLOR);
                    }
                    break;
                case HORIZONTAL:
                    for(int i=0; i<size; i++){
                        this.robotGridButtons[position.getY()][position.getX()+i].setBackground(SUNK_COLOR);
                    }
                    break;
                default:
                    this.robotGridButtons[position.getY()][position.getX()].setBackground(SUNK_COLOR);
            }

            // Demander la mise à jour des stats
            //gameController.updateRobotStatsDisplay();
        }
        else{
            // Pareil pour le joueur
            switch (position.getOrientation()){
                case VERTICAL:
                    for(int i=0; i<size; i++){
                        this.playerGridButtons[position.getY()+i][position.getX()].setBackground(SUNK_COLOR);
                    }
                    break;
                case HORIZONTAL:
                    for(int i=0; i<size; i++){
                        this.playerGridButtons[position.getY()][position.getX()+i].setBackground(SUNK_COLOR);
                    }
                    break;
                default:
                    this.playerGridButtons[position.getY()][position.getX()].setBackground(SUNK_COLOR);
            }
            //gameController.updatePlayerStatsDisplay();
        }
    }

    @Override
    public void boatTouched(boolean robot) {
        if(robot){
            //gameController.updateRobotStatsDisplay();
        }
        else{
            //gameController.updatePlayerStatsDisplay();
        }
    }

    @Override
    public void squareAttacked(Position position, boolean robot) {
        if(robot){
            // 1. Colorier en gris (raté)
            if(!(this.robotGridButtons[position.getY()][position.getX()].getBackground().equals(HIT_COLOR) ||
                    this.robotGridButtons[position.getY()][position.getX()].getBackground().equals(SUNK_COLOR))){
                this.robotGridButtons[position.getY()][position.getX()].setBackground(MISS_COLOR);
            }

            // 2. Mettre à jour les stats
            //gameController.updateRobotStatsDisplay();
        }
        else{
            // Pareil pour le joueur
            if(!(this.playerGridButtons[position.getY()][position.getX()].getBackground().equals(HIT_COLOR) ||
                    this.playerGridButtons[position.getY()][position.getX()].getBackground().equals(SUNK_COLOR))){
                this.playerGridButtons[position.getY()][position.getX()].setBackground(MISS_COLOR);
            }
            //gameController.updatePlayerStatsDisplay();
        }
    }

    @Override
    public void squareIsland(Position position, State state, boolean robot) {
        if(robot){
            switch(state){
                case EMPTY:
                    this.robotGridButtons[position.getY()][position.getX()].setBackground(ISLAND_SEARCHED_EMPTY);
                    break;
                case SEARCHED:
                    this.robotGridButtons[position.getY()][position.getX()].setBackground(ISLAND_SEARCHED_FOUND);
                    break;
                default:
                    this.robotGridButtons[position.getY()][position.getX()].setBackground(ISLAND_COLOR);
            }

            int remaining = Integer.parseInt(robotIslandLabel.getText().substring(15)) -1;
            updateRobotIsland(remaining);
        }
        else{
            switch(state){
                case EMPTY:
                    this.playerGridButtons[position.getY()][position.getX()].setBackground(ISLAND_SEARCHED_EMPTY);
                    break;
                case SEARCHED:
                    this.playerGridButtons[position.getY()][position.getX()].setBackground(ISLAND_SEARCHED_FOUND);
                    break;
                default:
                    this.playerGridButtons[position.getY()][position.getX()].setBackground(ISLAND_COLOR);
            }

            int remaining = Integer.parseInt(playerIslandLabel.getText().substring(15)) -1;
            updatePlayerIsland(remaining);
        }
    }

    public JLabel getPlayerBoatsIntactLabel() {
        return playerBoatsIntactLabel;
    }

    public JLabel getPlayerBoatsTouchedLabel() {
        return playerBoatsTouchedLabel;
    }

    public JLabel getPlayerBoatsSunkLabel() {
        return playerBoatsSunkLabel;
    }

    public JLabel getPlayerMissedShotsLabel() {
        return playerMissedShotsLabel;
    }

    public JLabel getPlayerHitRatioLabel() {
        return playerHitRatioLabel;
    }

    public JLabel getRobotBoatsIntactLabel() {
        return robotBoatsIntactLabel;
    }

    public JLabel getRobotBoatsTouchedLabel() {
        return robotBoatsTouchedLabel;
    }

    public JLabel getRobotBoatsSunkLabel() {
        return robotBoatsSunkLabel;
    }

    public JLabel getRobotMissedShotsLabel() {
        return robotMissedShotsLabel;
    }

    public JLabel getRobotHitRatioLabel() {
        return robotHitRatioLabel;
    }
}