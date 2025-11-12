package view;

import javax.swing.*;
import java.awt.*;

public class VueMenu extends JFrame {

    public VueMenu() {
        setTitle("Bataille Navale - Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Titre
        JLabel titre = new JLabel("BATAILLE NAVALE", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 32));
        titre.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(titre, BorderLayout.NORTH);

        // au milieu : les boutons etc..
        JPanel panneauCentral = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton btnNouvellePartie = new JButton("Nouvelle Partie");
        btnNouvellePartie.setFont(new Font("Arial", Font.PLAIN, 18));
        btnNouvellePartie.setPreferredSize(new Dimension(250, 50));
        btnNouvellePartie.addActionListener(e -> ouvrirConfiguration());
        panneauCentral.add(btnNouvellePartie, gbc);

        gbc.gridy++;
        JButton btnQuitter = new JButton("Quitter");
        btnQuitter.setFont(new Font("Arial", Font.PLAIN, 18));
        btnQuitter.setPreferredSize(new Dimension(250, 50));
        btnQuitter.addActionListener(e -> System.exit(0));
        panneauCentral.add(btnQuitter, gbc);

        add(panneauCentral, BorderLayout.CENTER);
    }

    private void ouvrirConfiguration() {
        VueConfiguration config = new VueConfiguration();
        config.setVisible(true);
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VueMenu menu = new VueMenu();
            menu.setVisible(true);
        });
    }

}
