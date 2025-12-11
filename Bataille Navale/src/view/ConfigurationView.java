package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ConfigurationView extends JFrame {

    private JTextField usernameField;
    private JComboBox<String> gridSizeCombo;
    private JRadioButton modeStandardRadio, modeIslandRadio;
    private JRadioButton robotRandomRadio, robotSmartRadio;
    private JRadioButton boatDefault, boatCustom;
    private JRadioButton trapFixedRadio, trapRandomRadio, trapManualRadio;
    private JRadioButton islandRandomRadio, islandManualRadio;
    private JButton backButton, nextButton, customizeBoatButton;
    private JLabel boatInfoLabel;
    private JPanel trapPanel;

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

        modeStandardRadio.addActionListener(e -> updateTrapPanel());
        modeIslandRadio.addActionListener(e -> updateTrapPanel());

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

        customizeBoatButton.addActionListener(e -> openBoatCustomizationDialog());

        configPanel.add(customizeBoatButton);
        configPanel.add(Box.createVerticalStrut(10));

        boatInfoLabel = new JLabel("Total: 17 cases utilisées");
        boatInfoLabel.setFont(new Font("Arial", Font.ITALIC, 12));

        configPanel.add(boatInfoLabel);
        configPanel.add(Box.createVerticalStrut(20));

        // Placement des pièges
        trapPanel = new JPanel();
        trapPanel.setLayout(new BoxLayout(trapPanel, BoxLayout.Y_AXIS));
        trapPanel.setBackground(Color.WHITE);

        configPanel.add(trapPanel);
        updateTrapPanel();

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

    private void openBoatCustomizationDialog() {
        JDialog dialog = new JDialog(this, "Personnalisation des bateaux", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] names = {"Porte-avions", "Croiseur", "Contre-torpilleur", "Sous-marin", "Torpilleur"};
        int[] sizes = {5, 4, 3, 3, 2};
        JSpinner[] spinners = new JSpinner[5];
        JLabel totalLabel = new JLabel();

        // Créer les lignes
        for (int i = 0; i < 5; i++) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel nameLabel = new JLabel(names[i] + " (" + sizes[i] + " cases)");
            nameLabel.setPreferredSize(new Dimension(200, 25));
            spinners[i] = new JSpinner(new SpinnerNumberModel(customBoatNumbers[i], 1, 3, 1));
            spinners[i].setPreferredSize(new Dimension(60, 25));

            // Mettre à jour le total quand on change une valeur
            final int[] sizesRef = sizes;
            spinners[i].addChangeListener(e -> updateDialogTotal(spinners, sizesRef, totalLabel));

            row.add(nameLabel);
            row.add(spinners[i]);
            panel.add(row);
        }

        panel.add(Box.createVerticalStrut(20));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(totalLabel);
        updateDialogTotal(spinners, sizes, totalLabel);

        // Bouton valider
        JButton validateBtn = new JButton("Valider");
        validateBtn.addActionListener(e -> {
            int total = calculateDialogTotal(spinners, sizes);
            if (total > 35) {
                JOptionPane.showMessageDialog(dialog,
                        "Maximum 35 cases ! Actuellement: " + total,
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Sauvegarder les valeurs dans l'attribut de la Vue
            for (int i = 0; i < 5; i++) {
                customBoatNumbers[i] = (Integer) spinners[i].getValue();
            }
            updateBoatInfo();
            dialog.dispose();
        });

        panel.add(Box.createVerticalStrut(20));
        panel.add(validateBtn);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void updateDialogTotal(JSpinner[] spinners, int[] sizes, JLabel label) {
        int total = calculateDialogTotal(spinners, sizes);
        label.setText("Total: " + total + " cases" + (total > 35 ? " (DÉPASSÉ !)" : ""));
        label.setForeground(total > 35 ? Color.RED : Color.BLACK);
    }

    private int calculateDialogTotal(JSpinner[] spinners, int[] sizes) {
        int total = 0;
        for (int i = 0; i < spinners.length; i++) {
            total += (Integer) spinners[i].getValue() * sizes[i];
        }
        return total;
    }



    // pour avoir mêmes écritures partt
    private JLabel createSection(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(30, 50, 100));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // change par rapport à si île sélectionné ou pas.
    private void updateTrapPanel() {
        trapPanel.removeAll();
        if(modeIslandRadio.isSelected()) {
            trapPanel.add(createSection("Placement des pièges et armes sur l'île"));
        }
        else{
            trapPanel.add(createSection("Placement des pièges"));
        }
        trapPanel.add(Box.createVerticalStrut(10));

        if (modeStandardRadio.isSelected()) {
            ButtonGroup trapGroup = new ButtonGroup();
            trapFixedRadio = new JRadioButton("Fixe", true);
            trapRandomRadio = new JRadioButton("Aléatoire");
            trapManualRadio = new JRadioButton("Manuel");
            trapGroup.add(trapFixedRadio);
            trapGroup.add(trapRandomRadio);
            trapGroup.add(trapManualRadio);
            trapPanel.add(trapFixedRadio);
            trapPanel.add(trapRandomRadio);
            trapPanel.add(trapManualRadio);
        } else {
            ButtonGroup islandGroup = new ButtonGroup();
            islandRandomRadio = new JRadioButton("Aléatoire", true);
            islandManualRadio = new JRadioButton("Manuel");
            islandGroup.add(islandRandomRadio);
            islandGroup.add(islandManualRadio);
            trapPanel.add(islandRandomRadio);
            trapPanel.add(islandManualRadio);
        }

        trapPanel.add(Box.createVerticalStrut(20));
        trapPanel.revalidate();
        trapPanel.repaint();
    }

    private void updateBoatInfo() {
        int[] sizes = {5, 4, 3, 3, 2};
        int total = 0;
        for (int i = 0; i < 5; i++) {
            total += customBoatNumbers[i] * sizes[i];
        }
        boatInfoLabel.setText("Total: " + total + " cases utilisées");
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

    public void setCustomBoatNumbers(int[] numbers) {
        this.customBoatNumbers = numbers.clone();
        updateBoatInfo();
    }

    public String getTrapPlacementMode() {
        if (modeStandardRadio.isSelected()) {
            if (trapFixedRadio != null && trapFixedRadio.isSelected()) return "FIXED";
            if (trapRandomRadio != null && trapRandomRadio.isSelected()) return "RANDOM";
            return "MANUAL";
        } else {
            if (islandRandomRadio != null && islandRandomRadio.isSelected()) return "RANDOM";
            return "MANUAL";
        }
    }

    // pour l'affichage
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }
}
