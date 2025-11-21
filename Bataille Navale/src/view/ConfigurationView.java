package view;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationView extends JFrame {

    private JComboBox<String> cboGameMode;
    private JComboBox<String> cboGridSize;
    private JComboBox<String> cboRobotMode;
    private JRadioButton rbBoatsDefault;
    private JRadioButton rbBoatsCustom;
    private JPanel customBoatsPanel;

    // Map qui associe chaque type de bateau à son JSpinner
    // En gros : "nom du bateau" -> spinner correspondant
    // C'est plus pratique qu'une liste, on récupère direct le bon spinner avec une clé.
    private Map<String, JSpinner> boatSpinners; // pour recupérer les quantités de bateaux. Jspinner c'est le truc avec les flèches pour choisir un nombre.
    private JLabel lblTotalCases;
    private JButton btnValidate;

    // Trap panels
    private JPanel trapPanel;
    private JRadioButton rbTrapFixed;
    private JRadioButton rbTrapRandom;
    private JRadioButton rbTrapManual;

    // Island mode panels
    private JPanel islandPanel;
    private JRadioButton rbIslandRandomBefore;
    private JRadioButton rbIslandManualAfter;

    // Boat types and their sizes
    private final String[] boatTypes = {"Porte-avion (5)", "Croiseur (4)", "Contre-torpilleur (3)", "Sous-marin (3)", "Torpilleur (2)"};
    private final int[] boatSizes = {5, 4, 3, 3, 2};

    // constructor
    public ConfigurationView() {
        setTitle("Configuration de la Partie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 800);
        setLocationRelativeTo(null);        // to center the window

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Configuration de la Partie", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Main panel with scroll if needed
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));        // vertical layout
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        // to scroll if needed
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // game mode
        addSectionTitle(mainPanel, "Mode de jeu");
        String[] modes = {"Mode normal", "Mode île"};
        cboGameMode = new JComboBox<>(modes);
        cboGameMode.setMaximumSize(new Dimension(500, 30));
        cboGameMode.addActionListener(e -> updateVisibility());     // update panel visibility according to the selected modes
        mainPanel.add(cboGameMode);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // grid size
        addSectionTitle(mainPanel, "Taille de la grille");
        String[] sizes = {"6x6", "7x7", "8x8", "9x9", "10x10"};
        cboGridSize = new JComboBox<>(sizes);
        cboGridSize.setSelectedIndex(0);
        cboGridSize.setMaximumSize(new Dimension(500, 30));
        cboGridSize.addActionListener(e -> updateTotalCases());     // to update max cases when grid size changes
        mainPanel.add(cboGridSize);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // robot mode
        addSectionTitle(mainPanel, "Mode du robot");
        String[] robotModes = {"Fixe", "Aléatoire"};
        cboRobotMode = new JComboBox<>(robotModes);
        cboRobotMode.setMaximumSize(new Dimension(500, 30));
        mainPanel.add(cboRobotMode);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // boats configuration
        addSectionTitle(mainPanel, "Nombre de bateaux");

        ButtonGroup boatGroup = new ButtonGroup();      // to group radio buttons
        rbBoatsDefault = new JRadioButton("1 bateau par type (5 bateaux)", true);
        rbBoatsCustom = new JRadioButton("Personnalisé");
        boatGroup.add(rbBoatsDefault);
        boatGroup.add(rbBoatsCustom);

        rbBoatsDefault.setAlignmentX(Component.LEFT_ALIGNMENT);
        rbBoatsCustom.setAlignmentX(Component.LEFT_ALIGNMENT);

        rbBoatsDefault.addActionListener(e -> updateBoatsPanel());      // to show/hide custom panel
        rbBoatsCustom.addActionListener(e -> updateBoatsPanel());

        mainPanel.add(rbBoatsDefault);
        mainPanel.add(rbBoatsCustom);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // if custom boats selected, show panel
        customBoatsPanel = new JPanel();
        customBoatsPanel.setLayout(new BoxLayout(customBoatsPanel, BoxLayout.Y_AXIS));

        // chat gpt suggested border
        customBoatsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 150, 200), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        customBoatsPanel.setMaximumSize(new Dimension(600, 280));
        customBoatsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        customBoatsPanel.setBackground(new Color(240, 245, 250));

        // On initialise la HashMap (la boîte où on range nos spinners)
        // HashMap = gros dictionnaire : tu donnes une clé, il te rend l'objet
        // Exemple : boatSpinners.get("Destroyer") -> te donne le spinner du destroyer
        boatSpinners = new HashMap<>();

        // loop on boat types to create rows
        for (int i = 0; i < boatTypes.length; i++) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));       // line with label + spinner
            row.setMaximumSize(new Dimension(550, 35));
            row.setOpaque(false);

            JLabel lbl = new JLabel(boatTypes[i] + " :");
            lbl.setPreferredSize(new Dimension(200, 25));
            lbl.setFont(new Font("Arial", Font.PLAIN, 13));

            SpinnerModel model = new SpinnerNumberModel(1, 0, 3, 1);        // initiale = 1, min = 0, max = 3, step = 1
            JSpinner spinner = new JSpinner(model);
            spinner.setPreferredSize(new Dimension(60, 25));
            spinner.addChangeListener(e -> updateTotalCases());     // update total cases when value changes

            boatSpinners.put(boatTypes[i], spinner);        // add to map with boat type as key

            row.add(lbl);
            row.add(spinner);
            customBoatsPanel.add(row);
        }

        lblTotalCases = new JLabel("Vous utilisez 17 cases");
        lblTotalCases.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalCases.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTotalCases.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 0));
        customBoatsPanel.add(lblTotalCases);

        customBoatsPanel.setVisible(false);
        mainPanel.add(customBoatsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // trap placement (only for normal mode)
        trapPanel = new JPanel();
        trapPanel.setLayout(new BoxLayout(trapPanel, BoxLayout.Y_AXIS));
        trapPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel trapTitle = new JLabel("Placement des pièges");
        trapTitle.setFont(new Font("Arial", Font.BOLD, 16));
        trapTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        trapPanel.add(trapTitle);
        trapPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        ButtonGroup trapGroup = new ButtonGroup();
        rbTrapFixed = new JRadioButton("Fixe", true);
        rbTrapRandom = new JRadioButton("Aléatoire");
        rbTrapManual = new JRadioButton("Manuel");

        trapGroup.add(rbTrapFixed);
        trapGroup.add(rbTrapRandom);
        trapGroup.add(rbTrapManual);

        rbTrapFixed.setAlignmentX(Component.LEFT_ALIGNMENT);
        rbTrapRandom.setAlignmentX(Component.LEFT_ALIGNMENT);
        rbTrapManual.setAlignmentX(Component.LEFT_ALIGNMENT);

        trapPanel.add(rbTrapFixed);
        trapPanel.add(rbTrapRandom);
        trapPanel.add(rbTrapManual);

        mainPanel.add(trapPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // island mode (gun/trap placement)
        islandPanel = new JPanel();
        islandPanel.setLayout(new BoxLayout(islandPanel, BoxLayout.Y_AXIS));
        islandPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        islandPanel.setVisible(false);

        JLabel islandTitle = new JLabel("Mode île (placement armes/pièges)");
        islandTitle.setFont(new Font("Arial", Font.BOLD, 16));
        islandTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        islandPanel.add(islandTitle);
        islandPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        ButtonGroup islandGroup = new ButtonGroup();
        rbIslandRandomBefore = new JRadioButton("Aléatoire", true);
        rbIslandManualAfter = new JRadioButton("Manuel");

        islandGroup.add(rbIslandRandomBefore);
        islandGroup.add(rbIslandManualAfter);

        rbIslandRandomBefore.setAlignmentX(Component.LEFT_ALIGNMENT);
        rbIslandManualAfter.setAlignmentX(Component.LEFT_ALIGNMENT);

        islandPanel.add(rbIslandRandomBefore);
        islandPanel.add(rbIslandManualAfter);

        mainPanel.add(islandPanel);
        mainPanel.add(Box.createVerticalGlue());

        // buttons validate / back
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton btnBack = new JButton("Retour");
        btnBack.setPreferredSize(new Dimension(120, 35));
        btnBack.addActionListener(e -> {
            new MenuView().setVisible(true);
            this.dispose();
        });

        btnValidate = new JButton("Valider");
        btnValidate.setPreferredSize(new Dimension(120, 35));
        btnValidate.setBackground(new Color(100, 180, 100));
        btnValidate.setForeground(Color.WHITE);
        btnValidate.setFont(new Font("Arial", Font.BOLD, 14));
        btnValidate.addActionListener(e -> ouvrirVuePlacement());

        bottomPanel.add(btnBack);
        bottomPanel.add(btnValidate);

        add(bottomPanel, BorderLayout.SOUTH);

        updateVisibility();
    }

    // helper method to add section titles (to not repeat code) -- chat gpt suggested
    private void addSectionTitle(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
    }

    // update custom boats panel display
    private void updateBoatsPanel() {
        customBoatsPanel.setVisible(rbBoatsCustom.isSelected());        // visible if custom boats selected
        updateTotalCases();     // recalculate total cases

        // chat gpt suggested
        revalidate();       // refresh layout
        repaint();      // refresh display
    }

    // calculate and update total cases used by boats
    private void updateTotalCases() {
        if (!rbBoatsCustom.isSelected()) {
            return;
        }

        int total = 0;
        for (int i = 0; i < boatTypes.length; i++) {
            JSpinner spinner = boatSpinners.get(boatTypes[i]);      // get spinner for this boat type
            int quantity = (Integer) spinner.getValue();        // get quantity selected
            total += quantity * boatSizes[i];       // add to total (quantity * size)
        }

        int maxCases = getMaxCases();
        lblTotalCases.setText("Vous utilisez " + total + " cases");

        if (total > maxCases) {
            lblTotalCases.setForeground(Color.RED);
            lblTotalCases.setText("Vous utilisez " + total + " cases - TROP DE CASES (max " + maxCases + ")");
            btnValidate.setEnabled(false);
        } else if (total == 0) {
            lblTotalCases.setForeground(Color.RED);
            lblTotalCases.setText("Vous devez placer au moins 1 bateau !");
            btnValidate.setEnabled(false);
        } else {
            lblTotalCases.setForeground(new Color(0, 120, 0));
            btnValidate.setEnabled(true);
        }
    }

    private int getMaxCases() {
        String gridSize = (String) cboGridSize.getSelectedItem();
        int size = Integer.parseInt(gridSize.substring(0, gridSize.indexOf('x')));
        return (int) (size * size * 0.35);
    }

    private void updateVisibility() {
        boolean isIslandMode = cboGameMode.getSelectedIndex() == 1;

        trapPanel.setVisible(!isIslandMode);
        islandPanel.setVisible(isIslandMode);

        revalidate();
        repaint();
    }

    private void ouvrirVuePlacement() {

        // check boat configuration if custom (if it's valid or not)
        if (rbBoatsCustom.isSelected()) {
            int total = 0;
            for (int i = 0; i < boatTypes.length; i++) {
                JSpinner spinner = boatSpinners.get(boatTypes[i]);
                total += (Integer) spinner.getValue() * boatSizes[i];
            }

            if (total == 0) {
                JOptionPane.showMessageDialog(this, "Vous devez placer au moins 1 bateau !", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (total > getMaxCases()) {
                JOptionPane.showMessageDialog(this, "Trop de cases utilisées ! Maximum : " + getMaxCases(), "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        PlacementView placement = new PlacementView(this);
        placement.setVisible(true);
        this.dispose();
    }

    // getters to retrieve configuration choices
    public String getGameMode() {
        return (String) cboGameMode.getSelectedItem();
    }

    public int getGridSize() {
        String size = (String) cboGridSize.getSelectedItem();
        return Integer.parseInt(size.substring(0, size.indexOf('x')));
    }

    public String getRobotMode() {
        return (String) cboRobotMode.getSelectedItem();
    }

    public boolean isCustomBoats() {
        return rbBoatsCustom.isSelected();
    }

    // return the boat configuration as a map (type -> quantity)
    public Map<String, Integer> getBoatConfiguration() {
        Map<String, Integer> config = new HashMap<>();
        if (rbBoatsCustom.isSelected()) {
            for (String boatType : boatTypes) {
                config.put(boatType, (Integer) boatSpinners.get(boatType).getValue());
            }
        } else {
            for (String boatType : boatTypes) {
                config.put(boatType, 1);
            }
        }
        return config;
    }

    public String getTrapPlacement() {
        if (rbTrapFixed.isSelected()) return "Fixe";
        if (rbTrapRandom.isSelected()) return "Aléatoire";
        return "Manuel";
    }

    public String getIslandMode() {
        if (rbIslandRandomBefore.isSelected()) return "Aléatoire";
        return "Manuel";
    }

    public boolean isIslandMode() {
        return cboGameMode.getSelectedIndex() == 1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ConfigurationView config = new ConfigurationView();
            config.setVisible(true);
        });
    }
}