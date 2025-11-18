package view;

import javax.swing.*;
import java.awt.*;

public class EndView extends JFrame {

    private JLabel messageLabel;

    public EndView(String message) {
        setTitle("Bataille Navale - Fin de la partie");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 300);

        initComponents(message);
    }

    private void initComponents(String message) {
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("FIN DE LA PARTIE", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Result message
        messageLabel = new JLabel(message != null ? message : "Match nul / Résultat inconnu", SwingConstants.CENTER);       // results to display.
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(messageLabel, BorderLayout.CENTER);

        // buttons on the bottom of the panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton restartBtn = new JButton("Recommencer");
        restartBtn.setPreferredSize(new Dimension(140, 40));
        restartBtn.addActionListener(e -> {
            /* A FAIRE, pour l'instant, on retourne juste au menu principal */
            MenuView menu = new MenuView();
            menu.setVisible(true);
            this.dispose();
        });

        JButton quitBtn = new JButton("Quitter");
        quitBtn.setPreferredSize(new Dimension(140, 40));
        quitBtn.addActionListener(e -> System.exit(0));

        bottomPanel.add(restartBtn);
        bottomPanel.add(quitBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // pour test (lancer la vue fin sans avoir à jouer toute une partie)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EndView fin = new EndView("Le joueur X a gagné ! Score : 3 - 1");
            fin.setVisible(true);
        });
    }
}
