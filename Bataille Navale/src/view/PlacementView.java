package view;

import controller.PlacementController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;

public class PlacementView extends JFrame {

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

    // Callback pour notifier le Controller d'un clic sur la grille
    private BiConsumer<Integer, Integer> gridClickCallback;
    // Callback pour le hover
    private BiConsumer<Integer, Integer> gridHoverCallback;

    private boolean modeIsland;

    private PlacementController controller;

    public PlacementView(int gridSize, String username, boolean modeIsland, PlacementController controller) {
        this.gridSize = gridSize;
        this.modeIsland = modeIsland;
        this.controller = controller;

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
                if(this.modeIsland){
                    int xIsland = this.gridSize/2 -2;
                    int yIsland = this.gridSize/2 -2;

                    if((y >= yIsland && y < yIsland + 4) && (x >= xIsland && x < xIsland + 4)){
                        btn.setBackground(new Color(248, 193, 59));
                    }
                }
                else {
                    btn.setBackground(new Color(100, 150, 200));
                }

                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

                final int fx = x, fy = y;

                btn.addActionListener(e -> {
                    if (gridClickCallback != null) {
                        gridClickCallback.accept(fx, fy);
                    }
                });

                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hoverX = fx; hoverY = fy;
                        if (gridHoverCallback != null) gridHoverCallback.accept(fx, fy);
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hoverX = -1; hoverY = -1;
                        if (gridHoverCallback != null) gridHoverCallback.accept(-1, -1);
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
        randomRadio = new JRadioButton("Aléatoire");
        manualRadio = new JRadioButton("Manuel", true);
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
    public void addFixedModeListener(ActionListener l) { fixedRadio.addActionListener(l); }
    public void addRandomModeListener(ActionListener l) { randomRadio.addActionListener(l); }
    public void addManualModeListener(ActionListener l) { manualRadio.addActionListener(l); }
    public void addBoatSelectorListener(ActionListener l) { boatSelector.addActionListener(l); }
    public void addTrapWeaponSelectorListener(ActionListener l) { trapWeaponSelector.addActionListener(l); }

    public void setGridClickCallback(BiConsumer<Integer, Integer> callback) {
        this.gridClickCallback = callback;
    }

    public void setGridHoverCallback(BiConsumer<Integer, Integer> callback) {
        this.gridHoverCallback = callback;
    }


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

    public void setBoatOptions(String[] options) {
        boatSelector.removeAllItems();
        for (String opt : options) boatSelector.addItem(opt);
    }

    public void setTrapWeaponOptions(String[] options) {
        trapWeaponSelector.removeAllItems();
        for (String opt : options) trapWeaponSelector.addItem(opt);
    }

    public void enableBoatSelector(boolean enable) {
        boatSelector.setEnabled(enable);
    }

    public void enableTrapWeaponSelector(boolean enable) {
        trapWeaponSelector.setEnabled(enable);
    }

    public void setCellColor(int x, int y, Color color) {
        if (x >= 0 && x < gridSize && y >= 0 && y < gridSize) {
            gridButtons[y][x].setBackground(color);
        }
    }

    public void setInfoText(String text) { infoLabel.setText(text); }
    public void setPhaseText(String text) { phaseLabel.setText(text); }

    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

}
