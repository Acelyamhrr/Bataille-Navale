package view;

import javax.swing.*;
import java.awt.*;

public class MenuView extends JFrame {

    public MenuView() {
        setTitle("Bataille Navale - Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // title
        JLabel title = new JLabel("BATAILLE NAVALE", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(title, BorderLayout.NORTH);

        // in the middle : buttons etc..
        JPanel centralPnl = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton btnNewGame = new JButton("Nouvelle Partie");
        btnNewGame.setFont(new Font("Arial", Font.PLAIN, 18));
        btnNewGame.setPreferredSize(new Dimension(250, 50));
        btnNewGame.addActionListener(e -> ouvrirConfiguration());
        centralPnl.add(btnNewGame, gbc);

        gbc.gridy++;
        JButton btnQuit = new JButton("Quitter");
        btnQuit.setFont(new Font("Arial", Font.PLAIN, 18));
        btnQuit.setPreferredSize(new Dimension(250, 50));
        btnQuit.addActionListener(e -> System.exit(0));
        centralPnl.add(btnQuit, gbc);

        add(centralPnl, BorderLayout.CENTER);
    }

    private void ouvrirConfiguration() {
        ConfigurationView config = new ConfigurationView();
        config.setVisible(true);
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuView menu = new MenuView();
            menu.setVisible(true);
        });
    }

}
