package view;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class PlacementView extends JFrame {

    // grid
    private JPanel gridPnl;
    private int rows;
    private int cols;
    private JPanel[][] square;

    // game configuration
    private ConfigurationView config;       // to get settings
    private boolean isIslandMode;       // if island mode is activate or not
    private String trapPlacement;       // trap placement mode
    private String islandPlacementMode;     // island placement mode
    private Map<String, Integer> boatConfig;        // boat configuration (name->quantity)

    // Placement des bateaux
    private JRadioButton rbPlacementFixed;
    private JRadioButton rbPlacementRandom;
    private JRadioButton rbPlacementCustom;

    // Panel personnalisé pour placement manuel
    private JPanel customPlacementPanel;
    private JComboBox<String> cboBoatType;
    private JButton btnLeft, btnRight, btnUp, btnDown, btnRotateLeft, btnRotateRight;
    private JButton btnPlace;
    private DefaultComboBoxModel<String> boatModel;

    // Bateaux disponibles
    private java.util.List<BoatInfo> availableBoats;

    // Position et orientation actuelles pour placement
    private int currentRow = 0;
    private int currentCol = 0;
    private boolean isHorizontal = true;

    // Panel des pièges
    private JPanel trapPlacementPanel;
    private JButton btnPlaceBlackHole;
    private JButton btnPlaceTornado;
    private boolean placingBlackHole = false;       // indicate if placing black hole
    private boolean placingTornado = false;
    private boolean blackHolePlaced = false;
    private boolean tornadoPlaced = false;

    // Grille de placement (pour vérifier les collisions)
    private boolean[][] occupiedCells;      // tabl 2d qui indique si une cellule est occupée
    private java.util.List<PlacedBoat> placedBoats;     //liste des bateaux placés

    // Zone île (D4-D7, E4-E7 dans grille 10x10) - ajusté selon la taille
    private int ISLAND_START_ROW;       // ligne de départ de l'île
    private int ISLAND_START_COL;       // colonne de départ de l'île
    private int ISLAND_SIZE = 4;        // taille de l'île (4x4) par defaut

    // Classe interne pour stocker les infos des bateaux
    private static class BoatInfo {
        String name;        // name of the boat
        int size;       // size of the boat
        int count;      // number of boats available

        // construct
        BoatInfo(String name, int size, int count) {
            this.name = name;
            this.size = size;
            this.count = count;
        }
    }

    // Classe pour les bateaux placés
    private static class PlacedBoat {
        int row, col;       // position of the boat
        int size;       // size of the boat
        boolean horizontal;     // orientation

        PlacedBoat(int row, int col, int size, boolean horizontal) {
            this.row = row;
            this.col = col;
            this.size = size;
            this.horizontal = horizontal;
        }
    }

    // constructor
    public PlacementView(ConfigurationView config) {
        this.config = config;       // sotck the config
        this.rows = config.getGridSize();       // number of rows
        this.cols = config.getGridSize();       // number of cols
        this.isIslandMode = config.isIslandMode();      // island mode?
        this.trapPlacement = config.getTrapPlacement();     // trap placement mode
        this.islandPlacementMode = config.getIslandMode();      // island placement mode
        this.boatConfig = config.getBoatConfiguration();        // boat configuration

        // calculate island position (in the center of the grid)
        ISLAND_START_ROW = rows / 2 - 2;
        ISLAND_START_COL = cols / 2 - 2;

        this.occupiedCells = new boolean[rows][cols];       // initialize empty occupied cells
        this.placedBoats = new ArrayList<>();       // initialize empty placed boats list

        setTitle("Placement des bateaux");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);

        initAvailableBoats();
        initComponents();
    }

    private void initAvailableBoats() {
        availableBoats = new ArrayList<>();

        // chat gpt !!
        for (Map.Entry<String, Integer> entry : boatConfig.entrySet()) {
            String name = entry.getKey();
            int count = entry.getValue();
            int size = getBoatSizeFromName(name);

            if (count > 0) {
                availableBoats.add(new BoatInfo(name, size, count));
            }
        }
    }

    private int getBoatSizeFromName(String name) {
        if (name.contains("(5)")) return 5;
        if (name.contains("(4)")) return 4;
        if (name.contains("(3)")) return 3;
        if (name.contains("(2)")) return 2;
        return 0;
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("PLACEMENT DES BATEAUX", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Panel central : grille + options
        JPanel center = new JPanel(new BorderLayout());
        add(center, BorderLayout.CENTER);

        // grid
        gridPnl = new JPanel(new GridLayout(rows, cols, 1, 1));

        // chat gpt suggestion
        gridPnl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Grille de placement"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gridPnl.setBackground(Color.GRAY);

        int cellSize = Math.min(500 / rows, 500 / cols);
        gridPnl.setPreferredSize(new Dimension(cellSize * cols + 20, cellSize * rows + 20));

        square = new JPanel[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JPanel cell = new JPanel();
                cell.setPreferredSize(new Dimension(cellSize, cellSize));

                // Colorier l'île en jaune si mode île activé
                if (isIslandMode && isIslandCell(r, c)) {
                    cell.setBackground(new Color(255, 230, 100));
                } else {
                    cell.setBackground(new Color(170, 200, 230));
                }

                cell.setBorder(BorderFactory.createLineBorder(Color.GRAY));

                // Ajouter listener pour placement manuel
                final int row = r;
                final int col = c;
                cell.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        handleCellClick(row, col);
                    }
                });

                square[r][c] = cell;
                gridPnl.add(cell);
            }
        }

        JPanel gridContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        gridContainer.add(gridPnl);
        center.add(gridContainer, BorderLayout.CENTER);


        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        right.setPreferredSize(new Dimension(380, 0));

        // placement mode
        JLabel lblPlacement = new JLabel("Mode de placement des bateaux :");
        lblPlacement.setFont(new Font("Arial", Font.BOLD, 15));
        lblPlacement.setAlignmentX(Component.LEFT_ALIGNMENT);
        right.add(lblPlacement);
        right.add(Box.createRigidArea(new Dimension(0, 10)));

        ButtonGroup placementGroup = new ButtonGroup();
        rbPlacementFixed = new JRadioButton("Fixe", true);
        rbPlacementRandom = new JRadioButton("Aléatoire");
        rbPlacementCustom = new JRadioButton("Personnalisé");

        placementGroup.add(rbPlacementFixed);
        placementGroup.add(rbPlacementRandom);
        placementGroup.add(rbPlacementCustom);

        rbPlacementFixed.setAlignmentX(Component.LEFT_ALIGNMENT);
        rbPlacementRandom.setAlignmentX(Component.LEFT_ALIGNMENT);
        rbPlacementCustom.setAlignmentX(Component.LEFT_ALIGNMENT);

        rbPlacementFixed.addActionListener(e -> updatePlacementMode());
        rbPlacementRandom.addActionListener(e -> updatePlacementMode());
        rbPlacementCustom.addActionListener(e -> updatePlacementMode());

        right.add(rbPlacementFixed);
        right.add(rbPlacementRandom);
        right.add(rbPlacementCustom);
        right.add(Box.createRigidArea(new Dimension(0, 15)));

        // placement panel personalized
        customPlacementPanel = createCustomPlacementPanel();
        right.add(customPlacementPanel);
        right.add(Box.createRigidArea(new Dimension(0, 15)));

        // trap placement panel
        trapPlacementPanel = createTrapPlacementPanel();
        right.add(trapPlacementPanel);
        right.add(Box.createVerticalGlue());

        // validate/play and return buttons
        JButton btnPlay = new JButton("Terminer et Jouer");
        btnPlay.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnPlay.setBackground(new Color(100, 180, 100));
        btnPlay.setForeground(Color.WHITE);
        btnPlay.setFont(new Font("Arial", Font.BOLD, 14));
        btnPlay.addActionListener(e -> openGameView());
        right.add(btnPlay);

        right.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btnReturn = new JButton("Retour au menu");
        btnReturn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnReturn.addActionListener(e -> revenirMenu());
        right.add(btnReturn);

        center.add(right, BorderLayout.EAST);

        updatePlacementMode();
    }

    private JPanel createCustomPlacementPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Placement personnalisé"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(360, 450));
        panel.setBackground(new Color(245, 248, 250));

        // boat selection
        JLabel lblBoat = new JLabel("Bateau à placer :");
        lblBoat.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblBoat.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblBoat);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        boatModel = new DefaultComboBoxModel<>();
        updateBoatComboBox();

        cboBoatType = new JComboBox<>(boatModel);
        cboBoatType.setMaximumSize(new Dimension(340, 30));
        cboBoatType.setAlignmentX(Component.LEFT_ALIGNMENT);
        cboBoatType.addActionListener(e -> highlightCurrentPosition());
        panel.add(cboBoatType);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // controls of movement
        JLabel lblControls = new JLabel("Déplacements :");
        lblControls.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblControls.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblControls);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Arrow buttons
        JPanel arrowPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        arrowPanel.setMaximumSize(new Dimension(200, 140));
        arrowPanel.setOpaque(false);

        btnUp = new JButton("↑");
        btnDown = new JButton("↓");
        btnLeft = new JButton("←");
        btnRight = new JButton("→");

        btnUp.setFont(new Font("Arial", Font.BOLD, 18));
        btnDown.setFont(new Font("Arial", Font.BOLD, 18));
        btnLeft.setFont(new Font("Arial", Font.BOLD, 18));
        btnRight.setFont(new Font("Arial", Font.BOLD, 18));

        btnUp.addActionListener(e -> moveBoat(-1, 0));
        btnDown.addActionListener(e -> moveBoat(1, 0));
        btnLeft.addActionListener(e -> moveBoat(0, -1));
        btnRight.addActionListener(e -> moveBoat(0, 1));

        arrowPanel.add(new JLabel(""));
        arrowPanel.add(btnUp);
        arrowPanel.add(new JLabel(""));
        arrowPanel.add(btnLeft);
        arrowPanel.add(new JLabel("", SwingConstants.CENTER));
        arrowPanel.add(btnRight);
        arrowPanel.add(new JLabel(""));
        arrowPanel.add(btnDown);
        arrowPanel.add(new JLabel(""));

        panel.add(arrowPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Rotation
        JLabel lblRotation = new JLabel("Rotation :");
        lblRotation.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblRotation.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblRotation);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel rotationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        rotationPanel.setMaximumSize(new Dimension(340, 40));
        rotationPanel.setOpaque(false);

        btnRotateLeft = new JButton("⟲ 90°");
        btnRotateRight = new JButton("⟳ 90°");

        btnRotateLeft.setPreferredSize(new Dimension(100, 35));
        btnRotateRight.setPreferredSize(new Dimension(100, 35));

        btnRotateLeft.addActionListener(e -> rotateBoat());
        btnRotateRight.addActionListener(e -> rotateBoat());

        rotationPanel.add(btnRotateLeft);
        rotationPanel.add(btnRotateRight);
        panel.add(rotationPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Bouton placer
        btnPlace = new JButton("PLACER LE BATEAU");
        btnPlace.setMaximumSize(new Dimension(340, 45));
        btnPlace.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPlace.setBackground(new Color(80, 160, 80));
        btnPlace.setForeground(Color.WHITE);
        btnPlace.setFont(new Font("Arial", Font.BOLD, 13));
        btnPlace.addActionListener(e -> placeBoat());
        panel.add(btnPlace);

        panel.setVisible(false);
        return panel;
    }

    private void updateBoatComboBox() {
        boatModel.removeAllElements();
        for (BoatInfo boat : availableBoats) {
            if (boat.count > 0) {
                boatModel.addElement(boat.name + " (reste: " + boat.count + ")");
            }
        }
    }

    private JPanel createTrapPlacementPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Placement des pièges"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(360, 180));
        panel.setBackground(new Color(255, 245, 240));

        JLabel lblTrap = new JLabel("Cliquez sur la grille pour placer :");
        lblTrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTrap.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(lblTrap);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        btnPlaceBlackHole = new JButton("Placer le Trou Noir");
        btnPlaceBlackHole.setMaximumSize(new Dimension(340, 40));
        btnPlaceBlackHole.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPlaceBlackHole.setBackground(new Color(50, 50, 50));
        btnPlaceBlackHole.setForeground(Color.WHITE);
        btnPlaceBlackHole.addActionListener(e -> startPlacingBlackHole());
        panel.add(btnPlaceBlackHole);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        btnPlaceTornado = new JButton("Placer la Tornade");
        btnPlaceTornado.setMaximumSize(new Dimension(340, 40));
        btnPlaceTornado.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPlaceTornado.setBackground(new Color(120, 120, 120));
        btnPlaceTornado.setForeground(Color.WHITE);
        btnPlaceTornado.addActionListener(e -> startPlacingTornado());
        panel.add(btnPlaceTornado);

        panel.setVisible(false);
        return panel;
    }

    private boolean isIslandCell(int row, int col) {
        if (!isIslandMode) return false;
        return row >= ISLAND_START_ROW && row < ISLAND_START_ROW + ISLAND_SIZE && col >= ISLAND_START_COL && col < ISLAND_START_COL + ISLAND_SIZE;
    }

    private void updatePlacementMode() {
        if (rbPlacementFixed.isSelected()) {
            customPlacementPanel.setVisible(false);
            placeBoatsFixed();
        } else if (rbPlacementRandom.isSelected()) {
            customPlacementPanel.setVisible(false);
            placeBoatsRandom();
        } else if (rbPlacementCustom.isSelected()) {
            customPlacementPanel.setVisible(true);
            clearGrid();
            highlightCurrentPosition();
        }

        revalidate();
        repaint();
    }

    private void placeBoatsFixed() {
        clearGrid();
        clearPlacedBoats();
        JOptionPane.showMessageDialog(this, "Placement fixe appliqué");
        checkIfAllBoatsPlaced();
    }

    private void placeBoatsRandom() {
        clearGrid();
        clearPlacedBoats();

        Random rand = new Random();
        for (BoatInfo boat : availableBoats) {
            for (int i = 0; i < boat.count; i++) {
                boolean placed = false;
                int attempts = 0;

                while (!placed && attempts < 100) {
                    int r = rand.nextInt(rows);
                    int c = rand.nextInt(cols);
                    boolean horizontal = rand.nextBoolean();

                    if (canPlaceBoat(r, c, boat.size, horizontal)) {
                        placeBoatOnGrid(r, c, boat.size, horizontal);
                        placed = true;
                    }
                    attempts++;
                }
            }
        }

        JOptionPane.showMessageDialog(this, "Placement aléatoire appliqué");
        checkIfAllBoatsPlaced();
    }

    private boolean canPlaceBoat(int row, int col, int size, boolean horizontal) {
        for (int i = 0; i < size; i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;

            if (r < 0 || r >= rows || c < 0 || c >= cols) {
                return false;
            }

            if (occupiedCells[r][c]) {
                return false;
            }

            if (isIslandCell(r, c)) {
                return false;
            }
        }
        return true;
    }

    private void placeBoatOnGrid(int row, int col, int size, boolean horizontal) {
        for (int i = 0; i < size; i++) {
            int r = horizontal ? row : row + i;
            int c = horizontal ? col + i : col;

            occupiedCells[r][c] = true;
            square[r][c].setBackground(new Color(50, 100, 200));
        }
        placedBoats.add(new PlacedBoat(row, col, size, horizontal));
    }

    private void clearGrid() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (isIslandMode && isIslandCell(r, c)) {
                    square[r][c].setBackground(new Color(255, 230, 100));
                } else {
                    square[r][c].setBackground(new Color(170, 200, 230));
                }
            }
        }
    }

    private void clearPlacedBoats() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                occupiedCells[r][c] = false;
            }
        }
        placedBoats.clear();
    }

    private void moveBoat(int deltaRow, int deltaCol) {
        currentRow = Math.max(0, Math.min(rows - 1, currentRow + deltaRow));
        currentCol = Math.max(0, Math.min(cols - 1, currentCol + deltaCol));
        highlightCurrentPosition();
    }

    private void rotateBoat() {
        isHorizontal = !isHorizontal;
        highlightCurrentPosition();
    }

    private void highlightCurrentPosition() {
        // Restaurer la grille
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (occupiedCells[r][c]) {
                    square[r][c].setBackground(new Color(50, 100, 200));
                } else if (isIslandMode && isIslandCell(r, c)) {
                    square[r][c].setBackground(new Color(255, 230, 100));
                } else {
                    square[r][c].setBackground(new Color(170, 200, 230));
                }
            }
        }

        if (boatModel.getSize() == 0) return;

        String selected = (String) cboBoatType.getSelectedItem();
        if (selected == null) return;

        int size = getBoatSizeFromName(selected);
        boolean valid = canPlaceBoat(currentRow, currentCol, size, isHorizontal);

        // coloriser la position actuelle
        for (int i = 0; i < size; i++) {
            int r = isHorizontal ? currentRow : currentRow + i;
            int c = isHorizontal ? currentCol + i : currentCol;

            if (r >= 0 && r < rows && c >= 0 && c < cols) {
                if (valid) {
                    square[r][c].setBackground(new Color(150, 255, 150));   // valid
                } else {
                    square[r][c].setBackground(new Color(255, 100, 100));   // invalid
                }
            }
        }
    }

    private void placeBoat() {
        if (boatModel.getSize() == 0) {
            JOptionPane.showMessageDialog(this, "Tous les bateaux sont déjà placés !");
            return;
        }

        String selected = (String) cboBoatType.getSelectedItem();
        if (selected == null) return;

        int size = getBoatSizeFromName(selected);
        String boatName = selected.substring(0, selected.indexOf(" (reste:"));

        if (!canPlaceBoat(currentRow, currentCol, size, isHorizontal)) {
            JOptionPane.showMessageDialog(this,
                    "Placement invalide ! Le bateau ne peut pas être placé ici.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Placer le bateau
        placeBoatOnGrid(currentRow, currentCol, size, isHorizontal);

        // Réduire le nombre de bateaux disponibles
        for (BoatInfo boat : availableBoats) {
            if (boat.name.equals(boatName)) {
                boat.count--;
                break;
            }
        }

        // Mettre à jour le combo box
        updateBoatComboBox();

        // Vérifier si tous les bateaux sont placés
        if (boatModel.getSize() == 0) {
            customPlacementPanel.setVisible(false);
            JOptionPane.showMessageDialog(this, "Tous les bateaux sont placés !");
            checkIfAllBoatsPlaced();
        } else {
            cboBoatType.setSelectedIndex(0);
            highlightCurrentPosition();
        }
    }

    private void checkIfAllBoatsPlaced() {
        // Afficher le panel de placement de pièges si nécessaire
        if (trapPlacement.equals("Manuel") && !isIslandMode) {
            trapPlacementPanel.setVisible(true);
        }
    }

    private void handleCellClick(int row, int col) {
        if (placingBlackHole) {
            placeTrap(row, col, "Trou Noir", new Color(50, 50, 50));
            placingBlackHole = false;
            blackHolePlaced = true;
            btnPlaceBlackHole.setEnabled(false);
            btnPlaceBlackHole.setText("✓ Trou Noir placé");
        } else if (placingTornado) {
            placeTrap(row, col, "Tornade", new Color(160, 160, 160));
            placingTornado = false;
            tornadoPlaced = true;
            btnPlaceTornado.setEnabled(false);
            btnPlaceTornado.setText("✓ Tornade placée");
        }

        if (blackHolePlaced && tornadoPlaced) {
            trapPlacementPanel.setVisible(false);
        }
    }

    private void startPlacingBlackHole() {
        placingBlackHole = true;
        placingTornado = false;
        JOptionPane.showMessageDialog(this, "Cliquez sur la grille pour placer le Trou Noir");
    }

    private void startPlacingTornado() {
        placingTornado = true;
        placingBlackHole = false;
        JOptionPane.showMessageDialog(this, "Cliquez sur la grille pour placer la Tornade");
    }

    private void placeTrap(int row, int col, String trapName, Color color) {
        if (isIslandCell(row, col)) {
            JOptionPane.showMessageDialog(this, "Impossible de placer un piège sur l'île !");
            placingBlackHole = false;
            placingTornado = false;
            return;
        }

        if (occupiedCells[row][col]) {
            JOptionPane.showMessageDialog(this, "Impossible de placer un piège sur un bateau !");
            placingBlackHole = false;
            placingTornado = false;
            return;
        }

        square[row][col].setBackground(color);
        char rowLetter = (char) ('A' + row);
        JOptionPane.showMessageDialog(this, trapName + " placé en (" + rowLetter + ", " + (col + 1) + ")");
    }

    private void openGameView() {
        boolean allBoatsPlaced = true;
        for (BoatInfo boat : availableBoats) {
            if (boat.count > 0) {
                allBoatsPlaced = false;
                break;
            }
        }

        if (rbPlacementCustom.isSelected() && !allBoatsPlaced) {
            JOptionPane.showMessageDialog(this, "Vous devez placer tous vos bateaux !");
            return;
        }

        if (trapPlacement.equals("Manuel") && !isIslandMode && (!blackHolePlaced || !tornadoPlaced)) {
            int result = JOptionPane.showConfirmDialog(this,
                    "Vous n'avez pas placé tous vos pièges. Continuer ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (result != JOptionPane.YES_OPTION) return;
        }

        GameView game = new GameView();
        game.setVisible(true);
        this.dispose();
    }

    private void revenirMenu() {
        int result = JOptionPane.showConfirmDialog(this,
                "Retourner au menu ? Votre placement sera perdu.",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            MenuView menu = new MenuView();
            menu.setVisible(true);
            this.dispose();
        }
    }

}