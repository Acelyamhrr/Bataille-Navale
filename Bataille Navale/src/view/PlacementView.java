package view;

import controller.PlacementController;
import model.enums.*;
import model.grid.Grid;
import model.grid.Position;
import model.placement.Placement;
import model.placement.PlacementObserver;
import model.placement.PreviewInfo;
import model.placement.SelectionState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

public class PlacementView extends JFrame  implements PlacementObserver {

    private int gridSize;
    private JButton[][] gridButtons;
    private JLabel phaseLabel;
    private JLabel infoLabel;
    private JComboBox<String> boatSelector, trapWeaponSelector;
    private JButton orientationButton;
    private JRadioButton fixedRadio, randomRadio, manualRadio;
    private JButton backButton, validateButton;
    private JPanel controlPanel;

    private boolean isHorizontal = true;
    private int hoverX = -1, hoverY = -1;

    private PlacementController controller;
    private Placement model;

    // Couleurs
    private static final Color WATER_COLOR = new Color(100, 150, 200);
    private static final Color BOAT_COLOR = new Color(80, 80, 80);
    private static final Color ISLAND_COLOR = new Color(210, 180, 140);
    private static final Color TRAP_COLOR = new Color(243, 88, 48);
    private static final Color WEAPON_COLOR = new Color(218, 14, 232);
    private static final Color PREVIEW_OK = new Color(100, 200, 100);
    private static final Color PREVIEW_BAD = new Color(200, 100, 100);

    public PlacementView(int gridSize, String username, PlacementController controller, Placement model) {
        this.gridSize = gridSize;
        this.controller = controller;
        this.model = model;

        setTitle("Placement - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(240, 245, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Titre
        phaseLabel = new JLabel("Phase: Placement des bateaux", SwingConstants.CENTER);
        phaseLabel.setFont(new Font("Arial", Font.BOLD, 24));
        phaseLabel.setForeground(new Color(30, 50, 100));

        // Grille
        JPanel gridPanel = createGridPanel();

        // Contrôles à droite
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 120, 140), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        controlPanel.setPreferredSize(new Dimension(300, 0));

        createControls();

        // Boutons du bas
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(new Color(240, 245, 250));
        backButton = createBtn("Retour", new Color(150, 150, 150));
        validateButton = createBtn("Valider et Commencer", new Color(70, 150, 70));
        btnPanel.add(backButton);
        btnPanel.add(validateButton);

        mainPanel.add(phaseLabel, BorderLayout.NORTH);
        mainPanel.add(gridPanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.EAST);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createGridPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(100, 120, 140), 2));

        JPanel grid = new JPanel(new GridLayout(gridSize, gridSize, 2, 2));
        grid.setBackground(new Color(200, 220, 240));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        gridButtons = new JButton[gridSize][gridSize];

        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(45, 45));

                //Color for island or water
                if(controller.squareInIsland(x, y)){
                    btn.setBackground(ISLAND_COLOR);
                }
                else {
                    btn.setBackground(WATER_COLOR);
                }

                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

                final int fx = x, fy = y;

                btn.addActionListener(e -> controller.onGridClick(fx, fy));

                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hoverX = fx; hoverY = fy;
                        controller.updateGrid();
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hoverX = -1; hoverY = -1;
                        controller.updateGrid();
                    }
                });

                gridButtons[y][x] = btn;
                grid.add(btn);
            }
        }

        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private void createControls() {
        controlPanel.removeAll();

        JLabel title = new JLabel("Placement des éléments");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlPanel.add(title);
        controlPanel.add(Box.createVerticalStrut(20));

        // Mode de placement
        JLabel modeLabel = new JLabel("Mode:");
        modeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlPanel.add(modeLabel);

        ButtonGroup modeGroup = new ButtonGroup();
        fixedRadio = new JRadioButton("Fixe");
        fixedRadio.addActionListener(e -> controller.applyFixedPlacement());
        randomRadio = new JRadioButton("Aléatoire");
        randomRadio.addActionListener(e -> controller.applyRandomPlacement());
        manualRadio = new JRadioButton("Manuel", true);
        manualRadio.addActionListener(e -> controller.enableManualPlacement());
        modeGroup.add(fixedRadio);
        modeGroup.add(randomRadio);
        modeGroup.add(manualRadio);

        fixedRadio.setBackground(Color.WHITE);
        randomRadio.setBackground(Color.WHITE);
        manualRadio.setBackground(Color.WHITE);
        fixedRadio.setAlignmentX(Component.CENTER_ALIGNMENT);
        randomRadio.setAlignmentX(Component.CENTER_ALIGNMENT);
        manualRadio.setAlignmentX(Component.CENTER_ALIGNMENT);

        controlPanel.add(fixedRadio);
        controlPanel.add(randomRadio);
        controlPanel.add(manualRadio);
        controlPanel.add(Box.createVerticalStrut(20));

        // Sélecteur de bateau
        JLabel boatLabel = new JLabel("Bateau à placer:");
        boatLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlPanel.add(boatLabel);

        boatSelector = new JComboBox<>();
        boatSelector.setMaximumSize(new Dimension(250, 30));
        boatSelector.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlPanel.add(boatSelector);
        controlPanel.add(Box.createVerticalStrut(15));

        // Sélecteur de piège/arme
        JLabel trapWeaponLabel = new JLabel("Piège/Arme à placer:");
        trapWeaponLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlPanel.add(trapWeaponLabel);

        trapWeaponSelector = new JComboBox<>();
        trapWeaponSelector.setMaximumSize(new Dimension(250, 30));
        trapWeaponSelector.setAlignmentX(Component.CENTER_ALIGNMENT);
        trapWeaponSelector.setEnabled(false);
        controlPanel.add(trapWeaponSelector);
        controlPanel.add(Box.createVerticalStrut(15));

        // Orientation
        JLabel orientLabel = new JLabel("Orientation:");
        orientLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlPanel.add(orientLabel);

        orientationButton = new JButton("→ Horizontal");
        orientationButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        orientationButton.addActionListener(e -> toggleOrientation());
        controlPanel.add(orientationButton);
        controlPanel.add(Box.createVerticalStrut(20));

        // Info
        infoLabel = new JLabel("Cliquez sur la grille");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlPanel.add(infoLabel);

        controlPanel.add(Box.createVerticalGlue());
        controlPanel.revalidate();
        controlPanel.repaint();
    }

    private void toggleOrientation() {
        isHorizontal = !isHorizontal;
        orientationButton.setText(isHorizontal ? "→ Horizontal" : "↓ Vertical");
    }

    private JButton createBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(180, 40));
        return btn;
    }

    // pour le controlleur

    public void addBackListener(ActionListener l) { backButton.addActionListener(l); }
    public void addValidateListener(ActionListener l) { validateButton.addActionListener(l); }
    public void addBoatSelectorListener(ActionListener l) { boatSelector.addActionListener(l); }
    public void addTrapWeaponSelectorListener(ActionListener l) { trapWeaponSelector.addActionListener(l); }


    public boolean isHorizontal() { return isHorizontal; }
    public int getSelectedBoatIndex() { return boatSelector.getSelectedIndex(); }
    public int getHoverX() { return hoverX; }
    public int getHoverY() { return hoverY; }

    public String getModeBoat() {
        if(fixedRadio.isSelected()){
            return "Fixed";
        }
        else if(randomRadio.isSelected()){
            return "Random";
        }
        else {
            return "Manual";
        }
    }

    private void setBoatOptions(String[] options) {
        boatSelector.removeAllItems();
        for (String opt : options) boatSelector.addItem(opt);
    }

    private void setTrapWeaponOptions(String[] options) {
        trapWeaponSelector.removeAllItems();
        for (String opt : options) trapWeaponSelector.addItem(opt);
    }

    private void enableBoatSelector(boolean enable) {
        boatSelector.setEnabled(enable);
    }

    private void enableTrapWeaponSelector(boolean enable) {
        trapWeaponSelector.setEnabled(enable);
    }

    public void setCellColor(int x, int y, String color) {
        switch(color){
            case "island":
                gridButtons[y][x].setBackground(ISLAND_COLOR);
                break;
            case "boat":
                gridButtons[y][x].setBackground(BOAT_COLOR);
                break;
            case "trap":
                gridButtons[y][x].setBackground(TRAP_COLOR);
                break;
            case "weapon":
                gridButtons[y][x].setBackground(WEAPON_COLOR);
                break;
            case "previewOk":
                gridButtons[y][x].setBackground(PREVIEW_OK);
                break;
            case "previewBad":
                gridButtons[y][x].setBackground(PREVIEW_BAD);
                break;
            default:
                gridButtons[y][x].setBackground(WATER_COLOR);
        }

    }

    public void setCellText(int x, int y, String text) {
        gridButtons[y][x].setText(text);
    }

    private void setInfoText(String text) { infoLabel.setText(text); }
    private void setPhaseText(String text) { phaseLabel.setText(text); }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    // METHODES OBSERVER

    @Override
    public void onGridChanged(Grid grid) {
        int size = grid.getSize();

        // Reset
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                setCellColor(x, y, "water");
                setCellText(x, y, "");
            }
        }

        // Île
        if (grid.hasIsland()) {
            drawIsland(size);
        }

        // Éléments placés
        drawBoats(grid);
        drawTraps(grid);
        drawWeapons(grid);

        // Preview
        drawPreview();
    }

    private void drawIsland(int gridSize) {
        int ix = gridSize / 2 - 2;
        int iy = gridSize / 2 - 2;
        for (int i = ix; i < ix + 4; i++) {
            for (int j = iy; j < iy + 4; j++) {
                setCellColor(i, j, "island");
            }
        }
    }

    private void drawBoats(Grid grid) {
        for(Position position : grid.getPositionsBoats()){
            setCellColor(position.getX(), position.getY(), "boat");
        }
    }

    private void drawTraps(Grid grid) {
        for(Map.Entry<TrapType, java.util.List<Position>> entry : grid.getPositionsTraps().entrySet()) {
            String text = (entry.getKey() == TrapType.BLACKHOLE ? "Trou noir" : "Tornade");
            for(Position pos : entry.getValue()) {
                setCellColor(pos.getX(), pos.getY(), "trap");
                setCellText(pos.getX(), pos.getY(), text);
            }
        }
    }

    private void drawWeapons(Grid grid) {
        for(Map.Entry<WeaponType, List<Position>> entry : grid.getPositionsWeapons().entrySet()) {
            String text = (entry.getKey() == WeaponType.BOMB ? "Bombe" : "Sonar");
            for(Position pos : entry.getValue()) {
                setCellColor(pos.getX(), pos.getY(), "weapon");
                setCellText(pos.getX(), pos.getY(), text);
            }
        }
    }

    private void drawPreview() {
        int hx = getHoverX();
        int hy = getHoverY();
        if (hx < 0 || hy < 0) return;

        Orientation orient = isHorizontal() ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        PreviewInfo preview = model.getPreviewInfo(hx, hy, orient);

        if (preview != null) {
            String color = preview.isValid() ? "previewOk" : "previewBad";
            for(Position pos : preview.getCells()){
                setCellColor(pos.getX(), pos.getY(), color);
            }
        }
    }

    @Override
    public void onPhaseChanged(PlacementPhase phase) {
        String phaseText;
        switch (phase) {
            case WEAPONS:
                phaseText = "Phase: Placement des armes";
                break;
            case TRAPS:
                phaseText = "Phase: Placement des pièges";
                break;
            default:
                phaseText = "Phase: Placement des bateaux";
        };
        setPhaseText(phaseText);
    }

    @Override
    public void onMessage(String message, MessageType type) {
        switch (type) {
            case INFO:
                setInfoText(message);
                break;
            case SUCCESS:
                setInfoText(message);
                showSuccess(message);
                break;
            case ERROR:
                setInfoText(message);
                showError(message);
                break;
        }
    }

    @Override
    public void onSelectionChanged(SelectionState state) {
        //TODO : Essayer de suppr SelectionState (faire un par combo ?)
        setBoatOptions(state.getBoatOptions().toArray(new String[0]));
        setTrapWeaponOptions(state.getTrapWeaponOptions().toArray(new String[0]));
        enableBoatSelector(state.isBoatSelectorEnabled());
        enableTrapWeaponSelector(state.isTrapWeaponSelectorEnabled());
    }
}
