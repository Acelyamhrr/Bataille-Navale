package view;

import javax.swing.*;
import java.awt.*;

public class VueFin extends JFrame {

    private JLabel messageLabel;

    public VueFin(String message) {
        setTitle("Bataille Navale - Fin de la partie");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 300);

        initComponents(message);
    }

    private void initComponents(String message) {
        setLayout(new BorderLayout());

        // Titre
        JLabel titre = new JLabel("FIN DE LA PARTIE", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 28));
        titre.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titre, BorderLayout.NORTH);

        // Message de résultat
        messageLabel = new JLabel(message != null ? message : "Match nul / Résultat inconnu", SwingConstants.CENTER);       // résultats à afficher.
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(messageLabel, BorderLayout.CENTER);

        // Boutons bas
        JPanel panneauBas = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnRejouer = new JButton("Recommencer");
        btnRejouer.setPreferredSize(new Dimension(140, 40));
        btnRejouer.addActionListener(e -> {
            /* A FAIRE, pour l'instant, on retourne juste au menu principal */
            VueMenu menu = new VueMenu();
            menu.setVisible(true);
            this.dispose();
        });

        JButton btnQuitter = new JButton("Quitter");
        btnQuitter.setPreferredSize(new Dimension(140, 40));
        btnQuitter.addActionListener(e -> System.exit(0));

        panneauBas.add(btnRejouer);
        panneauBas.add(btnQuitter);

        add(panneauBas, BorderLayout.SOUTH);
    }

    // pour test (lancer la vue fin sans avoir à jouer toute une partie)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VueFin fin = new VueFin("Le joueur X a gagné ! Score : 3 - 1");
            fin.setVisible(true);
        });
    }
}
