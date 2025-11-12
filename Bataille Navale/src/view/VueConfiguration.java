package view;

import javax.swing.*;
import java.awt.*;

public class VueConfiguration extends JFrame {

    private JComboBox<String> comboModeJeu;
    private JComboBox<String> comboTailleGrille;

    public VueConfiguration() {
        setTitle("Configuration de la Partie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Titre
        JLabel titre = new JLabel("Configuration de la Partie", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 28));
        titre.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titre, BorderLayout.NORTH);

        JPanel panneauCentral = new JPanel();
        panneauCentral.setLayout(new GridLayout(4, 1, 10, 10));
        panneauCentral.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        // Choix mode
        panneauCentral.add(new JLabel("Mode de jeu :", SwingConstants.CENTER));
        String[] modes = {"Mode normal", "Mode île"};
        comboModeJeu = new JComboBox(modes);
        comboModeJeu.setSelectedIndex(0);
        /* a faire : add event listener --> on modifie le mode de jeu par rapport a ca*/

        panneauCentral.add(comboModeJeu);

        // Choix taille grille
        panneauCentral.add(new JLabel("Taille de la grille :", SwingConstants.CENTER));
        String[] tailles = {"6x6", "7x7", "8x8", "9x9", "10x10"};
        comboTailleGrille = new JComboBox(tailles);
        comboTailleGrille.setSelectedIndex(0);
        /* a faire : add event listener --> on modifie la taille de la grille par rapport a ca*/
        panneauCentral.add(comboTailleGrille);

        add(panneauCentral, BorderLayout.CENTER);

        // boutons en bas de la page
        JPanel panneauBas = new JPanel(new FlowLayout());

        JButton btnRetour = new JButton("Retour");
        btnRetour.addActionListener(e -> {
            new VueMenu().setVisible(true);
            this.dispose();
        });

        JButton btnValider = new JButton("Valider");
        btnValider.addActionListener(e -> ouvrirVuePlacement());

        panneauBas.add(btnRetour);
        panneauBas.add(btnValider);

        add(panneauBas, BorderLayout.SOUTH);
    }

    private void ouvrirVuePlacement() {
        VuePlacement placement = new VuePlacement();
        placement.setVisible(true);
        this.dispose();
    }
}
