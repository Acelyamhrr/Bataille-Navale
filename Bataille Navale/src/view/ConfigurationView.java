package view;

import view.dialogs.BoatCustomizationDialog;
import view.panels.TrapPlacementPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ConfigurationView extends JFrame {

    private JTextField usernameField;
    private JComboBox<String> gridSizeCombo;
    private JRadioButton modeStandardRadio, modeIslandRadio;
    private JRadioButton robotRandomRadio, robotSmartRadio;
    private JRadioButton boatDefault, boatCustom;
    private JButton backButton, nextButton, customizeBoatButton;
    private JLabel boatInfoLabel;

    private TrapPlacementPanel trapPlacementPanel;

    private int[] customBoatNumbers = {1, 1, 1, 1, 1};      // par défaut, un de chaque bateau.

    public ConfigurationView() {
        setTitle("Configuration de la partie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 700);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 245, 250));

        JLabel titleLabel = new JLabel("Configuration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(30, 50, 100));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Panel de configuration
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
        configPanel.setBackground(Color.WHITE);
        configPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // nom du joueur
        configPanel.add(createSection("Nom du joueur"));
        usernameField = new JTextField("Joueur");       // par défaut
        usernameField.setMaximumSize(new Dimension(300, 30));

        configPanel.add(usernameField);
        configPanel.add(Box.createVerticalStrut(20));

        // Taille de la grille
        configPanel.add(createSection("Taille de la grille"));
        String[] sizes = {"6x6", "7x7", "8x8", "9x9", "10x10"};
        gridSizeCombo = new JComboBox<>(sizes);
        gridSizeCombo.setSelectedIndex(0);
        gridSizeCombo.setMaximumSize(new Dimension(150, 30));

        configPanel.add(gridSizeCombo);
        configPanel.add(Box.createVerticalStrut(20));

        // Mode de jeu
        configPanel.add(createSection("Mode de jeu"));
        ButtonGroup modeGroup = new ButtonGroup();
        modeStandardRadio = new JRadioButton("Standard", true);
        modeIslandRadio = new JRadioButton("Île");

        modeGroup.add(modeStandardRadio);
        modeGroup.add(modeIslandRadio);

        modeStandardRadio.addActionListener(e -> trapPlacementPanel.updateForMode(false));
        modeIslandRadio.addActionListener(e -> trapPlacementPanel.updateForMode(true));

        configPanel.add(modeStandardRadio);
        configPanel.add(modeIslandRadio);
        configPanel.add(Box.createVerticalStrut(20));

        // Difficulté du roboy
        configPanel.add(createSection("Difficulté du robot"));
        ButtonGroup robotGroup = new ButtonGroup();
        robotRandomRadio = new JRadioButton("Aléatoire", true);     // par défaut
        robotSmartRadio = new JRadioButton("Intelligent");

        robotGroup.add(robotRandomRadio);
        robotGroup.add(robotSmartRadio);
        configPanel.add(robotRandomRadio);
        configPanel.add(robotSmartRadio);
        configPanel.add(Box.createVerticalStrut(20));

        // nb de bateaux
        configPanel.add(createSection("Nombre de bateaux"));
        ButtonGroup boatGroup = new ButtonGroup();
        boatDefault = new JRadioButton("1 bateau de chaque type", true);
        boatCustom = new JRadioButton("Personnalisé");

        boatGroup.add(boatDefault);
        boatGroup.add(boatCustom);
        configPanel.add(boatDefault);
        configPanel.add(boatCustom);
        configPanel.add(Box.createVerticalStrut(10));

        customizeBoatButton = new JButton("Personnaliser les bateaux");
        customizeBoatButton.setEnabled(false);
        boatCustom.addActionListener(e -> customizeBoatButton.setEnabled(true));

        boatDefault.addActionListener(e -> {
            customizeBoatButton.setEnabled(false);
            customBoatNumbers = new int[]{1, 1, 1, 1, 1};
            updateBoatInfo();
        });

        customizeBoatButton.addActionListener(e -> {
            BoatCustomizationDialog dialog = new BoatCustomizationDialog(this, customBoatNumbers);
            int[] result = dialog.showAndGetResult();
            if (result != null) {
                customBoatNumbers = result;
                updateBoatInfo();
            }
        });


        configPanel.add(customizeBoatButton);
        configPanel.add(Box.createVerticalStrut(10));

        boatInfoLabel = new JLabel("Total: 17 cases utilisées");
        boatInfoLabel.setFont(new Font("Arial", Font.ITALIC, 12));

        configPanel.add(boatInfoLabel);
        configPanel.add(Box.createVerticalStrut(20));

        // Placement des pièges
        trapPlacementPanel = new TrapPlacementPanel();
        trapPlacementPanel.updateForMode(modeIslandRadio.isSelected());
        configPanel.add(trapPlacementPanel);

        JScrollPane scrollPane = new JScrollPane(configPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(240, 245, 250));

        backButton = createButton("Retour", new Color(150, 150, 150));
        nextButton = createButton("Suivant", new Color(70, 150, 70));

        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // pour avoir mêmes écritures partt
    private JLabel createSection(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(30, 50, 100));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Met à jour le label affichant le total de cases utilisées par les bateaux
     */
    private void updateBoatInfo() {
        int[] sizes = {5, 4, 3, 3, 2};
        int total = 0;
        for (int i = 0; i < 5; i++) {
            total += customBoatNumbers[i] * sizes[i];
        }
        boatInfoLabel.setText("Total: " + total + " cases utilisées");
    }

    // pour le controlleur


    // les vues ne se connaissent pas entre eux donc le controlleur va se déplacer entre vues.
    public void addBackListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

    public void addNextListener(ActionListener listener) {
        nextButton.addActionListener(listener);
    }

    public void addCustomizeBoatListener(ActionListener listener) {
        customizeBoatButton.addActionListener(listener);
    }

    // Getters pour récupérer les valeurs : le controlleur va lire les choix de l'utilisateur.
    public String getUsername() {
        return usernameField.getText().trim();
    }

    public int getGridSize() {
        return gridSizeCombo.getSelectedIndex() + 6;
    }

    public boolean isStandardMode() {
        return modeStandardRadio.isSelected();
    }

    public boolean isRandomRobot() {
        return robotRandomRadio.isSelected();
    }

    public boolean isDefaultBoats() {
        return boatDefault.isSelected();
    }

    public int[] getCustomBoatNumbers() {
        return customBoatNumbers.clone();
    }

    public String getTrapPlacementMode() {
        return trapPlacementPanel.getSelectedMode();
    }

    // pour l'affichage
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }
}
