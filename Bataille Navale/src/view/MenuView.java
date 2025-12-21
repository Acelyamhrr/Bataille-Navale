package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Vue du menu principal.
 * expose seulement des méthodes
 * pour que le Controller puisse s'y connecter.
 */
public class MenuView extends JFrame {
    private JButton newGameButton;
    private JButton quitButton;

    public MenuView() {
        setTitle("Bataille Navale");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 50, 100));

        // Titre
        JLabel titleLabel = new JLabel("BATAILLE NAVALE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));

        // Panel des boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(new Color(30, 50, 100));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 150, 80, 150));

        newGameButton = createMenuButton("Nouvelle Partie");
        quitButton = createMenuButton("Quitter");

        buttonPanel.add(newGameButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(quitButton);

        JLabel versionLabel = new JLabel("A31 Bataille Navale - Açelya - Elora", SwingConstants.CENTER);
        versionLabel.setForeground(new Color(150, 170, 200));
        versionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(versionLabel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // pour avoir le même type de boutons à chaque fois.
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 100, 150));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // hover quand on passe dessus
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(90, 120, 180));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(70, 100, 150));
            }
        });
        return button;
    }

    // méthodes pour le controlleur

    public void addNewGameListener(ActionListener listener) {
        newGameButton.addActionListener(listener);
    }

    public void addQuitListener(ActionListener listener) {
        quitButton.addActionListener(listener);
    }

}