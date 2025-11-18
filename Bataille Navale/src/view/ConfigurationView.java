package view;

import javax.swing.*;
import java.awt.*;

public class ConfigurationView extends JFrame {

    private JComboBox<String> cboGameMod;
    private JComboBox<String> cboGridSize;

    public ConfigurationView() {
        setTitle("Configuration de la Partie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Configuration de la Partie", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        JPanel centralPanel = new JPanel();
        centralPanel.setLayout(new GridLayout(4, 1, 10, 10));
        centralPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        // Game mode selection
        centralPanel.add(new JLabel("Mode de jeu :", SwingConstants.CENTER));
        String[] modes = {"Mode normal", "Mode île"};
        cboGameMod = new JComboBox(modes);
        cboGameMod.setSelectedIndex(0);
        /* a faire : add event listener --> on modifie le mode de jeu par rapport a ca*/

        centralPanel.add(cboGameMod);

        // Choix taille grille
        centralPanel.add(new JLabel("Taille de la grille :", SwingConstants.CENTER));
        String[] sizes = {"6x6", "7x7", "8x8", "9x9", "10x10"};
        cboGridSize = new JComboBox(sizes);
        cboGridSize.setSelectedIndex(0);
        /* a faire : add event listener --> on modifie la taille de la grille par rapport a ca*/
        centralPanel.add(cboGridSize);

        add(centralPanel, BorderLayout.CENTER);

        // boutons en bas de la page
        JPanel bottomPanel = new JPanel(new FlowLayout());

        JButton btnBack = new JButton("Retour");
        btnBack.addActionListener(e -> {
            new MenuView().setVisible(true);
            this.dispose();
        });

        JButton btnValidate = new JButton("Valider");
        btnValidate.addActionListener(e -> ouvrirVuePlacement());

        bottomPanel.add(btnBack);
        bottomPanel.add(btnValidate);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void ouvrirVuePlacement() {
        PlacementView placement = new PlacementView();
        placement.setVisible(true);
        this.dispose();
    }
}
